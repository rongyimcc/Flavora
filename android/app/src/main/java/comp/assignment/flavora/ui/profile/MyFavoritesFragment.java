package comp.assignment.flavora.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import comp.assignment.flavora.R;
import comp.assignment.flavora.facade.PostFacade;
import comp.assignment.flavora.facade.PostInteractionFacade;
import comp.assignment.flavora.model.Post;
import comp.assignment.flavora.repository.AuthRepository;
import comp.assignment.flavora.ui.discover.PostsAdapter;
import comp.assignment.flavora.ui.post.PostDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * My Favorites Fragment
 * <p>
 * Displays a list of posts that the current user has favorited.
 * Users can view, like, and remove posts from their favorites.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Display all posts favorited by the current user</li>
 *   <li>Support for liking posts</li>
 *   <li>Support for unfavoriting (removes the post from the list)</li>
 *   <li>Navigate to post details by clicking on a post</li>
 *   <li>Automatically refresh data each time the fragment becomes visible</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 */
public class MyFavoritesFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    private RecyclerView recyclerView;

    private View progressBar;

    private TextView textEmpty;

    private PostsAdapter postsAdapter;

    /**
     * Create the fragment's view
     *
     * @param inflater LayoutInflater for inflating the layout
     * @param container Parent view container
     * @param savedInstanceState Saved instance state
     * @return The created root view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);


        // Initialize view references
        recyclerView = view.findViewById(R.id.recycler_view_posts);
        progressBar = view.findViewById(R.id.progress_bar);
        textEmpty = view.findViewById(R.id.text_empty);

        setupRecyclerView();

        return view;
    }

    /**
     * Called when the fragment resumes.
     * Reloads data each time the fragment becomes visible to ensure freshness.
     */
    @Override
    public void onResume() {
        super.onResume();
        // 当Fragment变为可见时重新加载数据
        loadMyFavorites();
    }

    /**
     * Set up the RecyclerView
     * Initializes the adapter and layout manager.
     */
    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postsAdapter);
    }

    /**
     * Load all favorited posts for the current user
     * <p>
     * Steps:
     * 1. Get the current user's ID
     * 2. Load all favorited posts from the database
     * 3. Load the user's like and favorite status
     * 4. Update each post's interaction state
     * 5. Display posts in the list
     * </p>
     */
    private void loadMyFavorites() {
        progressBar.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);

        String userId = AuthRepository.getInstance().getCurrentUserId();
        if (userId == null) {
            progressBar.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.setText("Not logged in");
            return;
        }

        // Load user's favorited posts
        PostFacade.getFavoritedPostsByUser(userId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                progressBar.setVisibility(View.GONE);
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText("Failed to load favorites");
                return;
            }

            List<Post> posts = task.getResult();

            // Load user interaction states (likes and favorites)）
            PostInteractionFacade.getLikedPostIds(likedTask -> {
                PostInteractionFacade.getFavoritedPostIds(favoritedTask -> {
                    progressBar.setVisibility(View.GONE);

                    List<String> likedPostIds = likedTask.isSuccessful() && likedTask.getResult() != null
                            ? likedTask.getResult() : new ArrayList<>();
                    List<String> favoritedPostIds = favoritedTask.isSuccessful() && favoritedTask.getResult() != null
                            ? favoritedTask.getResult() : new ArrayList<>();

                    java.util.Set<String> likedSet = new java.util.HashSet<>(likedPostIds);
                    java.util.Set<String> favoritedSet = new java.util.HashSet<>(favoritedPostIds);

                    for (Post post : posts) {
                        post.setLikedByCurrentUser(likedSet.contains(post.getPostId()));
                        post.setFavoritedByCurrentUser(favoritedSet.contains(post.getPostId()));
                    }

                    postsAdapter.setPosts(posts);

                    if (postsAdapter.getItemCount() == 0) {
                        textEmpty.setVisibility(View.VISIBLE);
                        textEmpty.setText("No favorites yet");
                    } else {
                        textEmpty.setVisibility(View.GONE);
                    }
                });
            });
        });
    }

    /**
     * Handle post click events
     * Navigates to the post detail screen.
     *
     * @param post The clicked post
     */
    @Override
    public void onPostClicked(Post post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getPostId());
        startActivity(intent);
    }

    /**
     * Handle like button click events
     * Toggles the like status of a post and updates its like count.
     *
     * @param post The post being liked/unliked
     * @param position The position of the post in the list
     */
    @Override
    public void onLikeClicked(Post post, int position) {
        // Toggle like status
        boolean currentStatus = post.isLikedByCurrentUser();
        PostInteractionFacade.toggleLike(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setLikedByCurrentUser(newStatus);

                // Update like count
                if (newStatus) {
                    post.setLikeCount(post.getLikeCount() + 1);
                } else {
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                }

                postsAdapter.updatePost(position, post);
            }
        });
    }

    /**
     * Handle favorite button click events
     * <p>
     * Special case: In the favorites list, if the user removes a favorite,
     * reload the list to remove the post from view.
     * (Normally, adding a favorite should not occur here since all posts are already favorited.)
     * </p>
     *
     * @param post The post being favorited/unfavorited
     * @param position The position of the post in the list
     */
    @Override
    public void onFavoriteClicked(Post post, int position) {
        // Toggle favorite status
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();

                if (!newStatus) {

                    loadMyFavorites();
                } else {
                    post.setFavoritedByCurrentUser(newStatus);
                    post.setFavoriteCount(post.getFavoriteCount() + 1);
                    postsAdapter.updatePost(position, post);
                }
            }
        });
    }

    /**
     * Called when the fragment's view is destroyed
     * Clears all view references to prevent memory leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear references to prevent memory leaks
        recyclerView = null;
        progressBar = null;
        textEmpty = null;
        postsAdapter = null;
    }
}
