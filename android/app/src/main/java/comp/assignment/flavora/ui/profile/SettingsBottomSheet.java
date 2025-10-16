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
 * Bottom sheet for app settings.
 * <p>
 * Provides a dark-theme toggle and a logout action. Theme preferences are stored in
 * SharedPreferences and applied through AppCompatDelegate.
 * </p>
 *
 * @version 1.0
 */
public class SettingsBottomSheet extends BottomSheetDialogFragment {

    /** SharedPreferences file name. */
    private static final String PREFS_NAME = "AppPreferences";

    /** Preference key for the dark theme toggle. */
    private static final String KEY_DARK_THEME = "dark_theme";

    /** Switch controlling dark theme. */
    private SwitchMaterial switchDarkTheme;

    /** SharedPreferences instance. */
    private SharedPreferences preferences;

    /**
     * Inflates the bottom sheet layout, wires up controls, and applies saved theme state.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_settings, container, false);

        // Prepare SharedPreferences.
        preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Bind view references.
        switchDarkTheme = view.findViewById(R.id.switch_dark_theme);
        View buttonClose = view.findViewById(R.id.button_close);
        View buttonLogout = view.findViewById(R.id.button_logout);

        // Load current theme preference.
        boolean isDarkTheme = preferences.getBoolean(KEY_DARK_THEME, false);
        switchDarkTheme.setChecked(isDarkTheme);

        // Close button handler.
        buttonClose.setOnClickListener(v -> dismiss());

        // Theme toggle handler.
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Persist the preference.
            preferences.edit().putBoolean(KEY_DARK_THEME, isChecked).apply();

            // Apply the theme immediately.
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // Logout button handler.
        buttonLogout.setOnClickListener(v -> {
            logout();
            dismiss();
        });

        return view;
    }

    /**
     * Logs the user out and navigates back to the login screen, clearing the back stack.
     */
    private void logout() {
        // Perform logout.
        AuthRepository.getInstance().logout();

        // Navigate to the login activity.
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Clear the activity stack to prevent back navigation.
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Finish the hosting activity.
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}
