package comp.assignment.flavora.ui.discover;

import android.content.res.ColorStateList;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import comp.assignment.flavora.R;
import comp.assignment.flavora.model.Post;

import java.util.ArrayList;
import java.util.List;

/**
 * RecyclerView adapter for rendering posts with full interaction support (like, favorite, delete).
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    /** Backing list of posts. */
    private final List<Post> posts = new ArrayList<>();

    /** Listener for post interactions. */
    private OnPostInteractionListener interactionListener;

    /** Whether the delete button should be shown (e.g., on "My Posts"). */
    private boolean showDeleteButton = false;

    /**
     * Callbacks for handling post interactions.
     */
    public interface OnPostInteractionListener {
        /**
         * Triggered when a post row is tapped.
         */
        void onPostClicked(Post post);

        /**
         * Triggered when the like button is pressed.
         */
        void onLikeClicked(Post post, int position);

        /**
         * Triggered when the favorite button is pressed.
         */
        void onFavoriteClicked(Post post, int position);

        /**
         * Triggered when the delete button is pressed (optional).
         */
        default void onDeleteClicked(Post post, int position) {
            // Default no-op.
        }
    }

    /** Assigns the interaction listener. */
    public void setOnPostInteractionListener(OnPostInteractionListener listener) {
        this.interactionListener = listener;
    }

    /** Controls visibility of the delete button and refreshes the list. */
    public void setShowDeleteButton(boolean show) {
        this.showDeleteButton = show;
        notifyDataSetChanged();
    }

    /** Replaces the dataset and refreshes the list. */
    public void setPosts(List<Post> newPosts) {
        posts.clear();
        if (newPosts != null) {
            posts.addAll(newPosts);
        }
        notifyDataSetChanged();
    }

    /** Removes a post at the given index with animation. */
    public void removePost(int position) {
        if (position >= 0 && position < posts.size()) {
            posts.remove(position);
            notifyItemRemoved(position);
        }
    }

    /** Updates a post at the specified index (e.g., like/favorite state). */
    public void updatePost(int position, Post post) {
        if (position >= 0 && position < posts.size()) {
            posts.set(position, post);
            notifyItemChanged(position);
        }
    }

    /** Inflates the post row layout. */
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    /** Binds the post for the given position. */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, position, interactionListener, showDeleteButton);
    }

    /** Number of posts displayed. */
    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * ViewHolder that manages all UI elements for a post row.
     */
    static class PostViewHolder extends RecyclerView.ViewHolder {
        /** Avatar image. */
        private final ImageView imageAvatar;
        /** Username label. */
        private final TextView textUsername;
        /** Delete button. */
        private final ImageButton buttonDelete;
        /** Image carousel view pager. */
        private final ViewPager2 viewPagerImages;
        /** Image indicator showing current position. */
        private final TextView textImageIndicator;
        /** Post title label. */
        private final TextView textTitle;
        /** Post description label. */
        private final TextView textDescription;
        /** Rating bar. */
        private final RatingBar ratingBar;
        /** Like button. */
        private final ImageButton buttonLike;
        /** Like count label. */
        private final TextView textLikeCount;
        /** Favorite button. */
        private final ImageButton buttonFavorite;
        /** Favorite count label. */
        private final TextView textFavoriteCount;
        /** Publish time label. */
        private final TextView textTime;

        /**
         * Binds all view references for the row layout.
         */
        PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.image_avatar);
            textUsername = itemView.findViewById(R.id.text_username);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            viewPagerImages = itemView.findViewById(R.id.view_pager_images);
            textImageIndicator = itemView.findViewById(R.id.text_image_indicator);
            textTitle = itemView.findViewById(R.id.text_title);
            textDescription = itemView.findViewById(R.id.text_description);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            buttonLike = itemView.findViewById(R.id.button_like);
            textLikeCount = itemView.findViewById(R.id.text_like_count);
            buttonFavorite = itemView.findViewById(R.id.button_favorite);
            textFavoriteCount = itemView.findViewById(R.id.text_favorite_count);
            textTime = itemView.findViewById(R.id.text_time);
        }

        /**
         * Binds the post data to each view and sets up listeners.
         */
        void bind(Post post, int position, OnPostInteractionListener listener, boolean showDelete) {
            // Bind username.
            textUsername.setText(post.getUsername());

            // Toggle delete button visibility.
            buttonDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);

            // Bind avatar.
            if (post.getUserAvatarUrl() != null && !post.getUserAvatarUrl().isEmpty()) {
                Glide.with(imageAvatar.getContext())
                        .load(post.getUserAvatarUrl())
                        .circleCrop()
                        .into(imageAvatar);
            } else {
                // Fallback to default avatar.
                imageAvatar.setImageResource(R.drawable.ic_person_24);
            }

            // Configure image carousel.
            if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
                PostImageAdapter imageAdapter = new PostImageAdapter(post.getImageUrls());
                viewPagerImages.setAdapter(imageAdapter);

                // Update indicator when pages change.
                viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        textImageIndicator.setText((position + 1) + "/" + post.getImageUrls().size());
                    }
                });

                // Initial indicator text.
                textImageIndicator.setText("1/" + post.getImageUrls().size());
                // Show indicator only when more than one image exists.
                textImageIndicator.setVisibility(post.getImageUrls().size() > 1 ? View.VISIBLE : View.GONE);
            }

            // Bind title and description.
            textTitle.setText(post.getTitle());
            textDescription.setText(post.getDescription());

            // Bind rating.
            ratingBar.setRating((float) post.getRating());

            // Bind like state/count.
            updateLikeButton(post.isLikedByCurrentUser());
            textLikeCount.setText(String.valueOf(post.getLikeCount()));

            // Bind favorite state/count.
            updateFavoriteButton(post.isFavoritedByCurrentUser());
            textFavoriteCount.setText(String.valueOf(post.getFavoriteCount()));

            // Bind relative publish time.
            if (post.getCreatedAt() != null) {
                long timeMillis = post.getCreatedAt().toDate().getTime();
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                        timeMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
                textTime.setText(timeAgo);
            }

            // Attach listeners.
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPostClicked(post);
                }
            });

            buttonLike.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLikeClicked(post, position);
                }
            });

            buttonFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClicked(post, position);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClicked(post, position);
                }
            });
        }

        /**
         * Updates the like button icon/tint based on state.
         */
        private void updateLikeButton(boolean isLiked) {
            if (isLiked) {
                buttonLike.setImageResource(R.drawable.ic_thumb_up_24);
                // Use brand accent color when liked.
                int brandColor = ContextCompat.getColor(itemView.getContext(), R.color.yellow_500);
                buttonLike.setImageTintList(ColorStateList.valueOf(brandColor));
            } else {
                buttonLike.setImageResource(R.drawable.ic_thumb_up_border_24);
                // Use default icon tint when not liked.
                int defaultColor = ContextCompat.getColor(itemView.getContext(), R.color.icon_color);
                buttonLike.setImageTintList(ColorStateList.valueOf(defaultColor));
            }
        }

        /**
         * Updates the favorite button icon/tint based on state.
         */
        private void updateFavoriteButton(boolean isFavorited) {
            if (isFavorited) {
                buttonFavorite.setImageResource(R.drawable.ic_bookmark_24);
                // Use brand accent color when favorited.
                int brandColor = ContextCompat.getColor(itemView.getContext(), R.color.yellow_500);
                buttonFavorite.setImageTintList(ColorStateList.valueOf(brandColor));
            } else {
                buttonFavorite.setImageResource(R.drawable.ic_bookmark_border_24);
                // Use default icon tint when not favorited.
                int defaultColor = ContextCompat.getColor(itemView.getContext(), R.color.icon_color);
                buttonFavorite.setImageTintList(ColorStateList.valueOf(defaultColor));
            }
        }
    }
}
