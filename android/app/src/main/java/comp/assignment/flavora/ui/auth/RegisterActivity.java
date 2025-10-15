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
 * Register Activity - new user sign-up screen.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Provides a registration form with username, email, password, and confirm password.</li>
 *   <li>Validates input (username pattern, email format, password strength, etc.).</li>
 *   <li>Registers the user via AuthRepository.</li>
 *   <li>Auto-login on success and navigate to the main screen.</li>
 *   <li>Provides a link back to the login screen.</li>
 * </ul>
 *
 * <p>Validation rules:</p>
 * <ul>
 *   <li>Username: required, at least 3 chars, letters/numbers/underscore only.</li>
 *   <li>Email: required and must match email format.</li>
 *   <li>Password: required and at least 6 chars.</li>
 *   <li>Confirm password: required and must match password.</li>
 * </ul>
 *
 * <p>Technical notes:</p>
 * <ul>
 *   <li>Uses ViewBinding.</li>
 *   <li>Uses Task for async registration.</li>
 *   <li>Shows loading state and disables inputs during request.</li>
 *   <li>Uses TextInputLayout to display errors.</li>
 *   <li>Uses regex to validate username format.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class RegisterActivity extends AppCompatActivity {

    /** ViewBinding for accessing views in the layout. */
    private ActivityRegisterBinding binding;
    /** Auth repository that performs registration logic. */
    private AuthRepository authRepository;

    /**
     * Activity lifecycle - called on create.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Initialize ViewBinding and set content view.</li>
     *   <li>Obtain AuthRepository singleton.</li>
     *   <li>Set click listeners for register button and login link.</li>
     * </ol>
     *
     * @param savedInstanceState saved state for restoring Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepository = AuthRepository.getInstance();

        binding.buttonRegister.setOnClickListener(v -> attemptRegister());
        binding.textViewLogin.setOnClickListener(v -> finish());
    }

    /**
     * Attempt to register a new user with provided info.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Clear previous errors.</li>
     *   <li>Read username, email, password, confirm password.</li>
     *   <li>Validate inputs.</li>
     *   <li>If valid, call AuthRepository to register.</li>
     *   <li>Navigate to main or show error based on result.</li>
     * </ol>
     *
     * <p>Validation rules:</p>
     * <ul>
     *   <li>Username: required, ≥3 chars, letters/numbers/underscore only.</li>
     *   <li>Email: required, valid format.</li>
     *   <li>Password: required, ≥6 chars.</li>
     *   <li>Confirm password: required, must match.</li>
     * </ul>
     */
    private void attemptRegister() {
        // Clear previous errors
        binding.textInputLayoutUsername.setError(null);
        binding.textInputLayoutEmail.setError(null);
        binding.textInputLayoutPassword.setError(null);
        binding.textInputLayoutConfirmPassword.setError(null);

        // Read inputs
        String username = binding.editTextUsername.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        boolean hasError = false;

        // Validate Username
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

        // Validate Email
        if (TextUtils.isEmpty(email)) {
            binding.textInputLayoutEmail.setError("Email is required");
            hasError = true;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.textInputLayoutEmail.setError("Invalid email address");
            hasError = true;
        }

        // Validate Password
        if (TextUtils.isEmpty(password)) {
            binding.textInputLayoutPassword.setError("Password is required");
            hasError = true;
        } else if (password.length() < 6) {
            binding.textInputLayoutPassword.setError("Password must be at least 6 characters");
            hasError = true;
        }

        // Validate Confirm Password
        if (TextUtils.isEmpty(confirmPassword)) {
            binding.textInputLayoutConfirmPassword.setError("Please confirm your password");
            hasError = true;
        } else if (!password.equals(confirmPassword)) {
            binding.textInputLayoutConfirmPassword.setError("Passwords do not match");
            hasError = true;
        }

        if (hasError) {
            return;
        }

        setLoading(true);

        // Try register
        authRepository.register(email, password, username)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        navigateToMain();
                    } else {
                        String errorMessage = "Registration failed. Please try again.";
                        if (task.getException() != null) {
                            errorMessage = task.getException().getMessage();
                        }
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Set loading state for the register UI.
     *
     * <p>When loading:</p>
     * <ul>
     *   <li>Disable register button.</li>
     *   <li>Show progress bar.</li>
     *   <li>Disable all inputs.</li>
     *   <li>Disable login link.</li>
     * </ul>
     *
     * <p>Prevents duplicate submissions or input changes during the request.</p>
     *
     * @param loading true to show loading, false to hide.
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
     * Navigate to the main screen and clear back stack.
     *
     * <p>Called after successful registration. Uses intent flags to clear
     * previous activities so the user cannot go back to this screen.</p>
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
