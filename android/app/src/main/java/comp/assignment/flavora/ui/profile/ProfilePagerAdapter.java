package comp.assignment.flavora.ui.profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * 个人资料页面适配器
 * <p>
 * 用于在ViewPager2中管理个人资料页面的两个标签页：
 * - 我的帖子（MyPostsFragment）
 * - 我的收藏（MyFavoritesFragment）
 * </p>
 * <p>
 * 使用FragmentStateAdapter实现Fragment的懒加载和状态保存。
 * </p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class ProfilePagerAdapter extends FragmentStateAdapter {

    /**
     * 构造方法
     *
     * @param fragment 父Fragment（ProfileFragment）
     */
    public ProfilePagerAdapter(@NonNull Fragment fragment) {
        // TODO
    }

    /**
     * 根据位置创建对应的Fragment
     * <p>
     * 位置0：我的帖子Fragment
     * 位置1：我的收藏Fragment
     * </p>
     *
     * @param position 标签位置（0或1）
     * @return 对应位置的Fragment实例
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // TODO
    }

    /**
     * 获取标签页数量
     *
     * @return 固定返回2（我的帖子 + 我的收藏）
     */
    @Override
    public int getItemCount() {
        // TODO
    }
}
