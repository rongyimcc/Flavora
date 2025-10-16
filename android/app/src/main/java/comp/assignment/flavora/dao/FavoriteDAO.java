package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.Favorite;

import java.util.List;

/**
 * Favorite Data Access Object (DAO)
 * Handles all data access operations related to Favorite entities.
 * Implements the Singleton pattern and follows the reference-app design conventions.
 *
 * @author
 * Flavora Team
 * @version 1.0
 */
public class FavoriteDAO extends DAO<Favorite> {
    /** Singleton instance */
    private static FavoriteDAO instance;
    /** Firebase data source instance */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private FavoriteDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton instance of FavoriteDAO.
     * Implements thread-safe lazy initialization using Double-Checked Locking (DCL).
     *
     * @return FavoriteDAO singleton instance
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
     * @param favorite the Favorite object to add
     * @param listener completion listener for operation callback
     */
    @Override
    public void add(Favorite favorite, OnCompleteListener<Void> listener) {
        dataSource.addFavorite(favorite, listener);
    }

    /**
     * Retrieves a favorite record by ID.
     *
     * @param id       the favorite record ID
     * @param listener completion listener returning the Favorite object
     */
    @Override
    public void get(String id, OnCompleteListener<Favorite> listener) {
        dataSource.getFavorite(id, listener);
    }

    /**
     * Retrieves all favorite records.
     *
     * @param listener completion listener returning a list of favorites
     */
    @Override
    public void getAll(OnCompleteListener<List<Favorite>> listener) {
        dataSource.getAllFavorites(listener);
    }

    /**
     * Deletes a favorite record by ID.
     *
     * @param id       the favorite record ID
     * @param listener completion listener for operation callback
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deleteFavorite(id, listener);
    }

    /**
     * Updates a favorite record (not typically needed for favorites, returns success immediately).
     *
     * @param favorite the Favorite object
     * @param listener completion listener for operation callback
     */
    @Override
    public void update(Favorite favorite, OnCompleteListener<Void> listener) {
        listener.onComplete(Tasks.forResult(null));
    }

    /**
     * Atomic operation: adds a favorite record and increments the post's favorite count.
     *
     * @param userId   the user ID
     * @param postId   the post ID
     * @param listener completion listener for operation callback
     */
    public void addFavoriteWithCount(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.addFavoriteAndIncrementCount(userId, postId, listener);
    }

    /**
     * Atomic operation: removes a favorite record and decrements the post's favorite count.
     *
     * @param userId   the user ID
     * @param postId   the post ID
     * @param listener completion listener for operation callback
     */
    public void removeFavoriteWithCount(String userId, String postId, OnCompleteListener<Void> listener) {
        dataSource.removeFavoriteAndDecrementCount(userId, postId, listener);
    }

    /**
     * Checks whether a user has favorited a specific post.
     *
     * @param userId   the user ID
     * @param postId   the post ID
     * @param listener completion listener returning a boolean result
     */
    public void hasFavorited(String userId, String postId, OnCompleteListener<Boolean> listener) {
        dataSource.checkFavoriteExists(userId, postId, listener);
    }

    /**
     * Retrieves all post IDs favorited by a specific user.
     *
     * @param userId   the user ID
     * @param listener completion listener returning a list of post IDs
     */
    public void getFavoritesByUser(String userId, OnCompleteListener<List<String>> listener) {
        dataSource.getFavoritesByUser(userId, listener);
    }
}
