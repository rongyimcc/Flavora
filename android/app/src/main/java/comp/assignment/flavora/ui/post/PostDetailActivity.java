package comp.assignment.flavora.ui.post;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import comp.assignment.flavora.R;
import comp.assignment.flavora.databinding.ActivityPostDetailBinding;
import comp.assignment.flavora.facade.PostFacade;
import comp.assignment.flavora.facade.PostInteractionFacade;
import comp.assignment.flavora.model.Post;
import comp.assignment.flavora.ui.discover.PostImageAdapter;

/**
 * Activity that presents the full details for a single post, including media and interaction state.
 * Supports liking, favoriting, image swiping, and displays relative timestamps.
 */
public class PostDetailActivity extends AppCompatActivity {

    /** Intent extra key for the post ID. */
    public static final String EXTRA_POST_ID = "post_id";

    /** ViewBinding instance for layout access. */
    private ActivityPostDetailBinding binding;
    /** Post being displayed. */
    private Post post;

    /**
     * Activity lifecycle entry point; inflates binding, configures the toolbar, and loads the post.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        loadPost();
    }

    /**
     * Configures the toolbar and navigation behavior.
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Loads the post data and the user's interaction state before rendering.
     */
    private void loadPost() {
        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId == null) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Fetch the post data.
        PostFacade.getPost(postId, postTask -> {
            if (!postTask.isSuccessful() || postTask.getResult() == null) {
                Toast.makeText(this, "Failed to load post", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            post = postTask.getResult();

            // Fetch like/favorite state for the current user.
            PostInteractionFacade.checkIfLiked(postId, likedTask -> {
                PostInteractionFacade.checkIfFavorited(postId, favoritedTask -> {
                    boolean isLiked = likedTask.isSuccessful() && likedTask.getResult() != null
                            && likedTask.getResult();
                    boolean isFavorited = favoritedTask.isSuccessful() && favoritedTask.getResult() != null
                            && favoritedTask.getResult();

                    post.setLikedByCurrentUser(isLiked);
                    post.setFavoritedByCurrentUser(isFavorited);

                    displayPost();
                });
            });
        });
    }

    /**
     * Binds the post data to the UI controls.
     */
    private void displayPost() {
        // Bind username.
        binding.textUsername.setText(post.getUsername());

        // Load avatar image.
        if (post.getUserAvatarUrl() != null && !post.getUserAvatarUrl().isEmpty()) {
            // Load remote avatar with Glide and circle crop.
            Glide.with(this)
                    .load(post.getUserAvatarUrl())
                    .circleCrop()
                    .into(binding.imageAvatar);
        } else {
            // Fallback to default avatar.
            binding.imageAvatar.setImageResource(R.drawable.ic_person_24);
        }

        // Configure image ViewPager.
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            PostImageAdapter imageAdapter = new PostImageAdapter(post.getImageUrls());
            binding.viewPagerImages.setAdapter(imageAdapter);

            // Update the indicator when pages change.
            binding.viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // Update indicator text (e.g., 2/5).
                    binding.textImageIndicator.setText((position + 1) + "/" + post.getImageUrls().size());
                }
            });

            // Initialize indicator.
            binding.textImageIndicator.setText("1/" + post.getImageUrls().size());
            // Show indicator only when multiple images exist.
            binding.textImageIndicator.setVisibility(post.getImageUrls().size() > 1 ? View.VISIBLE : View.GONE);
        }

        // Bind title and description.
        binding.textTitle.setText(post.getTitle());
        binding.textDescription.setText(post.getDescription());

        // Bind rating.
        binding.ratingBar.setRating((float) post.getRating());

        // Bind like button and count.
        updateLikeButton(post.isLikedByCurrentUser());
        binding.textLikeCount.setText(String.valueOf(post.getLikeCount()));

        // Bind favorite button and count.
        updateFavoriteButton(post.isFavoritedByCurrentUser());
        binding.textFavoriteCount.setText(String.valueOf(post.getFavoriteCount()));

        // Bind relative publish time.
        if (post.getCreatedAt() != null) {
            long timeMillis = post.getCreatedAt().toDate().getTime();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    timeMillis,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
            binding.textTime.setText(timeAgo);
        }

        // Hook up interaction listeners.
        binding.buttonLike.setOnClickListener(v -> handleLikeClick());
        binding.buttonFavorite.setOnClickListener(v -> handleFavoriteClick());
    }

    /**
     * Handles like button presses and keeps counts in sync with the backend response.
     */
    private void handleLikeClick() {
        boolean currentStatus = post.isLikedByCurrentUser();
        PostInteractionFacade.toggleLike(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setLikedByCurrentUser(newStatus);

                // Update like count.
                if (newStatus) {
                    post.setLikeCount(post.getLikeCount() + 1);
                } else {
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                }

                // Refresh UI state.
                updateLikeButton(newStatus);
                binding.textLikeCount.setText(String.valueOf(post.getLikeCount()));
            } else {
                // Surface errors.
                String errorMessage = "Failed to update like";
                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles favorite button presses and updates counts/UI accordingly.
     */
    private void handleFavoriteClick() {
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setFavoritedByCurrentUser(newStatus);

                // Update favorite count.
                if (newStatus) {
                    post.setFavoriteCount(post.getFavoriteCount() + 1);
                } else {
                    post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
                }

                // Refresh UI state.
                updateFavoriteButton(newStatus);
                binding.textFavoriteCount.setText(String.valueOf(post.getFavoriteCount()));
            } else {
                // Surface errors.
                String errorMessage = "Failed to update favorite";
                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Updates the like button icon based on the state.
     */
    private void updateLikeButton(boolean isLiked) {
        if (isLiked) {
            // Show filled icon when liked.
            binding.buttonLike.setImageResource(R.drawable.ic_thumb_up_24);
        } else {
            // Show outline icon when not liked.
            binding.buttonLike.setImageResource(R.drawable.ic_thumb_up_border_24);
        }
    }

    /**
     * Updates the favorite button icon based on the state.
     */
    private void updateFavoriteButton(boolean isFavorited) {
        if (isFavorited) {
            // Show filled icon when favorited.
            binding.buttonFavorite.setImageResource(R.drawable.ic_bookmark_24);
        } else {
            // Show outline icon when not favorited.
            binding.buttonFavorite.setImageResource(R.drawable.ic_bookmark_border_24);
        }
    }

    /**
     * Clears the binding reference on destroy to avoid leaks.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
