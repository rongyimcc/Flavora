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
 * My Posts Fragment
 * <p>
 * Displays all posts published by the current user.
 * Users can view, like, favorite, and delete their own posts.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Display all posts created by the current user</li>
 *   <li>Support liking and favoriting operations</li>
 *   <li>Allow deleting user’s own posts (with confirmation dialog)</li>
 *   <li>Navigate to post details by clicking on a post</li>
 *   <li>Automatically refresh data each time the fragment becomes visible</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 */
public class MyPostsFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    private RecyclerView recyclerView;

    private View progressBar;

    private TextView textEmpty;

    private PostsAdapter postsAdapter;

    /**
     * Create the fragment view
     *
     * @param inflater LayoutInflater used to inflate the layout
     * @param container Parent view container
     * @param savedInstanceState Saved instance state
     * @return The created root view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_list, container, false);

        // 初始化视图引用
        recyclerView = view.findViewById(R.id.recycler_view_posts);
        progressBar = view.findViewById(R.id.progress_bar);
        textEmpty = view.findViewById(R.id.text_empty);

        setupRecyclerView();

        return view;
    }

    /**
     * Called when the fragment resumes.
     * Reloads the data each time the fragment becomes visible to ensure the list is up to date.
     */
    @Override
    public void onResume() {
        super.onResume();
        // Reload data when the fragment becomes visible
        loadMyPosts();
    }

    /**
     * Set up the RecyclerView
     * <p>
     * Initializes the adapter and layout manager, and enables the delete button
     * since this fragment shows the user's own posts.
     * </p>
     */
    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        postsAdapter.setShowDeleteButton(true); // 为"我的帖子"启用删除按钮
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postsAdapter);
    }

    /**
     * Load all posts created by the current user
     * <p>
     * Steps:
     * 1. Get the current logged-in user's ID
     * 2. Fetch all posts published by that user
     * 3. Load the user's like and favorite states
     * 4. Update each post's interaction status
     * 5. Display the posts in the list
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

        // Load posts published by the user
        PostFacade.getPostsByUser(userId, task -> {
            if (!task.isSuccessful() || task.getResult() == null) {
                progressBar.setVisibility(View.GONE);
                textEmpty.setVisibility(View.VISIBLE);
                textEmpty.setText("Failed to load posts");
                return;
            }

            List<Post> posts = task.getResult();

            // Load user interaction states (likes and favorites)
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
                        textEmpty.setText("No posts yet");
                    } else {
                        textEmpty.setVisibility(View.GONE);
                    }
                });
            });
        });
    }

    /**
     * Handle post click event
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
     * Handle like button click event
     * Toggles the like status of a post and updates the like count.
     *
     * @param post The post being liked or unliked
     * @param position The post's position in the list
     */
    @Override
    public void onLikeClicked(Post post, int position) {
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
     * Handle favorite button click event
     * Toggles the favorite status of a post and updates the favorite count.
     *
     * @param post The post being favorited or unfavorited
     * @param position The post's position in the list
     */
    @Override
    public void onFavoriteClicked(Post post, int position) {
        // Toggle favorite status
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setFavoritedByCurrentUser(newStatus);

                // Update favorite count
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
     * Handle delete button click event
     * <p>
     * Displays a confirmation dialog. If confirmed, deletes the post.
     * </p>
     *
     * @param post The post to delete
     * @param position The post's position in the list
     */
    @Override
    public void onDeleteClicked(Post post, int position) {
        // Show confirmation dialog
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Post")
                .setMessage("Are you sure you want to delete this post? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete the post
                    String userId = AuthRepository.getInstance().getCurrentUserId();
                    if (userId == null) {
                        Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Use PostFacade to delete the post
                    PostFacade.deletePost(post.getPostId(), userId, task -> {
                        if (task.isSuccessful()) {
                            // 从适配器中移除
                            postsAdapter.removePost(position);
                            Toast.makeText(getContext(), "Post deleted", Toast.LENGTH_SHORT).show();

                            // 如果没有剩余帖子，显示空状态提示
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
