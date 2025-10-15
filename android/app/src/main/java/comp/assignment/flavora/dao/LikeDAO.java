package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.Like;

import java.util.List;

/**
 * Like Data Access Object (DAO)
 * Handles all data access operations related to Like entities.
 * Implements the Singleton pattern and follows the reference-app design conventions.
 *
 * @author
 * Flavora Team
 * @version 1.0
 */
public class LikeDAO extends DAO<Like> {
    /** Singleton instance */
    private static LikeDAO instance;
    /** Firebase data source instance */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private LikeDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton instance of LikeDAO.
     * Implements thread-safe lazy initialization using Double-Checked Locking (DCL).
     *
     * @return LikeDAO singleton instance
     */
    public static LikeDAO getInstance() {
        if (instance == null) {
            synchronized (LikeDAO.class) {
                if (instance == null) {
                    instance = new LikeDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a like record to the database.
     *
     * @param like     the Like object to add
     * @param listener completion listener for operation callback
     */
    @Override
    public void add(Like like, OnCompleteListener<Void> listener) {
        dataSource.addLike(like, listener);
    }

    /**
     * Retrieves a like record by ID.
     *
     * @param id       the like record ID
     * @param listener completion listener returning the Like object
     */
    @Override
    public void get(String id, OnCompleteListener<Like> listener) {
        dataSource.getLike(id, listener);
    }

    /**
     * Retrieves all like records.
     *
     * @param listener completion listener returning a list of likes
     */
    @Override
    public void getAll(OnCompleteListener<List<Like>> listener) {
        dataSource.getAllLikes(listener);
    }

    /**
     * Deletes a like record by ID.
     *
     * @param id       the like record ID
     * @param listener completion listener for operation callback
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deleteLike(id, listener);
    }

    /**
     * Updates a like record (not typically needed for likes, returns success immediately).
     *
     * @param like     the Like object
     * @param listener completion listener for operation callback
     */
    @Override
    public void update(Like like, OnCompleteListener<Void> listener) {
        listener.onComplete(Tasks.forResult(null));
    }

    /**
     * Atomic operation: adds a like record and increments the corresponding post's like count.
     *
     * @param userId   the user ID
     * @param postId   the post ID
     * @param listener completion listener for operation callback
     */
    public void addLikeWithIncrement(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.addLikeAndIncrementCount(userId, postId, listener);
    }

    /**
     * Atomic operation: removes a like record and decrements the corresponding post's like count.
     *
     * @param userId   the user ID
     * @param postId   the post ID
     * @param listener completion listener for operation callback
     */
    public void removeLikeWithDecrement(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.removeLikeAndDecrementCount(userId, postId, listener);
    }

    /**
     * Checks whether a user has liked a specific post.
     *
     * @param userId   the user ID
     * @param postId   the post ID
     * @param listener completion listener returning a boolean result
     */
    public void hasLiked(String userId, String postId, OnCompleteListener<Boolean> listener) {
        dataSource.checkLikeExists(userId, postId, listener);
    }

    /**
     * Retrieves all like records for a given post.
     *
     * @param postId   the post ID
     * @param listener completion listener returning a list of likes
     */
    public void getLikesByPost(String postId, OnCompleteListener<List<Like>> listener) {
        dataSource.getLikesByPost(postId, listener);
    }

    /**
     * Retrieves all post IDs liked by a specific user.
     *
     * @param userId   the user ID
     * @param listener completion listener returning a list of post IDs
     */
    public void getLikesByUser(String userId, OnCompleteListener<List<String>> listener) {
        dataSource.getLikesByUser(userId, listener);
    }
}
