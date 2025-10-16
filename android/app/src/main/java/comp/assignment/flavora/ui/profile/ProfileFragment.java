package comp.assignment.flavora.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;
import comp.assignment.flavora.R;
import comp.assignment.flavora.dao.UserDAO;
import comp.assignment.flavora.databinding.FragmentProfileBinding;
import comp.assignment.flavora.facade.PostFacade;
import comp.assignment.flavora.model.User;
import comp.assignment.flavora.repository.AuthRepository;

/**
 * Fragment for the profile screen.
 * <p>
 * Shows personal information and post management with two tabs:
 * - My Posts: posts created by the user.
 * - My Favorites: posts the user favorited.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Displays avatar, username, and ID.</li>
 *   <li>Shows the total likes the user has received.</li>
 *   <li>Uses ViewPager2 + TabLayout to switch between "My Posts" and "My Favorites".</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 */
public class ProfileFragment extends Fragment {

    /** ViewBinding instance for accessing layout views. */
    private FragmentProfileBinding binding;

    /** ViewPager2 adapter managing the two profile tabs. */
    private ProfilePagerAdapter pagerAdapter;

    /**
     * Inflates the fragment view.
     *
     * @param inflater LayoutInflater for inflation.
     * @param container Parent container.
     * @param savedInstanceState Saved state bundle.
     * @return Root view instance.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Called after the view is created; initializes ViewPager and loads profile data.
     *
     * @param view Created view.
     * @param savedInstanceState Saved state bundle.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
        loadUserProfile();
    }

    /**
     * Configures ViewPager2 and TabLayout for the two profile tabs.
     */
    private void setupViewPager() {
        pagerAdapter = new ProfilePagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2.
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("My Posts");
            } else {
                tab.setText("My Favorites");
            }
        }).attach();
    }

    /**
     * Loads the user's profile information.
     * <p>
     * Steps:
     * 1. Retrieve the current user ID.
     * 2. Fetch user details (username, avatar, etc.).
     * 3. Load the total likes received.
     * </p>
     */
    private void loadUserProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);

        String userId = AuthRepository.getInstance().getCurrentUserId();
        if (userId == null) {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load user details.
        UserDAO.getInstance().get(userId, userTask -> {
            if (userTask.isSuccessful() && userTask.getResult() != null) {
                User user = userTask.getResult();
                displayUserInfo(user);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Load total likes received.
        PostFacade.getTotalLikesForUser(userId, likesTask -> {
            binding.progressBar.setVisibility(View.GONE);
            if (likesTask.isSuccessful() && likesTask.getResult() != null) {
                int totalLikes = likesTask.getResult();
                binding.textLikesCount.setText(String.valueOf(totalLikes));
            } else {
                binding.textLikesCount.setText("0");
            }
        });
    }

    /**
     * Displays user information (username, ID, avatar).
     *
     * @param user User entity.
     */
    private void displayUserInfo(User user) {
        binding.textUsername.setText(user.getUsername());

        // Show user ID as an auxiliary label.
        binding.textEmail.setText("@" + user.getUsername());

        // Load the avatar with Glide.
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            Glide.with(this)
                    .load(user.getAvatarUrl())
                    .circleCrop()
                    .placeholder(R.drawable.ic_person_24)
                    .error(R.drawable.ic_person_24)
                    .into(binding.imageAvatar);
        }
    }

    /**
     * Clears the binding reference when the view is destroyed to avoid leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
