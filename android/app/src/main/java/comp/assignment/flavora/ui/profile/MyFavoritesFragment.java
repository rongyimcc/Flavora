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
 * Fragment displaying posts favorited by the current user.
 * <p>
 * Allows viewing, liking, and removing favorites. Data refreshes whenever the fragment becomes visible.
 * </p>
 *
 * @version 1.0
 */
public class MyFavoritesFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    /** RecyclerView displaying the list of favorites. */
    private RecyclerView recyclerView;

    /** Progress bar shown while loading. */
    private View progressBar;

    /** Empty-state text shown when no favorites exist. */
    private TextView textEmpty;

    /** Adapter used to render the posts. */
    private PostsAdapter postsAdapter;

    /**
     * Inflates the fragment view.
     *
     * @param inflater LayoutInflater for inflation.
     * @param container Parent container.
     * @param savedInstanceState Saved state bundle.
     * @return Root view instance.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // Initialize view references.
        recyclerView = view.findViewById(R.id.recycler_view_posts);
        progressBar = view.findViewById(R.id.progress_bar);
        textEmpty = view.findViewById(R.id.text_empty);

        setupRecyclerView();

        return view;
    }

    /**
     * Reloads the data whenever the fragment resumes.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Reload data when the fragment becomes visible.
        loadMyFavorites();
    }

    /**
     * Configures the RecyclerView, adapter, and layout manager.
     */
    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postsAdapter);
    }

    /**
     * Loads all posts favorited by the current user and updates interaction states.
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

        // Fetch favorites for the user.
        PostFacade.getFavoritedPostsByUser(userId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                progressBar.setVisibility(View.GONE);
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText("Failed to load favorites");
                return;
            }

            List<Post> posts = task.getResult();

            // Load like/favorite interaction states.
            PostInteractionFacade.getLikedPostIds(likedTask -> {
                PostInteractionFacade.getFavoritedPostIds(favoritedTask -> {
                    progressBar.setVisibility(View.GONE);

                    List<String> likedPostIds = likedTask.isSuccessful() && likedTask.getResult() != null
                            ? likedTask.getResult() : new ArrayList<>();
                    List<String> favoritedPostIds = favoritedTask.isSuccessful() && favoritedTask.getResult() != null
                            ? favoritedTask.getResult() : new ArrayList<>();

                    // Convert to sets for faster lookups.
                    java.util.Set<String> likedSet = new java.util.HashSet<>(likedPostIds);
                    java.util.Set<String> favoritedSet = new java.util.HashSet<>(favoritedPostIds);

                    // Update each post with the user's interaction state.
                    for (Post post : posts) {
                        post.setLikedByCurrentUser(likedSet.contains(post.getPostId()));
                        post.setFavoritedByCurrentUser(favoritedSet.contains(post.getPostId()));
                    }

                    postsAdapter.setPosts(posts);

                    // Show empty state when no favorites remain.
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
     * Opens the post detail screen when a post is tapped.
     *
     * @param post Selected post.
     */
    @Override
    public void onPostClicked(Post post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getPostId());
        startActivity(intent);
    }

    /**
     * Handles like button interactions and updates counts.
     *
     * @param post Post being liked/unliked.
     * @param position Item position.
     */
    @Override
    public void onLikeClicked(Post post, int position) {
        // Toggle like state.
        boolean currentStatus = post.isLikedByCurrentUser();
        PostInteractionFacade.toggleLike(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setLikedByCurrentUser(newStatus);

                // Adjust like count.
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
     * Handles favorite button interactions. When a favorite is removed, the list is reloaded
     * to drop the post from the favorites view.
     *
     * @param post Post being favorited/unfavorited.
     * @param position Item position.
     */
    @Override
    public void onFavoriteClicked(Post post, int position) {
        // Toggle favorite state.
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();

                // If unfavorited, reload to remove the item; otherwise just refresh counts.
                if (!newStatus) {
                    // Reload to remove the unfavorited post.
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
     * Clears view references when the fragment view is destroyed to prevent leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release view references to avoid leaks.
        recyclerView = null;
        progressBar = null;
        textEmpty = null;
        postsAdapter = null;
    }
}
