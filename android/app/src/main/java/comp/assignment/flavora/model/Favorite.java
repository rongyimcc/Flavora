package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import comp.assignment.flavora.util.IdGenerator;

/**
 * 收藏模型类
 * 代表用户对帖子的收藏记录
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Favorite implements HasUUID {
    /** 收藏记录ID，格式为 userId_postId，确保用户对同一帖子只能收藏一次 */
    private String favoriteId;
    /** 收藏用户ID */
    private String userId;
    /** 被收藏的帖子ID */
    private String postId;
    /** 收藏创建时间 */
    private Timestamp createdAt;

    /**
     * 无参构造函数
     * Firestore反序列化时需要
     */
    public Favorite() {
        // TODO
    }

    /**
     * 构造收藏记录
     * favoriteId会自动生成为 userId_postId 格式
     *
     * @param userId 收藏用户ID
     * @param postId 被收藏的帖子ID
     * @param createdAt 收藏时间
     */
    public Favorite(String userId, String postId, Timestamp createdAt) {
        // TODO
    }

    /**
     * 获取对象的唯一标识符
     * 实现HasUUID接口的方法
     *
     * @return 收藏记录ID作为UUID
     */
    @Override
    @Exclude
    public String getUUID() {
        // TODO
    }

    /**
     * UUID的setter方法（占位方法）
     * 用于防止Firestore警告，实际UUID通过favoriteId处理
     *
     * @param uuid UUID值（被忽略）
     */
    @Exclude
    public void setUuid(String uuid) {
        // TODO
    }

    /**
     * 获取收藏记录ID
     * @return 收藏记录唯一标识符（userId_postId格式）
     */
    public String getFavoriteId() {
        // TODO
    }

    /**
     * 设置收藏记录ID
     * @param favoriteId 收藏记录唯一标识符
     */
    public void setFavoriteId(String favoriteId) {
        // TODO
    }

    /**
     * 获取收藏用户ID
     * @return 用户ID
     */
    public String getUserId() {
        // TODO
    }

    /**
     * 设置收藏用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        // TODO
    }

    /**
     * 获取被收藏的帖子ID
     * @return 帖子ID
     */
    public String getPostId() {
        // TODO
    }

    /**
     * 设置被收藏的帖子ID
     * @param postId 帖子ID
     */
    public void setPostId(String postId) {
        // TODO
    }

    /**
     * 获取收藏创建时间
     * @return Firebase时间戳
     */
    public Timestamp getCreatedAt() {
        // TODO
    }

    /**
     * 设置收藏创建时间
     * @param createdAt Firebase时间戳
     */
    public void setCreatedAt(Timestamp createdAt) {
        // TODO
    }
}
