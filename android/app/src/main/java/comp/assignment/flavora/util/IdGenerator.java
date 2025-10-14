package comp.assignment.flavora.util;

/**
 * ID生成工具类
 * <p>
 * 提供统一的ID生成逻辑，避免在多处重复拼接字符串。
 * 所有复合ID的生成都应该使用此类，确保格式一致。
 *
 * @author Flavora Team
 */
public class IdGenerator {

    /**
     * 生成点赞记录的复合ID
     * <p>
     * 格式: userId_postId
     * 用于唯一标识某个用户对某个帖子的点赞记录。
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 点赞记录的唯一ID
     * @throws IllegalArgumentException 如果userId或postId为null或空字符串
     */
    public static String generateLikeId(String userId, String postId) {
        // TODO
    }

    /**
     * 生成收藏记录的复合ID
     * <p>
     * 格式: userId_postId
     * 用于唯一标识某个用户对某个帖子的收藏记录。
     *
     * @param userId 用户ID
     * @param postId 帖子ID
     * @return 收藏记录的唯一ID
     * @throws IllegalArgumentException 如果userId或postId为null或空字符串
     */
    public static String generateFavoriteId(String userId, String postId) {
        // TODO
    }

    /**
     * 验证ID的有效性
     *
     * @param id ID字符串
     * @param fieldName 字段名称（用于错误信息）
     * @throws IllegalArgumentException 如果ID为null或空字符串
     */
    private static void validateId(String id, String fieldName) {
        // TODO
    }
}
