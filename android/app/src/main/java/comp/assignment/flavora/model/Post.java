package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

/**
 * Post model class.
 * Represents a Flavora food post that includes photos, description, rating, and metadata.
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Post implements HasUUID {
    /** Unique identifier for the post. */
    private String postId;
    /** Author's user ID. */
    private String userId;
    /** Author's username (stored redundantly for display to avoid extra lookups). */
    private String username;
    /** Author's avatar URL (stored redundantly for display to avoid extra lookups). */
    private String userAvatarUrl;
    /** Post title. */
    private String title;
    /** Post description content. */
    private String description;
    /** List of image URLs stored in Firebase Storage. */
    private List<String> imageUrls;
    /** Rating in the range of 1-5 stars. */
    private double rating;
    /** Creation timestamp. */
    private Timestamp createdAt;
    /** Number of likes. */
    private int likeCount;
    /** Number of favorites. */
    private int favoriteCount;

    /** Whether the current user liked the post (client-side only, not stored in Firestore). */
    private transient boolean isLikedByCurrentUser;
    /** Whether the current user favorited the post (client-side only, not stored in Firestore). */
    private transient boolean isFavoritedByCurrentUser;

    /**
     * No-arg constructor required by Firestore for deserialization.
     */
    public Post() {
    }

    /**
     * Full-argument constructor.
     *
     * @param postId Post ID.
     * @param userId Author user ID.
     * @param username Author username.
     * @param userAvatarUrl Author avatar URL.
     * @param title Post title.
     * @param description Post description.
     * @param imageUrls Image URL list.
     * @param rating Rating (1-5 stars).
     * @param createdAt Creation timestamp.
     * @param likeCount Number of likes.
     * @param favoriteCount Number of favorites.
     */
    public Post(String postId, String userId, String username, String userAvatarUrl,
                String title, String description, List<String> imageUrls, double rating,
                Timestamp createdAt, int likeCount, int favoriteCount) {
        this.postId = postId;
        this.userId = userId;
        this.username = username;
        this.userAvatarUrl = userAvatarUrl;
        this.title = title;
        this.description = description;
        this.imageUrls = imageUrls;
        this.rating = rating;
        this.createdAt = createdAt;
        this.likeCount = likeCount;
        this.favoriteCount = favoriteCount;
    }

    /**
     * Returns the unique identifier for this object.
     * Implements the HasUUID contract.
     *
     * @return Post ID treated as the UUID.
     */
    @Override
    @Exclude
    public String getUUID() {
        return postId;
    }

    /**
     * Setter for the UUID (placeholder).
     * Prevents Firestore warnings; the real UUID is handled via postId.
     *
     * @param uuid UUID value (ignored).
     */
    @Exclude
    public void setUuid(String uuid) {
    }

    /**
     * Gets the post ID.
     * @return Unique identifier of the post.
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the post ID.
     * @param postId Unique identifier of the post.
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the author user ID.
     * @return User ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the author user ID.
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the author username.
     * @return Username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the author username.
     * @param username Username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the author avatar URL.
     * @return Avatar URL.
     */
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    /**
     * Sets the author avatar URL.
     * @param userAvatarUrl Avatar URL.
     */
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    /**
     * Gets the post title.
     * @return Title text.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the post title.
     * @param title Title text.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the post description.
     * @return Description content.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the post description.
     * @param description Description content.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list of image URLs.
     * @return Collection of image URLs.
     */
    public List<String> getImageUrls() {
        return imageUrls;
    }

    /**
     * Sets the list of image URLs.
     * @param imageUrls Collection of image URLs.
     */
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Gets the rating.
     * @return Rating value (1-5 stars).
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the rating.
     * @param rating Rating value (1-5 stars).
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Gets the creation timestamp.
     * @return Firebase timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     * @param createdAt Firebase timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the number of likes.
     * @return Total likes.
     */
    public int getLikeCount() {
        return likeCount;
    }

    /**
     * Sets the number of likes.
     * @param likeCount Total likes.
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Gets the number of favorites.
     * @return Total favorites.
     */
    public int getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * Sets the number of favorites.
     * @param favoriteCount Total favorites.
     */
    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /**
     * Checks whether the current user liked the post.
     * @return true if liked, false otherwise.
     */
    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    /**
     * Sets whether the current user liked the post.
     * @param likedByCurrentUser true if liked, false otherwise.
     */
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    /**
     * Checks whether the current user favorited the post.
     * @return true if favorited, false otherwise.
     */
    public boolean isFavoritedByCurrentUser() {
        return isFavoritedByCurrentUser;
    }

    /**
     * Sets whether the current user favorited the post.
     * @param favoritedByCurrentUser true if favorited, false otherwise.
     */
    public void setFavoritedByCurrentUser(boolean favoritedByCurrentUser) {
        isFavoritedByCurrentUser = favoritedByCurrentUser;
    }
}
