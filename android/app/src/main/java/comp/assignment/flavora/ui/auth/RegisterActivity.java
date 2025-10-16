package comp.assignment.flavora.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import comp.assignment.flavora.MainActivity;
import comp.assignment.flavora.databinding.ActivityRegisterBinding;
import comp.assignment.flavora.repository.AuthRepository;

/**
 * Registration screen for new users.
 *
 * <p>Highlights:</p>
 * <ul>
 *   <li>Collects username, email, password, and confirmation.</li>
 *   <li>Validates username pattern, email format, and password strength.</li>
 *   <li>Registers via {@link AuthRepository}.</li>
 *   <li>Automatically logs in and navigates to the main screen when successful.</li>
 *   <li>Provides a link back to the login screen.</li>
 * </ul>
 *
 * <p>Validation rules:</p>
 * <ul>
 *   <li>Username: required, at least 3 characters, letters/digits/underscore only.</li>
 *   <li>Email: required and must match email format.</li>
 *   <li>Password: required and at least 6 characters.</li>
 *   <li>Confirm password: required and must match the password.</li>
 * </ul>
 *
 * <p>Implementation notes:</p>
 * <ul>
 *   <li>Uses ViewBinding for view access.</li>
 *   <li>Processes registration asynchronously with Task.</li>
 *   <li>Shows a loading state while disabling input.</li>
 *   <li>Displays validation errors with TextInputLayout.</li>
 *   <li>Validates username using a regular expression.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class RegisterActivity extends AppCompatActivity {

    /** ViewBinding instance for accessing layout views. */
    private ActivityRegisterBinding binding;
    /** Authentication repository handling registration logic. */
    private AuthRepository authRepository;

    /**
     * Activity lifecycle entry point.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Inflate ViewBinding and set the content view.</li>
     *   <li>Obtain the AuthRepository singleton.</li>
     *   <li>Attach handlers to the register button and login link.</li>
     * </ol>
     *
     * @param savedInstanceState Saved bundle for restoration.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewBinding.
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the auth repository instance.
        authRepository = AuthRepository.getInstance();

        // Attach listeners.
        binding.buttonRegister.setOnClickListener(v -> attemptRegister());
        binding.textViewLogin.setOnClickListener(v -> finish());
    }

    /**
     * Attempts to register a new user with the provided data.
     *
     * <p>Steps:</p>
     * <ol>
     *   <li>Clear previous error messages.</li>
     *   <li>Read username, email, password, and confirmation.</li>
     *   <li>Validate the inputs.</li>
     *   <li>If valid, call AuthRepository to register.</li>
     *   <li>Navigate to the main screen or show an error accordingly.</li>
     * </ol>
     *
     * <p>Validation rules:</p>
     * <ul>
     *   <li>Username: required, at least 3 characters, letters/digits/underscore only.</li>
     *   <li>Email: required and must match email format.</li>
     *   <li>Password: required and at least 6 characters.</li>
     *   <li>Confirm password: required and must match the password.</li>
     * </ul>
     */
    private void attemptRegister() {
        // Clear previous errors.
        binding.textInputLayoutUsername.setError(null);
        binding.textInputLayoutEmail.setError(null);
        binding.textInputLayoutPassword.setError(null);
        binding.textInputLayoutConfirmPassword.setError(null);

        // Read user input.
        String username = binding.editTextUsername.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        // Validate input.
        boolean hasError = false;

        // Validate username.
        if (TextUtils.isEmpty(username)) {
            binding.textInputLayoutUsername.setError("Username is required");
            hasError = true;
        } else if (username.length() < 3) {
            binding.textInputLayoutUsername.setError("Username must be at least 3 characters");
            hasError = true;
        } else if (!username.matches("[a-zA-Z0-9_]+")) {
            binding.textInputLayoutUsername.setError("Username can only contain letters, numbers, and underscores");
            hasError = true;
        }

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

        // Validate confirmation.
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.textInputLayoutConfirmPassword.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            binding.textInputLayoutConfirmPassword.setError("Passwords do not match");
            hasError = true;
        }

        // Abort if any validation failed.
        if (hasError) {
            return;
        }

        // Show loading UI.
        setLoading(true);

        // Attempt registration.
        authRepository.register(email, password, username)
                .addOnCompleteListener(this, task -> {
                    // Hide loading UI.
                    setLoading(false);

                    if (task.isSuccessful()) {
                        // Success: navigate to the main screen.
                        navigateToMain();
                    } else {
                        // Failure: show error message.
                        String errorMessage = "Registration failed. Please try again.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Controls the loading state of the registration form.
     *
     * <p>When loading:</p>
     * <ul>
     *   <li>Disable the register button.</li>
     *   <li>Show the progress indicator.</li>
     *   <li>Disable all input fields.</li>
     *   <li>Disable the login link.</li>
     * </ul>
     *
     * <p>Prevents repeated submissions while the request is ongoing.</p>
     *
     * @param loading true to enable loading state; false to restore the form.
     */
    private void setLoading(boolean loading) {
        binding.buttonRegister.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.editTextUsername.setEnabled(!loading);
        binding.editTextEmail.setEnabled(!loading);
        binding.editTextPassword.setEnabled(!loading);
        binding.editTextConfirmPassword.setEnabled(!loading);
        binding.textViewLogin.setEnabled(!loading);
    }

    /**
     * Navigates to the main activity and clears the back stack after registration succeeds.
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
     * Lifecycle cleanup; clears the binding reference to avoid leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
