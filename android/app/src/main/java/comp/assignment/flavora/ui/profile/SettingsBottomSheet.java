package comp.assignment.flavora.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
import comp.assignment.flavora.R;
import comp.assignment.flavora.repository.AuthRepository;
import comp.assignment.flavora.ui.auth.LoginActivity;

/**
 * Settings bottom sheet.
 * <p>
 * A bottom-sheet dialog showing app settings, including:
 * - Dark theme toggle
 * - Logout button
 * </p>
 * <p>
 * Theme preference is persisted with SharedPreferences and applied in real time via AppCompatDelegate.
 * </p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class SettingsBottomSheet extends BottomSheetDialogFragment {

    /** SharedPreferences name */
    private static final String PREFS_NAME = "AppPreferences";

    /** Preference key for dark theme. */
    private static final String KEY_DARK_THEME = "dark_theme";

    /** Dark theme switch. */
    private SwitchMaterial switchDarkTheme;

    /** SharedPreferences instance. */
    private SharedPreferences preferences;

    /**
     * Create view.
     * <p>
     * Inflate the settings layout, initialize views and SharedPreferences,
     * attach listeners, and load the saved theme setting.
     * </p>
     *
     * @param inflater layout inflater
     * @param container parent container
     * @param savedInstanceState saved instance state
     * @return created view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_settings, container, false);

        // Initialize SharedPreferences
        preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        switchDarkTheme = view.findViewById(R.id.switch_dark_theme);
        View buttonClose = view.findViewById(R.id.button_close);
        View buttonLogout = view.findViewById(R.id.button_logout);

        // Load current theme state
        boolean isDarkTheme = preferences.getBoolean(KEY_DARK_THEME, false);
        switchDarkTheme.setChecked(isDarkTheme);

        // Close button listener
        buttonClose.setOnClickListener(v -> dismiss());

        // Theme toggle listener
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Persist theme preference
            preferences.edit().putBoolean(KEY_DARK_THEME, isChecked).apply();

            // Apply
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Logout button listener
        buttonLogout.setOnClickListener(v -> {
            logout();
            dismiss();
        });

        return view;
                             }

    /**
     * Perform logout.
     * <p>
     * Calls AuthRepository to sign out, clears the user session, and navigates to Login.
     * Uses FLAG_ACTIVITY_NEW_TASK and FLAG_ACTIVITY_CLEAR_TASK to clear the back stack
     * and prevent returning to signed-out pages.
     * </p>
     */
    private void logout() {
        // Sign out
        AuthRepository.getInstance().logout();

        // Navigate to Login
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear activity stack to prevent back navigation
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish current Activity
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
