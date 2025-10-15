package comp.assignment.flavora.util;

/**
 * Firestore Field Name Constants
 * <p>
 * Defines constant names for all Firestore document fields to avoid hard-coded strings.
 * Using constants helps to:
 * 1) Catch typos at compile time
 * 2) Centralize changes during refactoring
 * 3) Enable IDE autocompletion
 *
 * @author Flavora Team
 */
public class FirestoreFields {

    // ==================== User Collection Fields ====================

    /** User ID field */
    public static final String USER_ID = "userId";

    /** Username field */
    public static final String USERNAME = "username";

    /** Email field */
    public static final String EMAIL = "email";

    /** Avatar URL field */
    public static final String AVATAR_URL = "avatarUrl";

    /** User creation timestamp field */
    public static final String USER_CREATED_AT = "createdAt";

    /** Followers count field */
    public static final String FOLLOWERS_COUNT = "followersCount";

    /** Following count field */
    public static final String FOLLOWING_COUNT = "followingCount";

    /** Posts count field */
    public static final String POSTS_COUNT = "postsCount";

    // ==================== Post Collection Fields ====================

    /** Post ID field */
    public static final String POST_ID = "postId";

    /** Post title field */
    public static final String TITLE = "title";

    /** Post description field */
    public static final String DESCRIPTION = "description";

    /** Image URL list field */
    public static final String IMAGE_URLS = "imageUrls";

    /** Rating field */
    public static final String RATING = "rating";

    /** Like count field */
    public static final String LIKE_COUNT = "likeCount";

    /** Favorite count field */
    public static final String FAVORITE_COUNT = "favoriteCount";

    /** Post creation timestamp field */
    public static final String POST_CREATED_AT = "createdAt";

    // ==================== Like Collection Fields ====================

    /** Like record ID field */
    public static final String LIKE_ID = "likeId";

    /** ID of the user who liked (field) */
    public static final String LIKE_USER_ID = "userId";

    /** ID of the liked post (field) */
    public static final String LIKE_POST_ID = "postId";

    /** Like creation timestamp field */
    public static final String LIKE_CREATED_AT = "createdAt";

    // ==================== Favorite Collection Fields ====================

    /** Favorite record ID field */
    public static final String FAVORITE_ID = "favoriteId";

    /** ID of the user who favorited (field) */
    public static final String FAVORITE_USER_ID = "userId";

    /** ID of the favorited post (field) */
    public static final String FAVORITE_POST_ID = "postId";

    /** Favorite creation timestamp field */
    public static final String FAVORITE_CREATED_AT = "createdAt";

    // ==================== Common Fields ====================

    /** Common creation timestamp field name */
    public static final String CREATED_AT = "createdAt";

    /**
     * Private constructor to prevent instantiation.
     */
    private FirestoreFields() {
        throw new AssertionError("FirestoreFields is a utility class and should not be instantiated");
    }
}
