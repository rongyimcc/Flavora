package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import comp.assignment.flavora.util.IdGenerator;

/**
 * Like model class.
 * Represents a user's "like" record for a post.
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Like implements HasUUID {
    /** Like record ID in the format userId_postId; ensures a user can like the same post only once. */
    private String likeId;
    /** ID of the user who liked the post. */
    private String userId;
    /** ID of the post that was liked. */
    private String postId;
    /** Timestamp when the like was created. */
    private Timestamp createdAt;

    /**
     * No-args constructor.
     * Required for Firestore deserialization.
     */
    public Like() {
    }

    /**
     * Constructs a like record.
     * The likeId is automatically generated as userId_postId.
     *
     * @param userId    ID of the user who likes the post
     * @param postId    ID of the post being liked
     * @param createdAt Timestamp when the like is created
     */
    public Like(String userId, String postId, Timestamp createdAt) {
        this.likeId = IdGenerator.generateLikeId(userId, postId);
        this.userId = userId;
        this.postId = postId;
        this.createdAt = createdAt;
    }

    /**
     * Returns the unique identifier of this object.
     * Implements the HasUUID interface.
     *
     * @return the like record ID as the UUID
     */
    @Override
    @Exclude
    public String getUUID() {
        return likeId;
    }

    /**
     * Placeholder UUID setter.
     * Exists to avoid Firestore warnings; UUID is managed via likeId.
     *
     * @param uuid UUID value (ignored)
     */
    @Exclude
    public void setUuid(String uuid) {
    }

    /**
     * Gets the like record ID.
     * @return the unique identifier (in userId_postId format)
     */
    public String getLikeId() {
        return likeId;
    }

    /**
     * Sets the like record ID.
     * @param likeId the unique identifier of the like record
     */
    public void setLikeId(String likeId) {
        this.likeId = likeId;
    }

    /**
     * Gets the ID of the user who liked the post.
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who liked the post.
     * @param userId user ID
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the ID of the liked post.
     * @return post ID
     */
    public String getPostId() {
        return postId;
    }

    /**
     * Sets the ID of the liked post.
     * @param postId post ID
     */
    public void setPostId(String postId) {
        this.postId = postId;
    }

    /**
     * Gets the creation timestamp of the like.
     * @return Firebase Timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp of the like.
     * @param createdAt Firebase Timestamp
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
