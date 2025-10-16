package comp.assignment.flavora.util;

/**
 * ID generation utility.
 * <p>
 * Centralizes ID generation logic to avoid duplicating string concatenation.
 * Use this class for every composite ID to keep formats consistent.
 *
 * @author Flavora Team
 */
public class IdGenerator {

    /**
     * Generates the composite ID for a like record.
     * <p>
     * Format: userId_postId.
     * Uniquely identifies the like for a given user/post pair.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @return Unique ID for the like.
     * @throws IllegalArgumentException If userId or postId is null or blank.
     */
    public static String generateLikeId(String userId, String postId) {
        validateId(userId, "userId");
        validateId(postId, "postId");
        return userId + "_" + postId;
    }

    /**
     * Generates the composite ID for a favorite record.
     * <p>
     * Format: userId_postId.
     * Uniquely identifies the favorite for a given user/post pair.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @return Unique ID for the favorite.
     * @throws IllegalArgumentException If userId or postId is null or blank.
     */
    public static String generateFavoriteId(String userId, String postId) {
        validateId(userId, "userId");
        validateId(postId, "postId");
        return userId + "_" + postId;
    }

    /**
     * Validates whether the ID is usable.
     *
     * @param id ID string.
     * @param fieldName Field name (used in error messages).
     * @throws IllegalArgumentException If the ID is null or blank.
     */
    private static void validateId(String id, String fieldName) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty");
        }
    }
}
