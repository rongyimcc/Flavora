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
 * Posts Adapter
 * <p>
 * Used to display a list of posts in a RecyclerView.
 * Supports displaying, clicking, liking, favoriting, and deleting posts.
 * Each post includes user info, image carousel, title, description, rating, and interaction buttons.
 * </p>
 */
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    private final List<Post> posts = new ArrayList<>();

    private OnPostInteractionListener interactionListener;

    private boolean showDeleteButton = false;

    /**
     * Listener interface for post interactions
     * <p>
     * Defines all post-related callbacks including click, like, favorite, and delete.
     * </p>
     */
    public interface OnPostInteractionListener {
        /**
         * Called when a post is clicked
         *
         * @param post The clicked post
         */
        void onPostClicked(Post post);

        /**
         * Called when the favorite button is clicked
         *
         * @param post The favorited post
         * @param position The position of the post in the list
         */
        void onLikeClicked(Post post, int position);

        /**
         * Called when the favorite button is clicked
         *
         * @param post The favorited post
         * @param position The position of the post in the list
         */
        void onFavoriteClicked(Post post, int position);

        /**
         * Called when the delete button is clicked (optional)
         *
         * @param post The deleted post
         * @param position The position of the post in the list
         */
        default void onDeleteClicked(Post post, int position) {
            // Default implementation: do nothing
        }
    }

    /**
     * Set the interaction listener
     *
     * @param listener The listener instance
     */
    public void setOnPostInteractionListener(OnPostInteractionListener listener) {
        this.interactionListener = listener;
    }

    /**
     * Set whether to show the delete button
     * <p>
     * The delete button is shown in the personal posts page and hidden in the discover page.
     * This method will trigger a UI refresh.
     * </p>
     *
     * @param show true to show the delete button, false to hide
     */
    public void setShowDeleteButton(boolean show) {
        this.showDeleteButton = show;
        notifyDataSetChanged();
    }

    /**
     * Set the list of posts
     * <p>
     * Clears existing data and adds new posts, then refreshes the entire list.
     * Note: This method calls notifyDataSetChanged(), which may impact performance.
     * </p>
     *
     * @param newPosts The new list of posts (null to clear)
     */
    public void setPosts(List<Post> newPosts) {
        posts.clear();
        if (newPosts != null) {
            posts.addAll(newPosts);
        }
        notifyDataSetChanged();
    }

    /**
     * Remove a post from the list by position
     * <p>
     * Calls notifyItemRemoved() to trigger a deletion animation.
     * </p>
     *
     * @param position The index of the post to remove
     */
    public void removePost(int position) {
        if (position >= 0 && position < posts.size()) {
            posts.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Update a specific post in the list
     * <p>
     * Used to update like/favorite status or other dynamic post attributes.
     * </p>
     *
     * @param position The index of the post to update
     * @param post The updated post object
     */
    public void updatePost(int position, Post post) {
        if (position >= 0 && position < posts.size()) {
            posts.set(position, post);
            notifyItemChanged(position);
        }
    }

    /**
     * Create a new ViewHolder
     * <p>
     * Inflates the item_post layout and returns a PostViewHolder instance.
     * </p>
     *
     * @param parent The parent ViewGroup
     * @param viewType The type of view (not used)
     * @return A new PostViewHolder
     */
    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    /**
     * Bind data to the ViewHolder
     * <p>
     * Displays post information at the specified position.
     * </p>
     *
     * @param holder The ViewHolder instance
     * @param position The position of the post
     */
    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post, position, interactionListener, showDeleteButton);
    }

    /**
     * Get the total number of posts
     *
     * @return The number of posts
     */
    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * Post ViewHolder
     * <p>
     * Holds and manages all view components of a single post item, including
     * user info, image carousel, text content, rating bar, and interaction buttons.
     * Provides methods to bind post data and update the UI.
     * </p>
     */
    static class PostViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageAvatar;
        private final ImageButton buttonDelete;
        private final ViewPager2 viewPagerImages;
        private final TextView textImageIndicator;
        private final TextView textTitle;
        private final TextView textDescription;
        private final RatingBar ratingBar;
        private final ImageButton buttonLike;
        private final TextView textLikeCount;
        private final ImageButton buttonFavorite;
        private final TextView textFavoriteCount;
        private final TextView textTime;

        /**
         * Constructor for PostViewHolder
         * <p>
         * Initializes and binds all view components.
         * </p>
         *
         * @param itemView The root view of the item
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
         * Bind post data to the view
         * <p>
         * Binds all post information such as user details, images, text, ratings,
         * like/favorite status, and sets up interaction listeners.
         * </p>
         *
         * @param post The post to display
         * @param position The position of the post in the list
         * @param listener The interaction listener
         * @param showDelete Whether to show the delete button
         */
        void bind(Post post, int position, OnPostInteractionListener listener, boolean showDelete) {

            textUsername.setText(post.getUsername());


            buttonDelete.setVisibility(showDelete ? View.VISIBLE : View.GONE);

            // Set up image carousel
            if (post.getUserAvatarUrl() != null && !post.getUserAvatarUrl().isEmpty()) {
                Glide.with(imageAvatar.getContext())
                        .load(post.getUserAvatarUrl())
                        .circleCrop()
                        .into(imageAvatar);
            } else {
                imageAvatar.setImageResource(R.drawable.ic_person_24);
            }

            // Set up image carousel
            if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
                PostImageAdapter imageAdapter = new PostImageAdapter(post.getImageUrls());
                viewPagerImages.setAdapter(imageAdapter);

                // Update image indicator when page changes
                viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        textImageIndicator.setText((position + 1) + "/" + post.getImageUrls().size());
                    }
                });


                textImageIndicator.setText("1/" + post.getImageUrls().size());
                textImageIndicator.setVisibility(post.getImageUrls().size() > 1 ? View.VISIBLE : View.GONE);
            }


            textTitle.setText(post.getTitle());
            textDescription.setText(post.getDescription());

            ratingBar.setRating((float) post.getRating());


            updateLikeButton(post.isLikedByCurrentUser());
            textLikeCount.setText(String.valueOf(post.getLikeCount()));


            updateFavoriteButton(post.isFavoritedByCurrentUser());
            textFavoriteCount.setText(String.valueOf(post.getFavoriteCount()));

            // Set post time (relative time)
            if (post.getCreatedAt() != null) {
                long timeMillis = post.getCreatedAt().toDate().getTime();
                CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                        timeMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS,
                        DateUtils.FORMAT_ABBREV_RELATIVE);
                textTime.setText(timeAgo);
            }

            // Set click listeners
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
         * Update the like button state
         * <p>
         * Changes the icon and color based on like status:
         * - Liked: solid icon + brand yellow
         * - Not liked: outlined icon + default color
         * </p>
         *
         * @param isLiked Whether the post is liked
         */
        private void updateLikeButton(boolean isLiked) {
            if (isLiked) {
                buttonLike.setImageResource(R.drawable.ic_thumb_up_24);
                // 设置品牌颜色（黄色）
                int brandColor = ContextCompat.getColor(itemView.getContext(), R.color.yellow_500);
                buttonLike.setImageTintList(ColorStateList.valueOf(brandColor));
            } else {
                buttonLike.setImageResource(R.drawable.ic_thumb_up_border_24);
                // 设置默认图标颜色（自动适配主题）
                int defaultColor = ContextCompat.getColor(itemView.getContext(), R.color.icon_color);
                buttonLike.setImageTintList(ColorStateList.valueOf(defaultColor));
            }
        }

        /**
         * Update the favorite button state
         * <p>
         * Changes the icon and color based on favorite status:
         * - Favorited: solid icon + brand yellow
         * - Not favorited: outlined icon + default color
         * </p>
         *
         * @param isFavorited Whether the post is favorited
         */
        private void updateFavoriteButton(boolean isFavorited) {
            if (isFavorited) {
                buttonFavorite.setImageResource(R.drawable.ic_bookmark_24);

                int brandColor = ContextCompat.getColor(itemView.getContext(), R.color.yellow_500);
                buttonFavorite.setImageTintList(ColorStateList.valueOf(brandColor));
            } else {
                buttonFavorite.setImageResource(R.drawable.ic_bookmark_border_24);

                int defaultColor = ContextCompat.getColor(itemView.getContext(), R.color.icon_color);
                buttonFavorite.setImageTintList(ColorStateList.valueOf(defaultColor));
            }
        }
    }
}
