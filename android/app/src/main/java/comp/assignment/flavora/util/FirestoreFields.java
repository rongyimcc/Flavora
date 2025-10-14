package comp.assignment.flavora.util;

/**
 * Firestore字段名常量类
 * <p>
 * 定义所有Firestore文档字段的名称常量，避免硬编码字符串。
 * 使用常量可以：
 * 1. 在编译时发现拼写错误
 * 2. 便于重构时统一修改
 * 3. 提供IDE自动完成支持
 *
 * @author Flavora Team
 */
public class FirestoreFields {

    // ==================== User Collection Fields ====================

    /** 用户ID字段 */
    public static final String USER_ID = "userId";

    /** 用户名字段 */
    public static final String USERNAME = "username";

    /** 邮箱字段 */
    public static final String EMAIL = "email";

    /** 头像URL字段 */
    public static final String AVATAR_URL = "avatarUrl";

    /** 用户创建时间字段 */
    public static final String USER_CREATED_AT = "createdAt";

    /** 粉丝数量字段 */
    public static final String FOLLOWERS_COUNT = "followersCount";

    /** 关注数量字段 */
    public static final String FOLLOWING_COUNT = "followingCount";

    /** 发帖数量字段 */
    public static final String POSTS_COUNT = "postsCount";

    // ==================== Post Collection Fields ====================

    /** 帖子ID字段 */
    public static final String POST_ID = "postId";

    /** 帖子标题字段 */
    public static final String TITLE = "title";

    /** 帖子描述字段 */
    public static final String DESCRIPTION = "description";

    /** 图片URL列表字段 */
    public static final String IMAGE_URLS = "imageUrls";

    /** 评分字段 */
    public static final String RATING = "rating";

    /** 点赞数量字段 */
    public static final String LIKE_COUNT = "likeCount";

    /** 收藏数量字段 */
    public static final String FAVORITE_COUNT = "favoriteCount";

    /** 帖子创建时间字段 */
    public static final String POST_CREATED_AT = "createdAt";

    // ==================== Like Collection Fields ====================

    /** 点赞记录ID字段 */
    public static final String LIKE_ID = "likeId";

    /** 点赞用户ID字段 */
    public static final String LIKE_USER_ID = "userId";

    /** 被点赞帖子ID字段 */
    public static final String LIKE_POST_ID = "postId";

    /** 点赞创建时间字段 */
    public static final String LIKE_CREATED_AT = "createdAt";

    // ==================== Favorite Collection Fields ====================

    /** 收藏记录ID字段 */
    public static final String FAVORITE_ID = "favoriteId";

    /** 收藏用户ID字段 */
    public static final String FAVORITE_USER_ID = "userId";

    /** 被收藏帖子ID字段 */
    public static final String FAVORITE_POST_ID = "postId";

    /** 收藏创建时间字段 */
    public static final String FAVORITE_CREATED_AT = "createdAt";

    // ==================== Common Fields ====================

    /** 通用创建时间字段名 */
    public static final String CREATED_AT = "createdAt";

    /**
     * 私有构造函数，防止实例化
     */
    private FirestoreFields() {
        // TODO
    }
}
