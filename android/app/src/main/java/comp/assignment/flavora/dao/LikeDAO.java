package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.Like;

import java.util.List;

/**
 * Data access object for likes.
 * Handles persistence operations for like entities.
 * Implements the singleton pattern in line with the reference-app design.
 *
 * @author Flavora Team
 * @version 1.0
 */
public class LikeDAO extends DAO<Like> {
    /** Singleton instance. */
    private static LikeDAO instance;
    /** Firebase data source instance. */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private LikeDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton LikeDAO instance.
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     *
     * @return Singleton LikeDAO instance.
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
     * @param like     Like to add.
     * @param listener Completion callback.
     */
    @Override
    public void add(Like like, OnCompleteListener<Void> listener) {
        dataSource.addLike(like, listener);
    }

    /**
     * Retrieves a like by ID.
     *
     * @param id       Like ID.
     * @param listener Completion callback returning the like.
     */
    @Override
    public void get(String id, OnCompleteListener<Like> listener) {
        dataSource.getLike(id, listener);
    }

    /**
     * Retrieves every like record.
     *
     * @param listener Completion callback with the like list.
     */
    @Override
    public void getAll(OnCompleteListener<List<Like>> listener) {
        dataSource.getAllLikes(listener);
    }

    /**
     * Deletes a like record by ID.
     *
     * @param id       Like ID.
     * @param listener Completion callback.
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deleteLike(id, listener);
    }

    /**
     * Updates a like (no-op because likes seldom change; resolves immediately).
     *
     * @param like     Like entity.
     * @param listener Completion callback.
     */
    @Override
    public void update(Like like, OnCompleteListener<Void> listener) {
        listener.onComplete(Tasks.forResult(null));
    }

    /**
     * Atomic operation: add a like and increment the post's like count.
     *
     * @param userId   User ID.
     * @param postId   Post ID.
     * @param listener Completion callback.
     */
    public void addLikeWithIncrement(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.addLikeAndIncrementCount(userId, postId, listener);
    }

    /**
     * Atomic operation: remove a like and decrement the post's like count.
     *
     * @param userId   User ID.
     * @param postId   Post ID.
     * @param listener Completion callback.
     */
    public void removeLikeWithDecrement(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.removeLikeAndDecrementCount(userId, postId, listener);
    }

    /**
     * Checks whether the user has already liked the post.
     *
     * @param userId   User ID.
     * @param postId   Post ID.
     * @param listener Completion callback returning a boolean result.
     */
    public void hasLiked(String userId, String postId, OnCompleteListener<Boolean> listener) {
        dataSource.checkLikeExists(userId, postId, listener);
    }

    /**
     * Retrieves every like record for the specified post.
     *
     * @param postId   Post ID.
     * @param listener Completion callback with the like list.
     */
    public void getLikesByPost(String postId, OnCompleteListener<List<Like>> listener) {
        dataSource.getLikesByPost(postId, listener);
    }

    /**
     * Retrieves all post IDs liked by the specified user.
     *
     * @param userId   User ID.
     * @param listener Completion callback with the list of post IDs.
     */
    public void getLikesByUser(String userId, OnCompleteListener<List<String>> listener) {
        dataSource.getLikesByUser(userId, listener);
    }
}
