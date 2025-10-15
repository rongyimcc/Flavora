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
 * Selected images adapter.
 * <p>
 * Used in the create-post bottom sheet to show user-selected images
 * in a horizontal list. Each item shows an image preview and a delete
 * button so the user can remove unwanted images before posting.
 * </p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {

    /** List of image URIs (local file URIs). */
    private final List<Uri> imageUris;

    /** Listener for image removal. */
    private final OnImageRemoveListener removeListener;

    /**
     * Listener interface for image removal.
     * <p>
     * Triggered when the user taps the delete button.
     * </p>
     */
    public interface OnImageRemoveListener {
        /**
         * Remove the image at the given position.
         *
         * @param position position of the image to remove
         */
        void onRemove(int position);
    }

    /**
     * Constructor method.
     *
     * @param imageUris list of image URIs
     * @param removeListener listener for image removal
     */
    public SelectedImagesAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
        this.imageUris = imageUris;
        this.removeListener = removeListener;
    }

    /**
     * Create a ViewHolder.
     * <p>
     * Inflate {@code item_selected_image} and create a ViewHolder instance.
     * </p>
     *
     * @param parent parent ViewGroup
     * @param viewType view type (unused)
     * @return ViewHolder instance
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Bind data to the ViewHolder.
     * <p>
     * Set the image preview and configure the delete button click listener.
     * </p>
     *
     * @param holder ViewHolder instance
     * @param position image position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        // Set local image URI directly
        holder.imageView.setImageURI(imageUri);
        // Set delete button click handler
        holder.buttonRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(position);
            }
        });
    }

    /**
     * Get number of images.
     *
     * @return size of the image list
     */
    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    /**
     * ViewHolder for image items.
     * <p>
     * Holds the image preview and the delete button views.
     * </p>
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /** Image preview. */
        ImageView imageView;
        /** Delete button. */
        ImageButton buttonRemove;

        /**
         * ViewHolder constructor.
         * <p>
         * Initialize and bind view components.
         * </p>
         *
         * @param itemView item view
         */
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }
    }
}
