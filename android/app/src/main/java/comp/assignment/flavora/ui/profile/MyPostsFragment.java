package comp.assignment.flavora.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
 * Fragment displaying posts authored by the current user.
 * <p>
 * Users can view, like, favorite, and delete their own posts.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Shows every post created by the logged-in user.</li>
 *   <li>Supports like and favorite interactions.</li>
 *   <li>Allows deleting posts with confirmation.</li>
 *   <li>Opens the detail page when a post is tapped.</li>
 *   <li>Refreshes data whenever the fragment becomes visible.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 */
public class MyPostsFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    /** RecyclerView displaying the post list. */
    private RecyclerView recyclerView;

    /** Progress bar shown during loading. */
    private View progressBar;

    /** Empty-state text shown when there are no posts. */
    private TextView textEmpty;

    /** Adapter for the post list. */
    private PostsAdapter postsAdapter;

    /**
     * Inflates the fragment view.
     *
     * @param inflater LayoutInflater for inflation.
     * @param container Parent container.
     * @param savedInstanceState Saved state bundle.
     * @return Root view for the fragment.
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
     * Reloads data whenever the fragment resumes to keep content fresh.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Reload data when the fragment is visible again.
        loadMyPosts();
    }

    /**
     * Configures the RecyclerView, adapter, and delete-button behavior.
     */
    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        postsAdapter.setShowDeleteButton(true); // Enable delete button for "My Posts".
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postsAdapter);
    }

    /**
     * Loads all posts created by the current user.
     * <p>
     * Steps:
     * 1. Retrieve the user ID.
     * 2. Fetch the user's posts.
     * 3. Load like and favorite states.
     * 4. Update each post with the interaction state.
     * 5. Display the results.
     * </p>
     */
    private void loadMyPosts() {
        progressBar.setVisibility(View.VISIBLE);
        textEmpty.setVisibility(View.GONE);

        String userId = AuthRepository.getInstance().getCurrentUserId();
        if (userId == null) {
            progressBar.setVisibility(View.GONE);
            textEmpty.setVisibility(View.VISIBLE);
            textEmpty.setText("Not logged in");
            return;
        }

        // Fetch posts authored by the user.
        PostFacade.getPostsByUser(userId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                progressBar.setVisibility(View.GONE);
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText("Failed to load posts");
                return;
            }

            List<Post> posts = task.getResult();

            // Load interaction states (likes and favorites).
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

                    // Show empty state if there are no posts.
                    if (postsAdapter.getItemCount() == 0) {
                        textEmpty.setVisibility(View.VISIBLE);
                        textEmpty.setText("No posts yet");
                    } else {
                        textEmpty.setVisibility(View.GONE);
                    }
                });
            });
        });
    }

    /**
     * Handles post taps by navigating to the detail screen.
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
     * Handles like button taps; toggles the state and updates the count.
     *
     * @param post Post being liked/unliked.
     * @param position Position in the list.
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
     * Handles favorite button taps; toggles the state and updates the count.
     *
     * @param post Post being favorited/unfavorited.
     * @param position Position in the list.
     */
    @Override
    public void onFavoriteClicked(Post post, int position) {
        // Toggle favorite state.
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setFavoritedByCurrentUser(newStatus);

                // Adjust favorite count.
                if (newStatus) {
                    post.setFavoriteCount(post.getFavoriteCount() + 1);
                } else {
                    post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
                }

                postsAdapter.updatePost(position, post);
            }
        });
    }

    /**
     * Handles delete button taps by confirming and removing the post.
     *
     * @param post Post to delete.
     * @param position Position in the list.
     */
    @Override
    public void onDeleteClicked(Post post, int position) {
        // Show confirmation dialog.
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the post.
                    String userId = AuthRepository.getInstance().getCurrentUserId();
                    if (userId == null) {
                        Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Delegate to the facade to remove the post.
                    PostFacade.deletePost(post.getPostId(), userId, task -> {
                        if (task.isSuccessful()) {
                            // Remove from the adapter.
                            postsAdapter.removePost(position);
                            Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();

                            // Show empty state if no posts remain.
                            if (postsAdapter.getItemCount() == 0) {
                                textEmpty.setVisibility(View.VISIBLE);
                                textEmpty.setText("No posts yet");
                            }
                        } else {
                            Toast.makeText(getContext(), "Failed to delete post", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Clears view references when the fragment view is destroyed to avoid leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Release view references to prevent leaks.
        recyclerView = null;
        progressBar = null;
        textEmpty = null;
        postsAdapter = null;
    }
}
