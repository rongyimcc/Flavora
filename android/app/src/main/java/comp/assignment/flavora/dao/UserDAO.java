package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.User;

import java.util.List;

/**
 * Data access object for users.
 * Handles persistence operations for user entities.
 * Implements the singleton pattern in line with the reference-app design.
 *
 * @author Flavora Team
 * @version 1.0
 */
public class UserDAO extends DAO<User> {
    /** Singleton instance. */
    private static UserDAO instance;
    /** Firebase data source instance. */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private UserDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton UserDAO instance.
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     *
     * @return Singleton UserDAO instance.
     */
    public static UserDAO getInstance() {
        if (instance == null) {
            synchronized (UserDAO.class) {
                if (instance == null) {
                    instance = new UserDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a user to the database.
     *
     * @param user     User to add.
     * @param listener Completion callback.
     */
    @Override
    public void add(User user, OnCompleteListener<Void> listener) {
        dataSource.addUser(user, listener);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id       User ID.
     * @param listener Completion callback returning the user.
     */
    @Override
    public void get(String id, OnCompleteListener<User> listener) {
        dataSource.getUser(id, listener);
    }

    /**
     * Retrieves every user.
     *
     * @param listener Completion callback with the user list.
     */
    @Override
    public void getAll(OnCompleteListener<List<User>> listener) {
        dataSource.getAllUsers(listener);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id       User ID.
     * @param listener Completion callback.
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deleteUser(id, listener);
    }

    /**
     * Updates user information.
     *
     * @param user     User containing the updated data.
     * @param listener Completion callback.
     */
    @Override
    public void update(User user, OnCompleteListener<Void> listener) {
        dataSource.updateUser(user, listener);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username Username to search for.
     * @param listener Completion callback returning the matched user.
     */
    public void getUserByUsername(String username, OnCompleteListener<User> listener) {
        dataSource.getUserByUsername(username, listener);
    }

    /**
     * Increments a user's post count.
     *
     * @param userId   User ID.
     * @param listener Completion callback.
     */
    public void incrementPostsCount(String userId, OnCompleteListener<Void> listener) {
        dataSource.incrementUserPostsCount(userId, listener);
    }

    /**
     * Decrements a user's post count.
     *
     * @param userId   User ID.
     * @param listener Completion callback.
     */
    public void decrementPostsCount(String userId, OnCompleteListener<Void> listener) {
        dataSource.decrementUserPostsCount(userId, listener);
    }
}
