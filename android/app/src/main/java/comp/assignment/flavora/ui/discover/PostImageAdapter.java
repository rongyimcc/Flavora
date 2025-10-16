package comp.assignment.flavora.ui.discover;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Adapter that powers the ViewPager2 image carousel for posts.
 * Loads images with Glide using CENTER_CROP to fill the view.
 */
public class PostImageAdapter extends RecyclerView.Adapter<PostImageAdapter.ImageViewHolder> {

    /** List of image URLs. */
    private final List<String> imageUrls;

    /**
     * Creates the adapter with the given image URLs.
     */
    public PostImageAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Creates the ImageView backing each carousel page.
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
     * Loads the image URL into the view for the given position.
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
     * Number of images in the carousel.
     */
    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    /**
     * Simple ViewHolder containing the ImageView.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        /** Image view displayed in the pager. */
        final ImageView imageView;

        /**
         * Binds the ImageView to the holder.
         */
        ImageViewHolder(@NonNull ImageView imageView) {
            super(imageView);
            this.imageView = imageView;
        }
    }
}
