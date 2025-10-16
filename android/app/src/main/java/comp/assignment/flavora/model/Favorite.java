package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import comp.assignment.flavora.util.IdGenerator;

/**
 * Favorite model class.
 * Represents a record of a user bookmarking a post.
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Favorite implements HasUUID {
    /** Favorite record ID in userId_postId format to ensure one favorite per user/post pair. */
    private String favoriteId;
    /** ID of the user who favorited the post. */
    private String userId;
    /** ID of the post that was favorited. */
    private String postId;
    /** Timestamp when the favorite was created. */
    private Timestamp createdAt;

    /**
     * No-arg constructor required by Firestore for deserialization.
     */
    public Favorite() {
    }

    /**
     * Creates a favorite record.
     * The favoriteId is generated automatically as userId_postId.
     *
     * @param userId ID of the user who favorited the post.
     * @param postId ID of the favorited post.
     * @param createdAt Timestamp of the favorite.
     */
    public Favorite(String userId, String postId, Timestamp createdAt) {
        this.favoriteId = IdGenerator.generateFavoriteId(userId, postId);
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier for this object.
     * Implements the HasUUID contract.
     *
     * @return Favorite record ID treated as the UUID.
     */
    @Override
    @Exclude
    public String getUUID() {
        return favoriteId;
    }

    /**
     * Setter for the UUID (placeholder).
     * Prevents Firestore warnings; the real UUID is handled via favoriteId.
     *
     * @param uuid UUID value (ignored).
     */
    @Exclude
    public void setUuid(String uuid) {
    }

    /**
     * Gets the favorite record ID.
     * @return Unique identifier in userId_postId format.
     */
    public String getFavoriteId() {
        return favoriteId;
    }

    /**
     * Sets the favorite record ID.
     * @param favoriteId Unique identifier for the favorite.
     */
    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    /**
     * Gets the ID of the user who favorited the post.
     * @return User ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who favorited the post.
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the favorited post.
     * @return Post ID.
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the ID of the favorited post.
     * @param postId Post ID.
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the timestamp when the favorite was created.
     * @return Firebase timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the favorite was created.
     * @param createdAt Firebase timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
