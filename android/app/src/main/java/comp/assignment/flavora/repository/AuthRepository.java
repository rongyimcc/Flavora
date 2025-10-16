package comp.assignment.flavora.repository;

import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import comp.assignment.flavora.dao.UserDAO;
import comp.assignment.flavora.model.User;

/**
 * Authentication repository.
 * <p>
 * Handles every authentication-related operation such as register, login, and logout.
 * Exposed as a singleton so only one repository instance exists.
 * Bridges Firebase Authentication and Firestore operations.
 * </p>
 *
 * <p>Key responsibilities:</p>
 * <ul>
 *   <li>User registration: create a Firebase auth account and persist user data in Firestore.</li>
 *   <li>User login: authenticate with email and password.</li>
 *   <li>User logout: clear the current session.</li>
 *   <li>Current user info: expose the details of the logged-in user.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class AuthRepository {
    /** Singleton instance. */
    private static AuthRepository instance;
    /** Firebase auth instance. */
    private final FirebaseAuth auth;
    /** User data access object. */
    private final UserDAO userDAO;

    /**
     * Private constructor.
     * <p>
     * Initializes Firebase Authentication and the UserDAO.
     * Private to enforce the singleton pattern.
     * </p>
     */
    private AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.userDAO = UserDAO.getInstance();
    }

    /**
     * Returns the singleton AuthRepository instance.
     * <p>
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     * </p>
     *
     * @return The unique AuthRepository instance.
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
     * Executes the full registration flow:
     * 1. Create the Firebase Authentication account.
     * 2. Retrieve the generated UID.
     * 3. Create the matching Firestore document.
     * </p>
     *
     * <p>Flow details:</p>
     * <ul>
     *   <li>First call Firebase Auth to create the account.</li>
     *   <li>If authentication succeeds, build a User object and save it to Firestore.</li>
     *   <li>Initial user data includes username, email, creation timestamp, and so on.</li>
     *   <li>Avatar, following, followers, and post counts start with default values.</li>
     * </ul>
     *
     * @param email Email address used for login and verification.
     * @param password Password that meets Firebase requirements (minimum 6 characters).
     * @param username Username shown inside the app.
     * @return Task<AuthResult> representing the async auth result.
     *         Returns AuthResult with user info on success.
     *         Carries the error via exception when it fails.
     */
    public Task<AuthResult> register(String email, String password, String username) {
        // Use Firebase Authentication to create the account.
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    // Ensure authentication succeeded and the user object is present.
                    if (task.isSuccessful() && task.getResult().getUser() != null) {
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        String userId = firebaseUser.getUid();

                        // Create the user object with initial defaults.
                        User user = new User(
                                userId,
                                username,
                                email,
                                "", // Initial avatar URL is empty.
                                Timestamp.now(), // Record creation time.
                                0, // Followers start at 0.
                                0, // Following starts at 0.
                                0  // Post count starts at 0.
                        );

                        // Persist the user in Firestore.
                        userDAO.add(user, addTask -> {
                            if (!addTask.isSuccessful()) {
                                // Note: if Firestore persistence fails, we currently ignore it.
                                // The user can still sign in, but their profile may be incomplete.
                                // A production app should log or retry here.
                            }
                        });
                    }
                    // Return the original auth task result.
                    return task;
                });
    }

    /**
     * Authenticates an existing user.
     * <p>
     * Uses email and password to sign in via Firebase Authentication.
     * </p>
     *
     * @param email Email used at registration.
     * @param password Account password.
     * @return Task<AuthResult> containing the login result.
     *         Returns AuthResult with user info on success.
     *         Returns an exception (e.g., wrong password, user missing) on failure.
     */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Signs out the current user.
     * <p>
     * Clears the active session; after calling this, getCurrentUser() returns null.
     * </p>
     */
    public void logout() {
        auth.signOut();
    }

    /**
     * Returns the currently authenticated user.
     * <p>
     * Delegates to Firebase Authentication and exposes the current user details (UID, email, etc.).
     * </p>
     *
     * @return FirebaseUser for the logged-in user, or null if no user is signed in.
     */
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    /**
     * Indicates whether a user is currently signed in.
     * <p>
     * Useful for gating navigation or rendering conditional UI.
     * </p>
     *
     * @return true when a user is logged in; otherwise false.
     */
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    /**
     * Returns the UID of the current user.
     * <p>
     * Firebase generates a unique UID for each user; useful for database queries and access control.
     * </p>
     *
     * @return UID string when logged in, otherwise null.
     */
    public String getCurrentUserId() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
