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
 * Adapter that renders the selected images list within the create-post bottom sheet.
 * Each item shows a preview and a remove button.
 */
public class SelectedImagesAdapter extends RecyclerView.Adapter<SelectedImagesAdapter.ViewHolder> {

    /** List of local image URIs. */
    private final List<Uri> imageUris;

    /** Listener invoked when an image is removed. */
    private final OnImageRemoveListener removeListener;

    /**
     * Callback for removing an image from the selection.
     */
    public interface OnImageRemoveListener {
        /**
         * Called when the image at the given position should be removed.
         */
        void onRemove(int position);
    }

    /**
     * Creates the adapter.
     */
    public SelectedImagesAdapter(List<Uri> imageUris, OnImageRemoveListener removeListener) {
        this.imageUris = imageUris;
        this.removeListener = removeListener;
    }

    /**
     * Inflates the view holder layout.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_image, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the preview and remove handler for the given position.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imageUris.get(position);
        // Bind the local URI directly.
        holder.imageView.setImageURI(imageUri);
        // Hook up the remove button.
        holder.buttonRemove.setOnClickListener(v -> {
            if (removeListener != null) {
                removeListener.onRemove(position);
            }
        });
    }

    /**
     * Number of selected images.
     */
    @Override
    public int getItemCount() {
        return imageUris.size();
    }

    /**
     * ViewHolder holding the preview image and remove button.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        /** Preview image view. */
        ImageView imageView;
        /** Remove button. */
        ImageButton buttonRemove;

        /**
         * Binds view components for the list item.
         */
        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }
    }
}
