package comp.assignment.flavora.ui.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * Pager adapter for the profile screen.
 * <p>
 * Hosts two tabs within ViewPager2:
 * - My Posts ({@link MyPostsFragment})
 * - My Favorites ({@link MyFavoritesFragment})
 * </p>
 * <p>
 * Uses FragmentStateAdapter for lazy loading and state retention.
 * </p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class ProfilePagerAdapter extends FragmentStateAdapter {

    /**
     * Creates the adapter.
     *
     * @param fragment Parent ProfileFragment.
     */
    public ProfilePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * Creates the fragment associated with the supplied position.
     * <p>
     * Position 0: MyPostsFragment.
     * Position 1: MyFavoritesFragment.
     * </p>
     *
     * @param position Tab index (0 or 1).
     * @return Fragment for the requested position.
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
     * Returns the number of tabs.
     *
     * @return Always 2 (My Posts + My Favorites).
     */
    @Override
    public int getItemCount() {
        return 2;
    }
}
