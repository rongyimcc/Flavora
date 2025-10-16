package comp.assignment.flavora.util;

/**
 * Firestore field name constants.
 * <p>
 * Defines constants for every Firestore document field to avoid hard-coded strings.
 * Using constants helps to:
 * 1. Detect typos at compile time.
 * 2. Apply refactors consistently.
 * 3. Provide IDE auto-complete support.
 *
 * @author Flavora Team
 */
public class FirestoreFields {

    // ==================== User Collection Fields ====================

    /** User ID field. */
    public static final String USER_ID = "userId";

    /** Username field. */
    public static final String USERNAME = "username";

    /** Email field. */
    public static final String EMAIL = "email";

    /** Avatar URL field. */
    public static final String AVATAR_URL = "avatarUrl";

    /** User creation timestamp field. */
    public static final String USER_CREATED_AT = "createdAt";

    /** Follower count field. */
    public static final String FOLLOWERS_COUNT = "followersCount";

    /** Following count field. */
    public static final String FOLLOWING_COUNT = "followingCount";

    /** Post count field. */
    public static final String POSTS_COUNT = "postsCount";

    // ==================== Post Collection Fields ====================

    /** Post ID field. */
    public static final String POST_ID = "postId";

    /** Post title field. */
    public static final String TITLE = "title";

    /** Post description field. */
    public static final String DESCRIPTION = "description";

    /** Image URL list field. */
    public static final String IMAGE_URLS = "imageUrls";

    /** Rating field. */
    public static final String RATING = "rating";

    /** Like count field. */
    public static final String LIKE_COUNT = "likeCount";

    /** Favorite count field. */
    public static final String FAVORITE_COUNT = "favoriteCount";

    /** Post creation timestamp field. */
    public static final String POST_CREATED_AT = "createdAt";

    // ==================== Like Collection Fields ====================

    /** Like record ID field. */
    public static final String LIKE_ID = "likeId";

    /** Liking user ID field. */
    public static final String LIKE_USER_ID = "userId";

    /** Liked post ID field. */
    public static final String LIKE_POST_ID = "postId";

    /** Like creation timestamp field. */
    public static final String LIKE_CREATED_AT = "createdAt";

    // ==================== Favorite Collection Fields ====================

    /** Favorite record ID field. */
    public static final String FAVORITE_ID = "favoriteId";

    /** Favoriting user ID field. */
    public static final String FAVORITE_USER_ID = "userId";

    /** Favorited post ID field. */
    public static final String FAVORITE_POST_ID = "postId";

    /** Favorite creation timestamp field. */
    public static final String FAVORITE_CREATED_AT = "createdAt";

    // ==================== Common Fields ====================

    /** Generic creation timestamp field. */
    public static final String CREATED_AT = "createdAt";

    /**
     * Private constructor to prevent instantiation.
     */
    private FirestoreFields() {
        throw new AssertionError("FirestoreFields is a utility class and should not be instantiated");
    }
}
