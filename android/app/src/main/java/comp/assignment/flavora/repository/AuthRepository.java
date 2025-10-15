package comp.assignment.flavora.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import comp.assignment.flavora.dao.UserDAO;
import comp.assignment.flavora.model.User;

/**
 * Authentication Repository
 * <p>
 * Handles all user authentication operations, including sign-up, sign-in, and sign-out.
 * Uses the Singleton pattern to ensure a single global repository instance.
 * Integrates Firebase Authentication and Firestore data operations.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>User registration: create a Firebase Auth account and persist user info in Firestore</li>
 *   <li>User login: authenticate with email and password</li>
 *   <li>User logout: clear the current user session</li>
 *   <li>Get current user: provide details of the currently signed-in user</li>
 * </ul>
 *
 * @author
 * Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class AuthRepository {
    /** Singleton instance */
    private static AuthRepository instance;
    /** Firebase Auth instance */
    private final FirebaseAuth auth;
    /** User DAO */
    private final UserDAO userDAO;

    /**
     * Private constructor
     * <p>
     * Initializes Firebase Authentication and the UserDAO instance.
     * Private to prevent external instantiation and enforce Singleton.
     * </p>
     */
    private AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.userDAO = UserDAO.getInstance();
    }

    /**
     * Returns the singleton instance of AuthRepository.
     * <p>
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     * </p>
     *
     * @return the unique instance of AuthRepository
     */
    public static AuthRepository getInstance() {
        if (instance == null) {
            synchronized (AuthRepository.class) {
                if (instance == null) {
                    instance = new AuthRepository();
                }
            }
        }
        return instance;
    }

    /**
     * Registers a new user.
     * <p>
     * Full registration flow:
     * 1) Create an Auth account in Firebase Authentication
     * 2) Obtain the generated user UID
     * 3) Create the corresponding user document in Firestore
     * </p>
     *
     * <p>Flow details:</p>
     * <ul>
     *   <li>Call Firebase Auth to create the account</li>
     *   <li>On success, build a User object and save it to Firestore</li>
     *   <li>Initial user data includes: username, email, createdAt, etc.</li>
     *   <li>Avatar, following, followers, and posts count are initialized to defaults</li>
     * </ul>
     *
     * @param email user email for login and verification
     * @param password user password (must meet Firebase requirements, min length 6)
     * @param username display name shown in the app
     * @return Task<AuthResult> asynchronous task with the auth result;
     *         on success contains AuthResult, on failure contains the exception
     */
    public Task<AuthResult> register(String email, String password, String username) {
        // Create user account via Firebase Authentication
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    // Ensure auth succeeded and user is not null
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        String userId = firebaseUser.getUid();

                        // Build user object with initial information
                        User user = new User(
                                userId,
                                username,
                                email,
                                "", // initial avatar URL is empty
                                Timestamp.now(), // creation time
                                0, // followers count
                                0, // following count
                                0  // posts count
                        );

                        // Persist user to Firestore
                        userDAO.add(user, addTask -> {
                            if (!addTask.isSuccessful()) {
                                // Note: if Firestore write fails, we silently ignore here.
                                // The user can still sign in, but profile data may be incomplete.
                                // In production, consider logging or a retry mechanism.
                            }
                        });
                    }
                    // Return original auth task result
                    return task;
                });
    }

    /**
     * Signs a user in with email and password.
     * <p>
     * Directly delegates to Firebase Authentication.
     * </p>
     *
     * @param email registered email address
     * @param password user password
     * @return Task<AuthResult> asynchronous task with the sign-in result
     */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Signs the current user out.
     * <p>
     * After calling this, getCurrentUser() will return null.
     * </p>
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * Returns the currently signed-in Firebase user.
     * <p>
     * Contains basic information such as UID and email.
     * </p>
     *
     * @return FirebaseUser the current user, or null if not signed in
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Checks whether a user is signed in.
     * <p>
     * Useful for guarding routes and conditional rendering.
     * </p>
     *
     * @return true if a user is signed in; false otherwise
     */
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    /**
     * Returns the UID of the currently signed-in user.
     * <p>
     * The UID is the unique identifier generated by Firebase for each user and
     * is commonly used for database lookups and access control.
     * </p>
     *
     * @return the UID as a String, or null if not signed in
     */
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
