package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.Favorite;

import java.util.List;

/**
 * Data access object for favorites.
 * Handles persistence operations for favorite entities.
 * Implements the singleton pattern to match the reference-app design.
 *
 * @author Flavora Team
 * @version 1.0
 */
public class FavoriteDAO extends DAO<Favorite> {
    /** Singleton instance. */
    private static FavoriteDAO instance;
    /** Firebase data source instance. */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private FavoriteDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton FavoriteDAO instance.
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     *
     * @return Singleton FavoriteDAO instance.
     */
    public static FavoriteDAO getInstance() {
        if (instance == null) {
            synchronized (FavoriteDAO.class) {
                if (instance == null) {
                    instance = new FavoriteDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a favorite record to the database.
     *
     * @param favorite Favorite to add.
     * @param listener Completion callback.
     */
    @Override
    public void add(Favorite favorite, OnCompleteListener<Void> listener) {
        dataSource.addFavorite(favorite, listener);
    }

    /**
     * Retrieves a favorite by ID.
     *
     * @param id       Favorite ID.
     * @param listener Completion callback that receives the favorite.
     */
    @Override
    public void get(String id, OnCompleteListener<Favorite> listener) {
        dataSource.getFavorite(id, listener);
    }

    /**
     * Retrieves every favorite record.
     *
     * @param listener Completion callback with the favorite list.
     */
    @Override
    public void getAll(OnCompleteListener<List<Favorite>> listener) {
        dataSource.getAllFavorites(listener);
    }

    /**
     * Deletes a favorite record by ID.
     *
     * @param id       Favorite ID.
     * @param listener Completion callback.
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deleteFavorite(id, listener);
    }

    /**
     * Updates a favorite (no-op because favorites rarely change; resolves immediately).
     *
     * @param favorite Favorite entity.
     * @param listener Completion callback.
     */
    @Override
    public void update(Favorite favorite, OnCompleteListener<Void> listener) {
        listener.onComplete(Tasks.forResult(null));
    }

    /**
     * Atomic operation: add a favorite and increment the post's favorite count.
     *
     * @param userId   User ID.
     * @param postId   Post ID.
     * @param listener Completion callback.
     */
    public void addFavoriteWithCount(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.addFavoriteAndIncrementCount(userId, postId, listener);
    }

    /**
     * Atomic operation: remove a favorite and decrement the post's favorite count.
     *
     * @param userId   User ID.
     * @param postId   Post ID.
     * @param listener Completion callback.
     */
    public void removeFavoriteWithCount(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.removeFavoriteAndDecrementCount(userId, postId, listener);
    }

    /**
     * Checks whether the user has already favorited the post.
     *
     * @param userId   User ID.
     * @param postId   Post ID.
     * @param listener Completion callback returning a boolean result.
     */
    public void hasFavorited(String userId, String postId, OnCompleteListener<Boolean> listener) {
        dataSource.checkFavoriteExists(userId, postId, listener);
    }

    /**
     * Retrieves all post IDs favorited by the specified user.
     *
     * @param userId   User ID.
     * @param listener Completion callback with the list of post IDs.
     */
    public void getFavoritesByUser(String userId, OnCompleteListener<List<String>> listener) {
        dataSource.getFavoritesByUser(userId, listener);
    }
}
