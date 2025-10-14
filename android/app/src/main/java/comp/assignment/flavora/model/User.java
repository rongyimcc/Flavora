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
        // TODO
    }

    public User(String userId, String username, String email,
                String avatarUrl, Timestamp createdAt, int followersCount,
                int followingCount, int postsCount) {
                    // TODO
                }

    @Override
    @Exclude
    public String getUUID() {
        // TODO
    }

    // Dummy setter to prevent Firestore warning
    @Exclude
    public void setUuid(String uuid) {
        // TODO
    }

    // Getters and Setters
    public String getUserId() {
        // TODO
    }

    public void setUserId(String userId) {
        // TODO
    }

    public String getUsername() {
        // TODO
    }

    public void setUsername(String username) {
        // TODO
    }

    public String getEmail() {
        // TODO
    }

    public void setEmail(String email) {
        // TODO
    }

    public String getAvatarUrl() {
        // TODO
    }

    public void setAvatarUrl(String avatarUrl) {
        // TODO
    }

    public Timestamp getCreatedAt() {
        // TODO
    }

    public void setCreatedAt(Timestamp createdAt) {
        // TODO
    }

    public int getFollowersCount() {
        // TODO
    }

    public void setFollowersCount(int followersCount) {
        // TODO
    }

    public int getFollowingCount() {
        // TODO
    }

    public void setFollowingCount(int followingCount) {
        // TODO
    }

    public int getPostsCount() {
        // TODO
    }

    public void setPostsCount(int postsCount) {
        // TODO
    }
}
