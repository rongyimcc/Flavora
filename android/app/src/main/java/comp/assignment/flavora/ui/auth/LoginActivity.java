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
 * Login screen for user authentication.
 *
 * <p>Highlights:</p>
 * <ul>
 *   <li>Provides email/password input fields.</li>
 *   <li>Validates email format and password length.</li>
 *   <li>Authenticates via {@link AuthRepository}.</li>
 *   <li>Navigates to the main screen on success.</li>
 *   <li>Offers a link to the registration screen.</li>
 * </ul>
 *
 * <p>Validation rules:</p>
 * <ul>
 *   <li>Email: required and must match email format.</li>
 *   <li>Password: required and at least 6 characters.</li>
 * </ul>
 *
 * <p>Implementation notes:</p>
 * <ul>
 *   <li>Uses ViewBinding for view access.</li>
 *   <li>Handles login asynchronously with Task.</li>
 *   <li>Shows a loading state that disables inputs.</li>
 *   <li>Displays errors through TextInputLayout.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class LoginActivity extends AppCompatActivity {

    /** ViewBinding instance for accessing layout views. */
    private ActivityLoginBinding binding;
    /** Authentication repository handling login logic. */
    private AuthRepository authRepository;

    /**
     * Activity lifecycle entry point.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Inflate ViewBinding and set the content view.</li>
     *   <li>Obtain the AuthRepository singleton.</li>
     *   <li>Attach listeners to the login button and register link.</li>
     * </ol>
     *
     * @param savedInstanceState Saved bundle for restoration.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding.
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the auth repository instance.
        authRepository = AuthRepository.getInstance();

        // Attach listeners.
        binding.buttonLogin.setOnClickListener(v -> attemptLogin());
        binding.textViewRegister.setOnClickListener(v -> navigateToRegister());
    }

    /**
     * Attempts to log in with the provided credentials.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Clear previous error messages.</li>
     *   <li>Read the email and password inputs.</li>
     *   <li>Validate the input.</li>
     *   <li>If valid, invoke AuthRepository to log in.</li>
     *   <li>Navigate to the main screen or show the error.</li>
     * </ol>
     *
     * <p>Validation rules:</p>
     * <ul>
     *   <li>Email: required and must match email format.</li>
     *   <li>Password: required and at least 6 characters.</li>
     * </ul>
     */
    private void attemptLogin() {
        // Clear previous errors.
        binding.textInputLayoutEmail.setError(null);
        binding.textInputLayoutPassword.setError(null);

        // Read user input.
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        // Validate input.
        boolean hasError = false;

        // Validate email.
        if (TextUtils.isEmpty(email)) {
            binding.textInputLayoutEmail.setError("Email is required");
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.setError("Invalid email address");
            hasError = true;
        }

        // Validate password.
        if (TextUtils.isEmpty(password)) {
            binding.textInputLayoutPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 6) {
            binding.textInputLayoutPassword.setError("Password must be at least 6 characters");
            hasError = true;
        }

        // Abort if any validation failed.
        if (hasError) {
            return;
        }

        // Show loading UI.
        setLoading(true);

        // Attempt login.
        authRepository.login(email, password)
                .addOnCompleteListener(this, task -> {
                    // Hide loading UI.
                    setLoading(false);

                    if (task.isSuccessful()) {
                        // Success: navigate to the main screen.
                        navigateToMain();
                    } else {
                        // Failure: show error message.
                        String errorMessage = "Login failed. Please check your credentials.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Controls the loading state of the login form.
     *
     * <p>When loading:</p>
     * <ul>
     *   <li>Disable the login button.</li>
     *   <li>Show the progress indicator.</li>
     *   <li>Disable the input fields.</li>
     *   <li>Disable the register link.</li>
     * </ul>
     *
     * <p>Prevents duplicate submissions while the request is in flight.</p>
     *
     * @param loading true to show loading state; false to restore the form.
     */
    private void setLoading(boolean loading) {
        binding.buttonLogin.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.editTextEmail.setEnabled(!loading);
        binding.editTextPassword.setEnabled(!loading);
        binding.textViewRegister.setEnabled(!loading);
    }

    /**
     * Opens the registration screen when the user taps the link.
     */
    private void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Navigates to the main activity and clears the back stack.
     *
     * <p>Called after a successful login so the user cannot return to the login screen.</p>
     *
     * <p>Intent flags:</p>
     * <ul>
     *   <li>FLAG_ACTIVITY_NEW_TASK - start a new task.</li>
     *   <li>FLAG_ACTIVITY_CLEAR_TASK - clear existing activities.</li>
     * </ul>
     */
    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Lifecycle callback for cleanup; clears the binding reference to avoid leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
