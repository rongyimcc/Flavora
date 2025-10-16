package comp.assignment.flavora.facade;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.repository.AuthRepository;

/**
 * Facade class for post interaction operations
 * <p>
 * This class provides a simplified API interface for managing user interactions with posts (likes, favorites).
 * All operations are based on the currently logged-in user, with user identity obtained through {@link AuthRepository}.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Toggle post like status (like/unlike)</li>
 *   <li>Toggle post favorite status (favorite/unfavorite)</li>
 *   <li>Check if current user has liked/favorited a post</li>
 *   <li>Get list of all post IDs liked/favorited by current user</li>
 * </ul>
 *
 * <p>This class uses static methods and can be used without instantiation. All methods use
 * asynchronous callback mechanism, returning operation results through {@link OnCompleteListener}.</p>
 *
 * <p>Note: All interaction operations require the user to be logged in, otherwise an exception or default value will be returned.</p>
 *
 * @author Flavora Team
 * @version 1.0
 * @see FirebaseDataSource
 * @see AuthRepository
 */
public class PostInteractionFacade {

    /** Firebase data source instance for executing underlying database operations */
    private static final FirebaseDataSource dataSource = FirebaseDataSource.getInstance();

    /** Authentication repository instance for obtaining current logged-in user information */
    private static final AuthRepository authRepository = AuthRepository.getInstance();

    /**
     * Toggle the current user's like status for a post
     * <p>
     * If the post is already liked, it will be unliked; if not liked, it will be liked.
     * This operation automatically updates the post's like count.
     * </p>
     *
     * <p>Operation flow:</p>
     * <ol>
     *   <li>Get current logged-in user ID</li>
     *   <li>Verify if user is logged in</li>
     *   <li>Execute like or unlike operation based on current status</li>
     *   <li>Synchronously update the post's like count</li>
     *   <li>Return new like status</li>
     * </ol>
     *
     * @param postId   Post ID
     * @param isLiked  Current like status (true means already liked, false means not liked)
     * @param listener Completion callback, returns new status after operation (true=liked, false=unliked)
     */
    public static void toggleLike(String postId, boolean isLiked, OnCompleteListener<Boolean> listener) {
        // Get current logged-in user ID
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forException(new IllegalStateException("User not logged in")));
            return;
        }

        if (isLiked) {
            // Unlike: remove like record and decrement count
            dataSource.removeLikeAndDecrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(false)); // Now unliked
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        } else {
            // Like: add like record and increment count
            dataSource.addLikeAndIncrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(true)); // Now liked
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        }
    }

    /**
     * Toggle the current user's favorite status for a post
     * <p>
     * If the post is already favorited, it will be unfavorited; if not favorited, it will be favorited.
     * This operation automatically updates the post's favorite count.
     * </p>
     *
     * <p>Operation flow:</p>
     * <ol>
     *   <li>Get current logged-in user ID</li>
     *   <li>Verify if user is logged in</li>
     *   <li>Execute favorite or unfavorite operation based on current status</li>
     *   <li>Synchronously update the post's favorite count</li>
     *   <li>Return new favorite status</li>
     * </ol>
     *
     * @param postId      Post ID
     * @param isFavorited Current favorite status (true means already favorited, false means not favorited)
     * @param listener    Completion callback, returns new status after operation (true=favorited, false=unfavorited)
     */
    public static void toggleFavorite(String postId, boolean isFavorited, OnCompleteListener<Boolean> listener) {
        // Get current logged-in user ID
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forException(new IllegalStateException("User not logged in")));
            return;
        }

        if (isFavorited) {
            // Unfavorite: remove favorite record and decrement count
            dataSource.removeFavoriteAndDecrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(false)); // Now unfavorited
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        } else {
            // Favorite: add favorite record and increment count
            dataSource.addFavoriteAndIncrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(true)); // Now favorited
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        }
    }

    /**
     * Check if the current user has liked a specified post
     * <p>
     * Queries the database for whether a like record exists for the current user on this post.
     * If the user is not logged in, directly returns false.
     * </p>
     *
     * @param postId   Post ID
     * @param listener Completion callback, returns like status (true=liked, false=not liked)
     */
    public static void checkIfLiked(String postId, OnCompleteListener<Boolean> listener) {
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forResult(false));
            return;
        }
        dataSource.checkLikeExists(userId, postId, listener);
    }

    /**
     * Check if the current user has favorited a specified post
     * <p>
     * Queries the database for whether a favorite record exists for the current user on this post.
     * If the user is not logged in, directly returns false.
     * </p>
     *
     * @param postId   Post ID
     * @param listener Completion callback, returns favorite status (true=favorited, false=not favorited)
     */
    public static void checkIfFavorited(String postId, OnCompleteListener<Boolean> listener) {
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forResult(false));
            return;
        }
        dataSource.checkFavoriteExists(userId, postId, listener);
    }

    /**
     * Get list of all post IDs liked by the current user
     * <p>
     * Returns the collection of IDs for all posts liked by the currently logged-in user.
     * If the user is not logged in, returns an empty list.
     * </p>
     *
     * <p>Usage examples:</p>
     * <ul>
     *   <li>Batch mark liked status in post lists</li>
     *   <li>Implement "Posts I Liked" feature page</li>
     *   <li>Track user like behavior</li>
     * </ul>
     *
     * @param listener Completion callback, returns list of post IDs
     */
    public static void getLikedPostIds(OnCompleteListener<java.util.List<String>> listener) {
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forResult(java.util.Collections.emptyList()));
            return;
        }
        dataSource.getLikesByUser(userId, listener);
    }

    /**
     * Get list of all post IDs favorited by the current user
     * <p>
     * Returns the collection of IDs for all posts favorited by the currently logged-in user.
     * If the user is not logged in, returns an empty list.
     * </p>
     *
     * <p>Usage examples:</p>
     * <ul>
     *   <li>Batch mark favorited status in post lists</li>
     *   <li>Implement "My Favorites" feature page</li>
     *   <li>Track user favorite behavior</li>
     * </ul>
     *
     * @param listener Completion callback, returns list of post IDs
     */
    public static void getFavoritedPostIds(OnCompleteListener<java.util.List<String>> listener) {
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forResult(java.util.Collections.emptyList()));
            return;
        }
        dataSource.getFavoritesByUser(userId, listener);
    }
}