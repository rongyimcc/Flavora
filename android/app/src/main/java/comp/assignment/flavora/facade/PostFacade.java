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
 * Facade class for post operations
 * <p>
 * This class provides a simplified API interface for post management, following the Facade design pattern.
 * Coordinates complex operations involving multiple DAOs, such as creating posts and updating user statistics.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Create and delete posts while updating user post counts</li>
 *   <li>Query post lists (by time, by user, by favorites, etc.)</li>
 *   <li>Retrieve individual post details</li>
 *   <li>Update post information</li>
 *   <li>Calculate total likes received by a user</li>
 * </ul>
 *
 * <p>This class uses static methods and can be used without instantiation. All methods use
 * asynchronous callback mechanism, returning operation results through {@link OnCompleteListener}.</p>
 *
 * @author Flavora Team
 * @version 1.0
 * @see PostDAO
 * @see UserDAO
 * @see Post
 */
public class PostFacade {

    /**
     * Create a new post and update the user's post count
     * <p>
     * This is a coordinated operation that ensures post creation and user statistics update
     * are executed as atomically as possible. This method automatically generates a post ID,
     * initializes like and favorite counts to 0, and records the current timestamp.
     * </p>
     *
     * <p>Operation flow:</p>
     * <ol>
     *   <li>Validate required parameters (user ID and title)</li>
     *   <li>Generate a unique post ID (UUID)</li>
     *   <li>Create Post object and initialize all fields</li>
     *   <li>Add the post to the database</li>
     *   <li>If addition is successful, increment the user's post count</li>
     *   <li>Return the newly created post ID through callback</li>
     * </ol>
     *
     * <p>Note: Even if the user count update fails, success will be returned with the post ID.
     * This design ensures post creation is not rolled back due to statistics update failure.</p>
     *
     * @param userId        User ID (post author)
     * @param username      Username (for data denormalization to avoid extra queries)
     * @param userAvatarUrl User avatar URL (for data denormalization)
     * @param title         Post title (required)
     * @param description   Post description content
     * @param imageUrls     List of image URLs
     * @param rating        Rating (1-5 points)
     * @param listener      Completion callback, returns newly created post ID on success, exception on failure
     */
    public static void createPost(String userId, String username, String userAvatarUrl,
                                  String title, String description, List<String> imageUrls,
                                  double rating, OnCompleteListener<String> listener) {

        if (userId == null || title == null || title.trim().isEmpty()) {
            listener.onComplete(Tasks.forException(
                    new IllegalArgumentException("User ID and title are required")));
            return;
        }


        String postId = UUID.randomUUID().toString();


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
                0,  // Initial like count
                0   // Initial favorite count
        );


        PostDAO.getInstance().add(post, task -> {
            if (task.isSuccessful()) {

                UserDAO.getInstance().incrementPostsCount(userId, updateTask -> {
                    if (updateTask.isSuccessful()) {
                        listener.onComplete(Tasks.forResult(postId));
                    } else {
                        // Post created but count update failed
                        // Rollback logic can be implemented here if needed
                        listener.onComplete(Tasks.forResult(postId));
                    }
                });
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }


    /**
     * Delete a post and update the user's post count
     * <p>
     * This method first deletes the specified post from the database. If deletion is successful
     * and a user ID is provided, it will automatically decrement that user's post count.
     * </p>
     *
     * @param postId   ID of the post to delete (required)
     * @param userId   User ID (for updating count, if null only deletes post without updating count)
     * @param listener Completion callback, returns null on success, exception on failure
     */
    public static void deletePost(String postId, String userId,
                                  OnCompleteListener<Void> listener) {
        // Validate post ID
        if (postId == null) {
            listener.onComplete(Tasks.forException(
                    new IllegalArgumentException("Post ID is required")));
            return;
        }

        // Delete post
        PostDAO.getInstance().delete(postId, task -> {
            if (task.isSuccessful() && userId != null) {
                // Decrement user's post count
                UserDAO.getInstance().decrementPostsCount(userId, listener);
            } else {
                listener.onComplete(task);
            }
        });
    }

    /**
     * Get all posts ordered by creation time descending (newest first)
     *
     * @param listener Completion callback, returns list of posts
     */
    public static void getAllPosts(OnCompleteListener<List<Post>> listener) {
        PostDAO.getInstance().getPostsOrderByTime(listener);
    }

    /**
     * Get all posts created by a specified user
     * <p>
     * If user ID is null, will return an empty list rather than throwing an exception.
     * </p>
     *
     * @param userId   User ID
     * @param listener Completion callback, returns list of posts by this user
     */
    public static void getUserPosts(String userId, OnCompleteListener<List<Post>> listener) {
        if (userId == null) {
            listener.onComplete(Tasks.forResult(List.of()));
            return;
        }

        PostDAO.getInstance().getPostsByUser(userId, listener);
    }

    /**
     * Get all posts created by a specified user (alias method for getUserPosts)
     * <p>
     * This method has identical functionality to {@link #getUserPosts(String, OnCompleteListener)}.
     * This alias is provided for code readability and backward compatibility.
     * </p>
     *
     * @param userId   User ID
     * @param listener Completion callback, returns list of posts by this user
     */
    public static void getPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        getUserPosts(userId, listener);
    }

    /**
     * Get all posts favorited by a specified user
     * <p>
     * Returns a list of all posts marked as favorites by this user. If user ID is null, returns empty list.
     * </p>
     *
     * @param userId   User ID
     * @param listener Completion callback, returns list of posts favorited by the user
     */
    public static void getFavoritedPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        if (userId == null) {
            listener.onComplete(Tasks.forResult(List.of()));
            return;
        }

        PostDAO.getInstance().getFavoritedPostsByUser(userId, listener);
    }

    /**
     * Get the total number of likes received on all posts by a specified user
     * <p>
     * This method calculates the sum of all likes received on posts created by the user,
     * which can be used to display the user's popularity. If user ID is null, returns 0.
     * </p>
     *
     * @param userId   User ID
     * @param listener Completion callback, returns total number of likes
     */
    public static void getTotalLikesForUser(String userId, OnCompleteListener<Integer> listener) {
        if (userId == null) {
            listener.onComplete(Tasks.forResult(0));
            return;
        }

        PostDAO.getInstance().getTotalLikesForUser(userId, listener);
    }

    /**
     * Get detailed information for a single post by ID
     *
     * @param postId   Post ID (required)
     * @param listener Completion callback, returns Post object, or null if it does not exist
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
     * Update information for an existing post
     * <p>
     * Updates the post record in the database using the provided Post object.
     * Note: The post ID must exist and cannot be null.
     * </p>
     *
     * @param post     Post object containing updated data (required, and postId cannot be null)
     * @param listener Completion callback, returns null on success, exception on failure
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