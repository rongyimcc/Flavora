package comp.assignment.flavora.util;

/**
 * ID Generation Utility
 * <p>
 * Provides centralized ID generation logic to avoid ad-hoc string concatenation.
 * All composite IDs should be generated via this class to ensure a consistent format.
 *
 * @author Flavora Team
 */
public class IdGenerator {

    /**
     * Generates a composite ID for a like record.
     * <p>
     * Format: userId_postId
     * Uniquely identifies a user's like action on a specific post.
     *
     * @param userId the user ID
     * @param postId the post ID
     * @return the unique like record ID
     * @throws IllegalArgumentException if userId or postId is null or empty
     */
    public static String generateLikeId(String userId, String postId) {
        validateId(userId, "userId");
        validateId(postId, "postId");
        return userId + "_" + postId;
    }

    /**
     * Generates a composite ID for a favorite record.
     * <p>
     * Format: userId_postId
     * Uniquely identifies a user's favorite (bookmark) on a specific post.
     *
     * @param userId the user ID
     * @param postId the post ID
     * @return the unique favorite record ID
     * @throws IllegalArgumentException if userId or postId is null or empty
     */
    public static String generateFavoriteId(String userId, String postId) {
        validateId(userId, "userId");
        validateId(postId, "postId");
        return userId + "_" + postId;
    }

    /**
     * Validates an ID string.
     *
     * @param id        the ID value
     * @param fieldName the field name (for error messages)
     * @throws IllegalArgumentException if the ID is null or empty
     */
    private static void validateId(String id, String fieldName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
}
