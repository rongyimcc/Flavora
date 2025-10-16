package comp.assignment.flavora.facade;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import comp.assignment.flavora.dao.PostDAO;
import comp.assignment.flavora.dao.UserDAO;
import comp.assignment.flavora.model.Post;

import java.util.List;
import java.util.UUID;

/**
 * Facade for post operations.
 * <p>
 * Provides a simplified API for managing posts, following the facade pattern.
 * Coordinates multi-DAO workflows such as creating posts while updating user statistics.
 * </p>
 *
 * <p>Main capabilities:</p>
 * <ul>
 *   <li>Create and delete posts while keeping the user's post count in sync.</li>
 *   <li>Query posts (by time, by user, by favorites, etc.).</li>
 *   <li>Fetch single post details.</li>
 *   <li>Update post information.</li>
 *   <li>Aggregate total likes earned by a user.</li>
 * </ul>
 *
 * <p>This class exposes only static methods. Results are returned asynchronously through
 * {@link OnCompleteListener} callbacks.</p>
 *
 * @author Flavora Team
 * @version 1.0
 * @see PostDAO
 * @see UserDAO
 * @see Post
 */
public class PostFacade {

    /**
     * Creates a post and updates the author's post count.
     * <p>
     * Coordinates post creation with user statistics to keep data consistent.
     * Generates the post ID, initializes like/favorite counts to zero, and records the current timestamp.
     * </p>
     *
     * <p>Workflow:</p>
     * <ol>
     *   <li>Validate required parameters (user ID and title).</li>
     *   <li>Generate a unique post ID (UUID).</li>
     *   <li>Construct the Post object with all fields initialized.</li>
     *   <li>Add the post to the database.</li>
     *   <li>If insertion succeeds, increment the user's post count.</li>
     *   <li>Return the new post ID through the callback.</li>
     * </ol>
     *
     * <p>Note: even if the count update fails, the post ID is still returned.
     * This avoids rolling back the post creation when stats fail.</p>
     *
     * @param userId        Author's user ID.
     * @param username      Username (denormalized for display).
     * @param userAvatarUrl Avatar URL (denormalized for display).
     * @param title         Post title (required).
     * @param description   Post description.
     * @param imageUrls     Image URL list.
     * @param rating        Rating (1-5).
     * @param listener      Completion listener; returns the new post ID on success or the exception on failure.
     */
    public static void createPost(String userId, String username, String userAvatarUrl,
                                  String title, String description, List<String> imageUrls,
                                  double rating, OnCompleteListener<String> listener) {
        // Validate required parameters.
        if (userId == null || title == null || title.trim().isEmpty()) {
            listener.onComplete(Tasks.forException(
                    new IllegalArgumentException("User ID and title are required")));
            return;
        }

        // Generate a unique post ID.
        String postId = UUID.randomUUID().toString();

        // Build the post object with zeroed like and favorite counts.
        Post post = new Post(
                postId,
                userId,
                username,
                userAvatarUrl,
                title,
                description,
                imageUrls,
                rating,
                Timestamp.now(),
                0,  // Initial like count.
                0   // Initial favorite count.
        );

        // Add the post to the database.
        PostDAO.getInstance().add(post, task -> {
            if (task.isSuccessful()) {
                // Update the user's post count.
                UserDAO.getInstance().incrementPostsCount(userId, updateTask -> {
                    if (updateTask.isSuccessful()) {
                        listener.onComplete(Tasks.forResult(postId));
                    } else {
                        // Post created but the count update failed.
                        // Implement rollback here if desired.
                        listener.onComplete(Tasks.forResult(postId));
                    }
                });
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Deletes a post and decrements the author's post count.
     * <p>
     * Removes the specified post; when a user ID is provided, also decrements that user's post count.
     * </p>
     *
     * @param postId   Post ID to remove (required).
     * @param userId   User ID for adjusting the post count; if null, only the post is deleted.
     * @param listener Completion listener returning null on success or an exception on failure.
     */
    public static void deletePost(String postId, String userId,
                                  OnCompleteListener<Void> listener) {
        // Validate the post ID.
        if (postId == null) {
            listener.onComplete(Tasks.forException(
                    new IllegalArgumentException("Post ID is required")));
            return;
        }

        // Delete the post.
        PostDAO.getInstance().delete(postId, task -> {
            if (task.isSuccessful() && userId != null) {
                // Decrement the user's post count.
                UserDAO.getInstance().decrementPostsCount(userId, listener);
            } else {
                listener.onComplete(task);
            }
        });
    }

    /**
     * Retrieves all posts ordered by creation time (newest first).
     *
     * @param listener Completion listener returning the post list.
     */
    public static void getAllPosts(OnCompleteListener<List<Post>> listener) {
        PostDAO.getInstance().getPostsOrderByTime(listener);
    }

    /**
     * Retrieves every post created by the specified user.
     * <p>
     * Returns an empty list when the user ID is null instead of throwing.
     * </p>
     *
     * @param userId   User ID.
     * @param listener Completion listener returning the user's posts.
     */
    public static void getUserPosts(String userId, OnCompleteListener<List<Post>> listener) {
        if (userId == null) {
            listener.onComplete(Tasks.forResult(List.of()));
            return;
        }

        PostDAO.getInstance().getPostsByUser(userId, listener);
    }

    /**
     * Alias for {@link #getUserPosts(String, OnCompleteListener)}.
     * <p>
     * Provided for readability and backward compatibility.
     * </p>
     *
     * @param userId   User ID.
     * @param listener Completion listener returning the user's posts.
     */
    public static void getPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        getUserPosts(userId, listener);
    }

    /**
     * Retrieves every post the user has favorited.
     * <p>
     * Returns an empty list when the user ID is null.
     * </p>
     *
     * @param userId   User ID.
     * @param listener Completion listener returning the user's favorite posts.
     */
    public static void getFavoritedPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        if (userId == null) {
            listener.onComplete(Tasks.forResult(List.of()));
            return;
        }

        PostDAO.getInstance().getFavoritedPostsByUser(userId, listener);
    }

    /**
     * Retrieves the total likes earned across all posts by the user.
     * <p>
     * Useful for showing popularity metrics. Returns 0 when the user ID is null.
     * </p>
     *
     * @param userId   User ID.
     * @param listener Completion listener returning the total like count.
     */
    public static void getTotalLikesForUser(String userId, OnCompleteListener<Integer> listener) {
        if (userId == null) {
            listener.onComplete(Tasks.forResult(0));
            return;
        }

        PostDAO.getInstance().getTotalLikesForUser(userId, listener);
    }

    /**
     * Retrieves the post details for the given ID.
     *
     * @param postId   Post ID (required).
     * @param listener Completion listener returning the post or null if it does not exist.
     */
    public static void getPost(String postId, OnCompleteListener<Post> listener) {
        if (postId == null) {
            listener.onComplete(Tasks.forException(
                    new IllegalArgumentException("Post ID is required")));
            return;
        }

        PostDAO.getInstance().get(postId, listener);
    }

    /**
     * Updates an existing post.
     * <p>
     * Persists the provided Post entity. The post must already have an ID.
     * </p>
     *
     * @param post     Post containing updated data (postId must be non-null).
     * @param listener Completion listener returning null on success or an exception on failure.
     */
    public static void updatePost(Post post, OnCompleteListener<Void> listener) {
        if (post == null || post.getPostId() == null) {
            listener.onComplete(Tasks.forException(
                    new IllegalArgumentException("Post and post ID are required")));
            return;
        }

        PostDAO.getInstance().update(post, listener);
    }
}
