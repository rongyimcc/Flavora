public class MainActivity extends AppCompatActivity {

    /** SharedPreferences file name */
    private static final String PREFS_NAME = "AppPreferences";
    /** Key name for dark theme setting */
    private static final String KEY_DARK_THEME = "dark_theme";

    /** ViewBinding object for accessing views in the layout */
    private ActivityMainBinding binding;
    /** Authentication repository for managing user login status */
    private AuthRepository authRepository;

    /**
     * Activity lifecycle method - called on creation
     *
     * <p>Execution flow:</p>
     * <ol>
     *   <li>Apply saved theme settings before super.onCreate()</li>
     *   <li>Check user login status, redirect to login if not logged in</li>
     *   <li>Initialize ViewBinding and set content view</li>
     *   <li>Configure top toolbar, hide default title</li>
     *   <li>Set up bottom navigation bar and Navigation Component</li>
     *   <li>Configure navigation listener to update toolbar buttons based on current page</li>
     *   <li>Set up click events for create post and settings buttons</li>
     * </ol>
     *
     * <p>Important notes:</p>
     * <ul>
     *   <li>Must call applyTheme() before super.onCreate() to correctly apply theme</li>
     *   <li>If user is not logged in, method returns early without initializing UI</li>
     * </ul>
     *
     * @param savedInstanceState Saved instance state for restoring Activity state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Load and apply theme before calling super.onCreate()
        applyTheme();

        super.onCreate(savedInstanceState);

        // Check if user is logged in
        authRepository = AuthRepository.getInstance();
        if (!authRepository.isLoggedIn()) {
            // User not logged in, redirect to login screen
            navigateToLogin();
            return;
        }

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            // Hide default title, use custom title TextView
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Get bottom navigation view
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Configure top-level destinations, these pages won't show back button
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_discover, R.id.navigation_profile)
                .build();

        // Get NavController and associate with bottom navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Add navigation destination change listener to update toolbar title and buttons based on current page
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_discover) {
                // Discover page: show "Discover" title and create post button
                binding.toolbarTitle.setText("Discover");
                binding.buttonCreatePost.setVisibility(View.VISIBLE);
                binding.buttonSettings.setVisibility(View.GONE);
            } else if (destination.getId() == R.id.navigation_profile) {
                // Profile page: show "Profile" title and settings button
                binding.toolbarTitle.setText("Profile");
                binding.buttonCreatePost.setVisibility(View.GONE);
                binding.buttonSettings.setVisibility(View.VISIBLE);
            }
        });

        // Set up create post button click listener
        binding.buttonCreatePost.setOnClickListener(v -> {
            // Open create post BottomSheet dialog
            comp.assignment.flavora.ui.post.CreatePostBottomSheet bottomSheet =
                    new comp.assignment.flavora.ui.post.CreatePostBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "CreatePostBottomSheet");
        });

        // Set up settings button click listener
        binding.buttonSettings.setOnClickListener(v -> {
            // Open settings BottomSheet dialog
            comp.assignment.flavora.ui.profile.SettingsBottomSheet settingsBottomSheet =
                    new comp.assignment.flavora.ui.profile.SettingsBottomSheet();
            settingsBottomSheet.show(getSupportFragmentManager(), "SettingsBottomSheet");
        });
    }

    /**
     * Navigate to login screen and clear back stack
     *
     * <p>This method is used to redirect to the login screen when user is not logged in. Uses
     * special Intent flags to clear all previous Activities, preventing users from returning
     * to the main screen via the back button.</p>
     *
     * <p>Intent flags used:</p>
     * <ul>
     *   <li>FLAG_ACTIVITY_NEW_TASK - Create a new task stack</li>
     *   <li>FLAG_ACTIVITY_CLEAR_TASK - Clear all Activities in the task stack</li>
     * </ul>
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Apply saved theme preference settings
     *
     * <p>Reads the user's saved theme settings from SharedPreferences and applies them to the application.
     * This method must be called before super.onCreate() to ensure the theme is applied when the Activity is created.</p>
     *
     * <p>Theme options:</p>
     * <ul>
     *   <li>Dark theme - MODE_NIGHT_YES</li>
     *   <li>Light theme - MODE_NIGHT_NO (default)</li>
     * </ul>
     */
    private void applyTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkTheme = preferences.getBoolean(KEY_DARK_THEME, false);

        if (isDarkTheme) {
            // Apply dark theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            // Apply light theme
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}