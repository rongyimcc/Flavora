package comp.assignment.flavora.ui.discover;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Post Image Adapter
 * <p>
 * This adapter is used to display post images in a ViewPager2 carousel.
 * It uses Glide to load images from URLs and applies CENTER_CROP scaling
 * to preserve aspect ratio while filling the view.
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ImageViewHolder> {

    private final List<String> imageUrls;

    /**
     * Constructor
     *
     * @param imageUrls List of image URLs
     */
    public PostImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Create a ViewHolder
     * <p>
     * Dynamically creates an ImageView, sets layout parameters and scaling type.
     * </p>
     *
     * @param parent Parent ViewGroup
     * @param viewType View type (not used)
     * @return An instance of ImageViewHolder
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ImageViewHolder(imageView);
    }

    /**
     * Bind data to the ViewHolder
     * <p>
     * Uses Glide to load the image URL at the specified position into the ImageView.
     * </p>
     *
     * @param holder ViewHolder instance
     * @param position Image position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        Glide.with(holder.imageView.getContext())
                .load(imageUrl)
                .centerCrop()
                .into(holder.imageView);
    }

    /**
     * Get the number of images
     *
     * @return Size of the image list
     */
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * Image ViewHolder
     * <p>
     * A simple ViewHolder that holds an ImageView for displaying an image.
     * </p>
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;

        /**
         * ViewHolder constructor
         *
         * @param imageView The ImageView to be held
         */
        ImageViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}
