package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.User;

import java.util.List;

/**
 * User Data Access Object (DAO)
 * Handles all data access operations related to User entities.
 * Implements the Singleton pattern and follows the reference-app design conventions.
 *
 * @author Flavora Team
 * @version 1.0
 */
public class UserDAO extends DAO<User> {
    /** Singleton instance */
    private static UserDAO instance;
    /** Firebase data source instance */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private UserDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton instance of UserDAO.
     * Implements thread-safe lazy initialization using Double-Checked Locking (DCL).
     *
     * @return UserDAO singleton instance
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
     * @param user     the user to add
     * @param listener completion listener for operation callback
     */
    @Override
    public void add(User user, OnCompleteListener<Void> listener) {
        dataSource.addUser(user, listener);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id       the user ID
     * @param listener completion listener returning the User object
     */
    @Override
    public void get(String id, OnCompleteListener<User> listener) {
        dataSource.getUser(id, listener);
    }

    /**
     * Retrieves all users.
     *
     * @param listener completion listener returning a list of all users
     */
    @Override
    public void getAll(OnCompleteListener<List<User>> listener) {
        dataSource.getAllUsers(listener);
    }

    /**
     * Deletes a user by ID.
     *
     * @param id       the user ID
     * @param listener completion listener for operation callback
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deleteUser(id, listener);
    }

    /**
     * Updates user information.
     *
     * @param user     the user object containing updated data
     * @param listener completion listener for operation callback
     */
    @Override
    public void update(User user, OnCompleteListener<Void> listener) {
        dataSource.updateUser(user, listener);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username the username to search for
     * @param listener completion listener returning the matching User object
     */
    public void getUserByUsername(String username, OnCompleteListener<User> listener) {
        dataSource.getUserByUsername(username, listener);
    }

    /**
     * Increments the user's post count.
     *
     * @param userId   the user ID
     * @param listener completion listener for operation callback
     */
    public void incrementPostsCount(String userId, OnCompleteListener<Void> listener) {
        dataSource.incrementUserPostsCount(userId, listener);
    }

    /**
     * Decrements the user's post count.
     *
     * @param userId   the user ID
     * @param listener completion listener for operation callback
     */
    public void decrementPostsCount(String userId, OnCompleteListener<Void> listener) {
        dataSource.decrementUserPostsCount(userId, listener);
    }
}
