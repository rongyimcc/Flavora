package comp.assignment.flavora;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import comp.assignment.flavora.databinding.ActivityMainBinding;
import comp.assignment.flavora.repository.AuthRepository;
import comp.assignment.flavora.ui.auth.LoginActivity;

/**
 * Main activity that hosts the primary UI for Flavora.
 *
 * <p>Responsibilities:</p>
 * <ul>
 *   <li>Manages the main navigation structure (bottom nav + fragment switching).</li>
 *   <li>Validates the user's login state and redirects to login when necessary.</li>
 *   <li>Applies the saved theme preference (dark/light).</li>
 *   <li>Provides the top toolbar with create-post and settings actions.</li>
 *   <li>Uses the Navigation Component for fragment navigation.</li>
 * </ul>
 *
 * <p>Navigation destinations:</p>
 * <ul>
 *   <li>Discover - browse food posts.</li>
 *   <li>Profile - view personal information.</li>
 * </ul>
 *
 * <p>Implementation notes:</p>
 * <ul>
 *   <li>Relies on ViewBinding instead of findViewById.</li>
 *   <li>Stores theme preferences in SharedPreferences.</li>
 *   <li>Leverages the Navigation Component for fragment transitions.</li>
 *   <li>Shows BottomSheets for creating posts and opening settings.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class MainActivity extends AppCompatActivity {

    /** SharedPreferences file name. */
    private static final String PREFS_NAME = "AppPreferences";
    /** Preference key for the dark theme toggle. */
    private static final String KEY_DARK_THEME = "dark_theme";

    /** ViewBinding instance for accessing the layout's views. */
    private ActivityMainBinding binding;
    /** Authentication repository used to check login state. */
    private AuthRepository authRepository;

    /**
     * Activity lifecycle entry point for creation.
     *
     * <p>Sequence:</p>
     * <ol>
     *   <li>Apply the saved theme before calling super.onCreate().</li>
     *   <li>Check whether the user is logged in; redirect if not.</li>
     *   <li>Inflate ViewBinding and set the content view.</li>
     *   <li>Configure the toolbar (hide the default title).</li>
     *   <li>Set up the bottom navigation and Navigation Component.</li>
     *   <li>Add a destination listener to adjust toolbar actions.</li>
     *   <li>Wire up the create-post and settings button handlers.</li>
     * </ol>
     *
     * <p>Notes:</p>
     * <ul>
     *   <li>Call applyTheme() before super.onCreate() so the theme is applied correctly.</li>
     *   <li>If the user is not logged in, the method returns early without initializing the UI.</li>
     * </ul>
     *
     * @param savedInstanceState Saved state bundle used to restore the Activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load and apply the saved theme before super.onCreate().
        applyTheme();

        super.onCreate(savedInstanceState);

        // Verify that the user is logged in.
        authRepository = AuthRepository.getInstance();
        if (!authRepository.isLoggedIn()) {
            // User not logged in; redirect to the login screen.
            navigateToLogin();
            return;
        }

        // Initialize ViewBinding.
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configure the toolbar.
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            // Hide the default title and rely on the custom TextView.
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Grab the bottom navigation view.
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Configure top-level destinations so they do not show the Up button.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_discover, R.id.navigation_profile)
                .build();

        // Obtain the NavController and connect it to the bottom navigation.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Listen for navigation changes to update toolbar labels and actions.
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_discover) {
                // Discover screen: show "Discover" and the create-post button.
                binding.toolbarTitle.setText("Discover");
                binding.buttonCreatePost.setVisibility(View.VISIBLE);
                binding.buttonSettings.setVisibility(View.GONE);
            } else if (destination.getId() == R.id.navigation_profile) {
                // Profile screen: show "Profile" and the settings button.
                binding.toolbarTitle.setText("Profile");
                binding.buttonCreatePost.setVisibility(View.GONE);
                binding.buttonSettings.setVisibility(View.VISIBLE);
            }
        });

        // Hook up the create-post button.
        binding.buttonCreatePost.setOnClickListener(v -> {
            // Open the BottomSheet used to create a post.
            comp.assignment.flavora.ui.post.CreatePostBottomSheet bottomSheet =
                    new comp.assignment.flavora.ui.post.CreatePostBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "CreatePostBottomSheet");
        });

        // Hook up the settings button.
        binding.buttonSettings.setOnClickListener(v -> {
            // Open the settings BottomSheet dialog.
            comp.assignment.flavora.ui.profile.SettingsBottomSheet settingsBottomSheet =
                    new comp.assignment.flavora.ui.profile.SettingsBottomSheet();
            settingsBottomSheet.show(getSupportFragmentManager(), "SettingsBottomSheet");
        });
    }

    /**
     * Redirects to the login activity and clears the back stack.
     *
     * <p>Used when the user is not authenticated. Applies intent flags so the user cannot
     * navigate back to the main screen.</p>
     *
     * <p>Intent flags:</p>
     * <ul>
     *   <li>FLAG_ACTIVITY_NEW_TASK - start a new task.</li>
     *   <li>FLAG_ACTIVITY_CLEAR_TASK - clear any existing activities.</li>
     * </ul>
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Applies the saved theme preference.
     *
     * <p>Reads the user's theme setting from SharedPreferences and applies it. Must be called
     * before super.onCreate() so the theme takes effect during Activity creation.</p>
     *
     * <p>Theme options:</p>
     * <ul>
     *   <li>Dark theme - MODE_NIGHT_YES.</li>
     *   <li>Light theme - MODE_NIGHT_NO (default).</li>
     * </ul>
     */
    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(KEY_DARK_THEME, false);

        if (isDarkTheme) {
            // Apply dark theme.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Apply light theme.
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}
