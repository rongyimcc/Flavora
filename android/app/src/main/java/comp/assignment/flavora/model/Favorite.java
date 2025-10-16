package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import comp.assignment.flavora.util.IdGenerator;

/**
 * Favorite model class.
 * Represents a user's favorite (bookmark) record for a post.
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Favorite implements HasUUID {
    /** Favorite record ID in the format userId_postId; ensures a user can favorite a post only once. */
    private String favoriteId;
    /** ID of the user who favorited the post. */
    private String userId;
    /** ID of the post being favorited. */
    private String postId;
    /** Timestamp when the favorite was created. */
    private Timestamp createdAt;

    /**
     * No-args constructor.
     * Required for Firestore deserialization.
     */
    public Favorite() {
    }

    /**
     * Constructs a favorite record.
     * The favoriteId is automatically generated as userId_postId.
     *
     * @param userId    ID of the user who favorites the post
     * @param postId    ID of the post being favorited
     * @param createdAt Timestamp when the favorite is created
     */
    public Favorite(String userId, String postId, Timestamp createdAt) {
        this.favoriteId = IdGenerator.generateFavoriteId(userId, postId);
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier of this object.
     * Implements the HasUUID interface.
     *
     * @return the favorite record ID as the UUID
     */
    @Override
    @Exclude
    public String getUUID() {
        return favoriteId;
    }

    /**
     * Placeholder UUID setter.
     * Exists to avoid Firestore warnings; UUID is managed via favoriteId.
     *
     * @param uuid UUID value (ignored)
     */
    @Exclude
    public void setUuid(String uuid) {
    }

    /**
     * Gets the favorite record ID.
     * @return the unique identifier (in userId_postId format)
     */
    public String getFavoriteId() {
        return favoriteId;
    }

    /**
     * Sets the favorite record ID.
     * @param favoriteId the unique identifier of the favorite record
     */
    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    /**
     * Gets the ID of the user who favorited the post.
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who favorited the post.
     * @param userId user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the favorited post.
     * @return post ID
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the ID of the favorited post.
     * @param postId post ID
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the creation timestamp of the favorite.
     * @return Firebase Timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the favorite.
     * @param createdAt Firebase Timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
