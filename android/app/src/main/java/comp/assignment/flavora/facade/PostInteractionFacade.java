package comp.assignment.flavora.facade;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.repository.AuthRepository;

/**
 * Facade for post interaction operations.
 * <p>
 * Provides a simplified API for managing likes and favorites tied to the current user
 * (retrieved via {@link AuthRepository}).
 * </p>
 *
 * <p>Main capabilities:</p>
 * <ul>
 *   <li>Toggle like state for a post.</li>
 *   <li>Toggle favorite state for a post.</li>
 *   <li>Check whether the current user liked or favorited a post.</li>
 *   <li>Fetch the lists of post IDs liked or favorited by the user.</li>
 * </ul>
 *
 * <p>All methods are static and return results through {@link OnCompleteListener} callbacks.</p>
 *
 * <p>Note: an authenticated user is required; otherwise an exception or default value is returned.</p>
 *
 * @author Flavora Team
 * @version 1.0
 * @see FirebaseDataSource
 * @see AuthRepository
 */
public class PostInteractionFacade {

    /** Firebase data source used for underlying database operations. */
    private static final FirebaseDataSource dataSource = FirebaseDataSource.getInstance();

    /** Auth repository used to obtain the current user's information. */
    private static final AuthRepository authRepository = AuthRepository.getInstance();

    /**
     * Toggles the like state for the current user and post.
     * <p>
     * Removes the like if it already exists; otherwise adds it. Automatically keeps the like count in sync.
     * </p>
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Fetch the current user ID.</li>
     *   <li>Ensure the user is logged in.</li>
     *   <li>Add or remove the like based on the current state.</li>
     *   <li>Update the post's like count.</li>
     *   <li>Return the new like state.</li>
     * </ol>
     *
     * @param postId   Post ID.
     * @param isLiked  Current like state (true when liked).
     * @param listener Completion listener returning the new like state.
     */
    public static void toggleLike(String postId, boolean isLiked, OnCompleteListener<Boolean> listener) {
        // Fetch the current user ID.
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forException(new IllegalStateException("User not logged in")));
            return;
        }

        if (isLiked) {
            // Unlike: remove the record and decrement the count.
            dataSource.removeLikeAndDecrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(false)); // Now not liked.
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        } else {
            // Like: add the record and increment the count.
            dataSource.addLikeAndIncrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(true)); // Now liked.
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        }
    }

    /**
     * Toggles the favorite state for the current user and post.
     * <p>
     * Removes the favorite if it already exists; otherwise adds it. Keeps the favorite count up to date.
     * </p>
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Fetch the current user ID.</li>
     *   <li>Ensure the user is logged in.</li>
     *   <li>Add or remove the favorite depending on the state.</li>
     *   <li>Update the post's favorite count.</li>
     *   <li>Return the new favorite state.</li>
     * </ol>
     *
     * @param postId      Post ID.
     * @param isFavorited Current favorite state (true when favorited).
     * @param listener    Completion listener returning the new favorite state.
     */
    public static void toggleFavorite(String postId, boolean isFavorited, OnCompleteListener<Boolean> listener) {
        // Fetch the current user ID.
        String userId = authRepository.getCurrentUserId();
        if (userId == null) {
            listener.onComplete(Tasks.forException(new IllegalStateException("User not logged in")));
            return;
        }

        if (isFavorited) {
            // Unfavorite: remove the record and decrement the count.
            dataSource.removeFavoriteAndDecrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(false)); // Now not favorited.
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        } else {
            // Favorite: add the record and increment the count.
            dataSource.addFavoriteAndIncrementCount(userId, postId, task -> {
                if (task.isSuccessful()) {
                    listener.onComplete(Tasks.forResult(true)); // Now favorited.
                } else {
                    listener.onComplete(Tasks.forException(task.getException()));
                }
            });
        }
    }

    /**
     * Checks whether the current user liked the given post.
     * <p>
     * Looks up the like record; returns false when no user is logged in.
     * </p>
     *
     * @param postId   Post ID.
     * @param listener Completion listener returning the like state.
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
     * Checks whether the current user favorited the given post.
     * <p>
     * Looks up the favorite record; returns false when no user is logged in.
     * </p>
     *
     * @param postId   Post ID.
     * @param listener Completion listener returning the favorite state.
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
     * Retrieves the IDs of every post the user has liked.
     * <p>
     * Returns an empty list when no user is logged in.
     * </p>
     *
     * <p>Example uses:</p>
     * <ul>
     *   <li>Mark liked posts within a feed.</li>
     *   <li>Build a "posts I liked" view.</li>
     *   <li>Analyze user like behavior.</li>
     * </ul>
     *
     * @param listener Completion listener returning the post ID list.
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
     * Retrieves the IDs of every post the user has favorited.
     * <p>
     * Returns an empty list when no user is logged in.
     * </p>
     *
     * <p>Example uses:</p>
     * <ul>
     *   <li>Mark favorited posts in a feed.</li>
     *   <li>Build a "my favorites" page.</li>
     *   <li>Analyze favorite behavior.</li>
     * </ul>
     *
     * @param listener Completion listener returning the post ID list.
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
