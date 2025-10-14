package comp.assignment.flavora.ui.post;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import comp.assignment.flavora.R;

import java.util.List;

/**
 * 已选图片适配器
 * <p>
 * 用于在创建帖子底部表单中以水平列表方式显示用户已选择的图片。
 * 每个图片项包含图片预览和删除按钮，用户可以在发布前移除不需要的图片。
 * </p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {

    /** 图片URI列表（本地文件URI） */
    private final List<Uri> imageUris;

    /** 图片移除监听器 */
    private final OnImageRemoveListener removeListener;

    /**
     * 图片移除监听器接口
     * <p>
     * 当用户点击删除按钮时触发回调。
     * </p>
     */
    public interface OnImageRemoveListener {
        /**
         * 移除指定位置的图片
         *
         * @param position 要移除的图片位置
         */
        void onRemove(int position);
    }

    /**
     * 构造方法
     *
     * @param imageUris 图片URI列表
     * @param removeListener 图片移除监听器
     */
    public SelectedImagesAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
        // TODO
    }

    /**
     * 创建ViewHolder
     * <p>
     * 加载item_selected_image布局并创建ViewHolder实例。
     * </p>
     *
     * @param parent 父ViewGroup
     * @param viewType 视图类型（未使用）
     * @return ViewHolder实例
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO
    }

    /**
     * 绑定数据到ViewHolder
     * <p>
     * 设置图片预览并配置删除按钮的点击监听器。
     * </p>
     *
     * @param holder ViewHolder实例
     * @param position 图片位置
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // TODO
    }

    /**
     * 获取图片数量
     *
     * @return 图片列表的大小
     */
    @Override
    public int getItemCount() {
        // TODO
    }

    /**
     * 图片ViewHolder
     * <p>
     * 持有图片预览和删除按钮视图。
     * </p>
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /** 图片预览 */
        ImageView imageView;
        /** 删除按钮 */
        ImageButton buttonRemove;

        /**
         * ViewHolder构造方法
         * <p>
         * 初始化并绑定视图组件。
         * </p>
         *
         * @param itemView 列表项视图
         */
        ViewHolder(View itemView) {
            // TODO
        }
    }
}
