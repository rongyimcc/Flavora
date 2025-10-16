package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import comp.assignment.flavora.util.IdGenerator;

/**
 * Like model class.
 * Represents a record of a user liking a post.
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Like implements HasUUID {
    /** Like record ID in userId_postId format to enforce one like per user/post pair. */
    private String likeId;
    /** ID of the user who liked the post. */
    private String userId;
    /** ID of the post that was liked. */
    private String postId;
    /** Timestamp when the like was created. */
    private Timestamp createdAt;

    /**
     * No-arg constructor required by Firestore for deserialization.
     */
    public Like() {
    }

    /**
     * Creates a like record.
     * The likeId is generated automatically as userId_postId.
     *
     * @param userId ID of the user who liked the post.
     * @param postId ID of the liked post.
     * @param createdAt Timestamp of the like.
     */
    public Like(String userId, String postId, Timestamp createdAt) {
        this.likeId = IdGenerator.generateLikeId(userId, postId);
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier for this object.
     * Implements the HasUUID contract.
     *
     * @return Like record ID treated as the UUID.
     */
    @Override
    @Exclude
    public String getUUID() {
        return likeId;
    }

    /**
     * Setter for the UUID (placeholder).
     * Prevents Firestore warnings; the real UUID is handled via likeId.
     *
     * @param uuid UUID value (ignored).
     */
    @Exclude
    public void setUuid(String uuid) {
    }

    /**
     * Gets the like record ID.
     * @return Unique identifier in userId_postId format.
     */
    public String getLikeId() {
        return likeId;
    }

    /**
     * Sets the like record ID.
     * @param likeId Unique identifier for the like.
     */
    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    /**
     * Gets the ID of the user who liked the post.
     * @return User ID.
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who liked the post.
     * @param userId User ID.
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the liked post.
     * @return Post ID.
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the ID of the liked post.
     * @param postId Post ID.
     */
   public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the timestamp when the like was created.
     * @return Firebase timestamp.
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the like was created.
     * @param createdAt Firebase timestamp.
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
