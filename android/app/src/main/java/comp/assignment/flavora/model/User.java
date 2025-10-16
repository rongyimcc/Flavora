package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

/**
 * User model representing a user in the Flavora app.
 * Implements HasUUID for compatibility with DAO pattern.
 */
@IgnoreExtraProperties
public class User implements HasUUID {
    private String userId;
    private String username;
    private String email;
    private String avatarUrl;
    private Timestamp createdAt;
    private int followersCount;
    private int followingCount;
    private int postsCount;

    // Firestore requires a no-argument constructor
    public User() {
    }

    public User(String userId, String username, String email,
                String avatarUrl, Timestamp createdAt, int followersCount,
                int followingCount, int postsCount) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.postsCount = postsCount;
    }

    @Override
    @Exclude
    public String getUUID() {
        return userId;
    }

    // Dummy setter to prevent Firestore warning
    @Exclude
    public void setUuid(String uuid) {
        // Intentionally empty - uuid is handled by userId
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }
}
