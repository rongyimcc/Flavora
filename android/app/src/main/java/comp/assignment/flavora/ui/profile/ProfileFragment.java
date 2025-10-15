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
 * Profile Fragment
 * <p>
 * Displays the user's profile information and post management options, including two sub-pages:
 * - My Posts: Posts created by the user
 * - My Favorites: Posts favorited by the user
 * </p>
 *
 * <p>Main Features:</p>
 * <ul>
 *   <li>Show user avatar, username, and ID</li>
 *   <li>Display total number of likes received by the user</li>
 *   <li>Use ViewPager2 and TabLayout to switch between "My Posts" and "My Favorites"</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    private ProfilePagerAdapter pagerAdapter;

    /**
     * Create the fragment's view
     *
     * @param inflater LayoutInflater used to inflate the layout
     * @param container Parent view container
     * @param savedInstanceState Saved instance state
     * @return The created root view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
                             }

    /**
     * Callback after the view has been created
     * Initializes the ViewPager and loads the user's profile data.
     *
     * @param view The created view
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
        loadUserProfile();
    }

    /**
     * Sets up ViewPager2 and TabLayout
     * Configures two tabs: "My Posts" and "My Favorites"
     */
    private void setupViewPager() {
        pagerAdapter = new ProfilePagerAdapter(this);
        binding.viewPager.setAdapter(pagerAdapter);

        // Attach TabLayout to ViewPager2
        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("My Posts");
            } else {
                tab.setText("My Favorites");
            }
        }).attach();
    }

    /**
     * Loads user profile data
     * <p>
     * Actions performed:
     * 1. Get the current user's ID
     * 2. Load user info from the database (username, avatar, etc.)
     * 3. Load total number of likes received by the user
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

        // Load user info
        UserDAO.getInstance().get(userId, userTask -> {
            if (userTask.isSuccessful() && userTask.getResult() != null) {
                User user = userTask.getResult();
                displayUserInfo(user);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });

        // Load total likes received by the user
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
     * Displays user info on the screen
     * Includes username, ID, and avatar
     *
     * @param user The user object
     */
    private void displayUserInfo(User user) {
        binding.textUsername.setText(user.getUsername());

        // Display user ID as a secondary identifier
        binding.textEmail.setText("@" + user.getUsername());

        // Load avatar using Glide
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
     * Callback when the fragment view is destroyed
     * Clears ViewBinding to prevent memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}