package comp.assignment.flavora.ui.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;


/**
 * Profile Pager Adapter
 * <p>
 * Used to manage the two tabs in the ViewPager2 of the profile screen:
 * - My Posts (MyPostsFragment)
 * - My Favorites (MyFavoritesFragment)
 * </p>
 * <p>
 * Implements lazy loading and state saving for fragments using FragmentStateAdapter.
 * </p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class ProfilePagerAdapter extends FragmentStateAdapter {

    /**
     * Constructor
     *
     * @param fragment The parent fragment (ProfileFragment)
     */
    public ProfilePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Creates the fragment for the given position
     * <p>
     * Position 0: MyPostsFragment<br>
     * Position 1: MyFavoritesFragment
     * </p>
     *
     * @param position The tab index (0 or 1)
     * @return The corresponding fragment instance
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new MyPostsFragment();
        } else {
            return new MyFavoritesFragment();
        }
    }

    /**
     * Returns the number of tabs
     *
     * @return Always returns 2 (My Posts + My Favorites)
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
