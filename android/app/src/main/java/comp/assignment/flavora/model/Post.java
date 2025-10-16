package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

/**
 * Post model class.
 * Represents a food post in the Flavora app, including photos, description, rating, and related metadata.
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Post implements HasUUID {
    /** Unique identifier of the post. */
    private String postId;
    /** Author's user ID. */
    private String userId;
    /** Author's username (denormalized for display to avoid extra lookups). */
    private String username;
    /** Author's avatar URL (denormalized for display to avoid extra lookups). */
    private String userAvatarUrl;
    /** Post title. */
    private String title;
    /** Post description/content. */
    private String description;
    /** List of image URLs stored in Firebase Storage. */
    private List<String> imageUrls;
    /** Rating in the range 1–5 stars. */
    private double rating;
    /** Creation timestamp. */
    private Timestamp createdAt;
    /** Number of likes. */
    private int likeCount;
    /** Number of favorites. */
    private int favoriteCount;

    /** Whether the current user has liked this post (client-side only; not stored in Firestore). */
    private transient boolean isLikedByCurrentUser;
    /** Whether the current user has favorited this post (client-side only; not stored in Firestore). */
    private transient boolean isFavoritedByCurrentUser;

    /**
     * No-args constructor.
     * Required for Firestore deserialization.
     */
    public Post() {
    }

    /**
     * All-args constructor.
     *
     * @param postId         Post ID
     * @param userId         Author's user ID
     * @param username       Author's username
     * @param userAvatarUrl  Author's avatar URL
     * @param title          Post title
     * @param description    Post description
     * @param imageUrls      List of image URLs
     * @param rating         Rating (1–5 stars)
     * @param createdAt      Creation timestamp
     * @param likeCount      Number of likes
     * @param favoriteCount  Number of favorites
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
     * Returns the unique identifier of this object.
     * Implements the HasUUID interface.
     *
     * @return post ID as the UUID
     */
    @Override
    @Exclude
    public String getUUID() {
        return postId;
    }

    /**
     * Placeholder UUID setter.
     * Exists to prevent Firestore warnings; UUID is managed via postId.
     *
     * @param uuid UUID value (ignored)
     */
    @Exclude
    public void setUuid(String uuid) {
    }

    /**
     * Gets the post ID.
     * @return unique post identifier
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the post ID.
     * @param postId unique post identifier
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the author's user ID.
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the author's user ID.
     * @param userId user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the author's username.
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the author's username.
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the author's avatar URL.
     * @return avatar URL
     */
    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    /**
     * Sets the author's avatar URL.
     * @param userAvatarUrl avatar URL
     */
    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    /**
     * Gets the post title.
     * @return title text
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the post title.
     * @param title title text
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the post description.
     * @return description content
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the post description.
     * @param description description content
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the list of image URLs.
     * @return collection of image URLs
     */
    public List<String> getImageUrls() {
        return imageUrls;
    }

    /**
     * Sets the list of image URLs.
     * @param imageUrls collection of image URLs
     */
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    /**
     * Gets the rating.
     * @return rating value (1–5 stars)
     */
    public double getRating() {
        return rating;
    }

    /**
     * Sets the rating.
     * @param rating rating value (1–5 stars)
     */
    public void setRating(double rating) {
        this.rating = rating;
    }

    /**
     * Gets the creation timestamp.
     * @return Firebase Timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     * @param createdAt Firebase Timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the like count.
     * @return total likes
     */
    public int getLikeCount() {
        return likeCount;
    }

    /**
     * Sets the like count.
     * @param likeCount total likes
     */
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * Gets the favorite count.
     * @return total favorites
     */
    public int getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * Sets the favorite count.
     * @param favoriteCount total favorites
     */
    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /**
     * Checks whether the current user has liked this post.
     * @return true if liked; false otherwise
     */
    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    /**
     * Sets the current user's like state.
     * @param likedByCurrentUser true if liked; false otherwise
     */
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        isLikedByCurrentUser = likedByCurrentUser;
    }

    /**
     * Checks whether the current user has favorited this post.
     * @return true if favorited; false otherwise
     */
    public boolean isFavoritedByCurrentUser() {
        return isFavoritedByCurrentUser;
    }

    /**
     * Sets the current user's favorite state.
     * @param favoritedByCurrentUser true if favorited; false otherwise
     */
    public void setFavoritedByCurrentUser(boolean favoritedByCurrentUser) {
        isFavoritedByCurrentUser = favoritedByCurrentUser;
    }
}
