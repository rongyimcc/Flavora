package comp.assignment.flavora.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import comp.assignment.flavora.MainActivity;
import comp.assignment.flavora.databinding.ActivityLoginBinding;
import comp.assignment.flavora.repository.AuthRepository;

/**
 * Login Activity - user authentication screen.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Provides a login form with email and password inputs.</li>
 *   <li>Validates user input (email format, password length, etc.).</li>
 *   <li>Authenticates via AuthRepository.</li>
 *   <li>Navigates to the main screen on success.</li>
 *   <li>Provides an entry to the registration screen.</li>
 * </ul>
 *
 * <p>Validation rules:</p>
 * <ul>
 *   <li>Email: required and must match email format.</li>
 *   <li>Password: required and at least 6 characters.</li>
 * </ul>
 *
 * <p>Technical notes:</p>
 * <ul>
 *   <li>Uses ViewBinding.</li>
 *   <li>Uses Task for async login.</li>
 *   <li>Shows loading state during login and disables inputs.</li>
 *   <li>Uses TextInputLayout for error messages.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {

    /** ViewBinding for accessing views in the layout. */
    private ActivityLoginBinding binding;
    /** Auth repository handling login logic. */
    private AuthRepository authRepository;

    /**
     * Activity lifecycle - called on create.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Initialize ViewBinding and set content view.</li>
     *   <li>Obtain AuthRepository singleton.</li>
     *   <li>Set click listeners for login and register actions.</li>
     * </ol>
     *
     * @param savedInstanceState saved state for restoring Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get auth repository instance
        authRepository = AuthRepository.getInstance();

        // Set listeners
        binding.buttonLogin.setOnClickListener(v -> attemptLogin());
        binding.textViewRegister.setOnClickListener(v -> navigateToRegister());
    }

    /**
     * Attempt to login with the provided credentials.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Clear previous errors.</li>
     *   <li>Read email and password.</li>
     *   <li>Validate input.</li>
     *   <li>If valid, call AuthRepository to login.</li>
     *   <li>Navigate to main screen on success or show error on failure.</li>
     * </ol>
     *
     * <p>Validation rules:</p>
     * <ul>
     *   <li>Email: required and must match email format.</li>
     *   <li>Password: required and at least 6 characters.</li>
     * </ul>
     */
    private void attemptLogin() {

        binding.textInputLayoutEmail.setError(null);
        binding.textInputLayoutPassword.setError(null);

        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        boolean hasError = false;

        // Validate email
        if (TextUtils.isEmpty(email)) {
            binding.textInputLayoutEmail.setError("Email is required");
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.setError("Invalid email address");
            hasError = true;
        }

        // Validate password
        if (TextUtils.isEmpty(password)) {
            binding.textInputLayoutPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 6) {
            binding.textInputLayoutPassword.setError("Password must be at least 6 characters");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        setLoading(true);

        // Try login
        authRepository.login(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        navigateToMain();
                    } else {
                        String errorMessage = "Login failed. Please check your credentials.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Set loading state for the login UI.
     *
     * <p>When loading:</p>
     * <ul>
     *   <li>Disable login button.</li>
     *   <li>Show progress bar.</li>
     *   <li>Disable all inputs.</li>
     *   <li>Disable register link.</li>
     * </ul>
     *
     * <p>Prevents duplicate submissions or input changes during the request.</p>
     *
     * @param loading true to show loading, false to hide.
     */
    private void setLoading(boolean loading) {
        binding.buttonLogin.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.editTextEmail.setEnabled(!loading);
        binding.editTextPassword.setEnabled(!loading);
        binding.textViewRegister.setEnabled(!loading);
    }

    /**
     * Navigate to the registration screen.
     *
     * <p>Called when the user taps the "Register" link.</p>
     */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Navigate to the main screen and clear back stack.
     *
     * <p>Called after a successful login. Uses intent flags to clear previous
     * activities so the user cannot navigate back to the login screen.</p>
     *
     * <p>Intent flags used:</p>
     * <ul>
     *   <li>FLAG_ACTIVITY_NEW_TASK - create a new task</li>
     *   <li>FLAG_ACTIVITY_CLEAR_TASK - clear the existing task</li>
     * </ul>
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Activity lifecycle - called on destroy.
     *
     * <p>Clears the ViewBinding reference to avoid memory leaks.</p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
