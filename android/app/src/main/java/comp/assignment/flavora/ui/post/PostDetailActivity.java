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
 * Post Detail Activity – shows details for a single post.
 *
 * <p>Features:</p>
 * <ul>
 *   <li>Displays full post info, including title, description, rating, images, etc.</li>
 *   <li>Shows the author’s avatar and username.</li>
 *   <li>Supports browsing multiple images with ViewPager2.</li>
 *   <li>Supports like and favorite actions with real-time UI updates.</li>
 *   <li>Shows like and favorite counts.</li>
 *   <li>Shows post publish time in relative format.</li>
 * </ul>
 *
 * <p>Interactions:</p>
 * <ul>
 *   <li>Like/Unlike – tap the like button to toggle state.</li>
 *   <li>Favorite/Unfavorite – tap the favorite button to toggle state.</li>
 *   <li>Image browsing – swipe left/right to view multiple images.</li>
 *   <li>Back – tap the toolbar back button to return.</li>
 * </ul>
 *
 * <p>Technical notes:</p>
 * <ul>
 *   <li>Uses ViewBinding for view access.</li>
 *   <li>Uses Glide to load network images.</li>
 *   <li>Uses ViewPager2 for image carousel.</li>
 *   <li>Uses Facade pattern to access data layer.</li>
 *   <li>Uses Task for async network requests.</li>
 *   <li>Uses DateUtils to format relative time.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class PostDetailActivity extends AppCompatActivity {

    /** Intent extra key – post ID */
    public static final String EXTRA_POST_ID = "post_id";

    /** ViewBinding object for accessing views in the layout */
    private ActivityPostDetailBinding binding;
    /** Currently displayed post */
    private Post post;

    /**
     * Activity lifecycle – called on creation.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Initialize ViewBinding and set the content view.</li>
     *   <li>Set up the toolbar.</li>
     *   <li>Load post data.</li>
     * </ol>
     *
     * @param savedInstanceState saved instance state for restoring Activity state
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
     * Set up the toolbar.
     *
     * <p>Configure toolbar display and behavior:</p>
     * <ul>
     *   <li>Use the toolbar as ActionBar.</li>
     *   <li>Hide the default title.</li>
     *   <li>Set back navigation click to finish this Activity.</li>
     * </ul>
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Load post data.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Get the post ID from Intent.</li>
     *   <li>Validate the post ID; if invalid, show an error and finish.</li>
     *   <li>Load post data via {@code PostFacade}.</li>
     *   <li>Load the user’s interaction state for the post (liked, favorited).</li>
     *   <li>Set all data to the {@code Post} object.</li>
     *   <li>Call {@code displayPost()} to render the UI.</li>
     * </ol>
     *
     * <p>Note: This method uses nested callbacks for async requests, which may lead to callback hell.</p>
     */
    private void loadPost() {
        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        if (postId == null) {
            Toast.makeText(this, "Invalid post", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load post data
        PostFacade.getPost(postId, postTask -> {
            if (!postTask.isSuccessful() || postTask.getResult() == null) {
                Toast.makeText(this, "Failed to load post", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            post = postTask.getResult();

            // Load user interaction state (like and favorite)
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
     * Bind post content to the UI.
     *
     * <p>This method binds all fields of {@code Post} to the views, including:</p>
     * <ul>
     *   <li>User info: username, avatar</li>
     *   <li>Post content: title, description, rating</li>
     *   <li>Images: show multiple images with ViewPager2 and an indicator</li>
     *   <li>Interaction data: like count, favorite count, like state, favorite state</li>
     *   <li>Time: publish time in relative format</li>
     *   <li>Click listeners: like button, favorite button</li>
     * </ul>
     */
    private void displayPost() {
        // Set User Name
        binding.textUsername.setText(post.getUsername());

        // Set User Avatar
        if (post.getUserAvatarUrl() != null && !post.getUserAvatarUrl().isEmpty()) {
            // Load network image with Glide and circle crop
            Glide.with(this)
                    .load(post.getUserAvatarUrl())
                    .circleCrop()
                    .into(binding.imageAvatar);
        } else {
            // Default avatar
            binding.imageAvatar.setImageResource(R.drawable.ic_person_24);
        }

        // Image ViewPager
        if (post.getImageUrls() != null && !post.getImageUrls().isEmpty()) {
            PostImageAdapter imageAdapter = new PostImageAdapter(post.getImageUrls());
            binding.viewPagerImages.setAdapter(imageAdapter);

            // Register page change callback to update the indicator
            binding.viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    // Update indicator text
                    binding.textImageIndicator.setText((position + 1) + "/" + post.getImageUrls().size());
                }
            });

            binding.textImageIndicator.setText("1/" + post.getImageUrls().size());
            // Only show indicator if more than one image
            binding.textImageIndicator.setVisibility(post.getImageUrls().size() > 1 ? View.VISIBLE : View.GONE);
        }

        // Title and description
        binding.textTitle.setText(post.getTitle());
        binding.textDescription.setText(post.getDescription());

        // Rating
        binding.ratingBar.setRating((float) post.getRating());

        // Like Button and Count
        updateLikeButton(post.isLikedByCurrentUser());
        binding.textLikeCount.setText(String.valueOf(post.getLikeCount()));

        // Favorite Button and Count
        updateFavoriteButton(post.isFavoritedByCurrentUser());
        binding.textFavoriteCount.setText(String.valueOf(post.getFavoriteCount()));

        // Relative publish time
        if (post.getCreatedAt() != null) {
            long timeMillis = post.getCreatedAt().toDate().getTime();
            CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
                    timeMillis,
                    System.currentTimeMillis(),
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE);
            binding.textTime.setText(timeAgo);
        }

        // Click listeners
        binding.buttonLike.setOnClickListener(v -> handleLikeClick());
        binding.buttonFavorite.setOnClickListener(v -> handleFavoriteClick());
    }

    /**
     * Handle like button click.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Read current like state.</li>
     *   <li>Call {@code PostInteractionFacade} to toggle like.</li>
     *   <li>Update local {@code Post} and UI based on the result.</li>
     *   <li>Show an error on failure.</li>
     * </ol>
     *
     * <p>Counts update with like/unlike:</p>
     * <ul>
     *   <li>Like: count +1</li>
     *   <li>Unlike: count −1 (min 0)</li>
     * </ul>
     */
    private void handleLikeClick() {
        boolean currentStatus = post.isLikedByCurrentUser();
        PostInteractionFacade.toggleLike(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setLikedByCurrentUser(newStatus);

                if (newStatus) {
                    post.setLikeCount(post.getLikeCount() + 1);
                } else {
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                }

                updateLikeButton(newStatus);
                binding.textLikeCount.setText(String.valueOf(post.getLikeCount()));
            } else {

                String errorMessage = "Failed to update like";
                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handle favorite button click.
     *
     * <p>Flow:</p>
     * <ol>
     *   <li>Read current favorite state.</li>
     *   <li>Call {@code PostInteractionFacade} to toggle favorite.</li>
     *   <li>Update local {@code Post} and UI based on the result.</li>
     *   <li>Show an error on failure.</li>
     * </ol>
     *
     * <p>Counts update with favorite/unfavorite:</p>
     * <ul>
     *   <li>Favorite: count +1</li>
     *   <li>Unfavorite: count −1 (min 0)</li>
     * </ul>
     */
    private void handleFavoriteClick() {
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setFavoritedByCurrentUser(newStatus);

                if (newStatus) {
                    post.setFavoriteCount(post.getFavoriteCount() + 1);
                } else {
                    post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
                }

                updateFavoriteButton(newStatus);
                binding.textFavoriteCount.setText(String.valueOf(post.getFavoriteCount()));
            } else {

                String errorMessage = "Failed to update favorite";
                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Update the like button icon.
     *
     * @param isLiked true show the filled icon, false show the outlined icon
     */
    private void updateLikeButton(boolean isLiked) {
        if (isLiked) {

            binding.buttonLike.setImageResource(R.drawable.ic_thumb_up_24);
        } else {

            binding.buttonLike.setImageResource(R.drawable.ic_thumb_up_border_24);
        }
    }

    /**
     * Update the favorite button icon.
     *
     * @param isFavorited true show the filled icon, false show the outlined icon
     */
    private void updateFavoriteButton(boolean isFavorited) {
        if (isFavorited) {

            binding.buttonFavorite.setImageResource(R.drawable.ic_bookmark_24);
        } else {

            binding.buttonFavorite.setImageResource(R.drawable.ic_bookmark_border_24);
        }
    }

    /**
     * Activity lifecycle – called on destroy.
     *
     * <p>Clear the ViewBinding reference to prevent memory leaks.</p>
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
