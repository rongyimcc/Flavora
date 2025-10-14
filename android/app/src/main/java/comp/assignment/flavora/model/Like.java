package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import comp.assignment.flavora.util.IdGenerator;

/**
 * 点赞模型类
 * 代表用户对帖子的点赞记录
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Like implements HasUUID {
    /** 点赞记录ID，格式为 userId_postId，确保用户对同一帖子只能点赞一次 */
    private String likeId;
    /** 点赞用户ID */
    private String userId;
    /** 被点赞的帖子ID */
    private String postId;
    /** 点赞创建时间 */
    private Timestamp createdAt;

    /**
     * 无参构造函数
     * Firestore反序列化时需要
     */
    public Like() {
        // TODO
    }

    /**
     * 构造点赞记录
     * likeId会自动生成为 userId_postId 格式
     *
     * @param userId 点赞用户ID
     * @param postId 被点赞的帖子ID
     * @param createdAt 点赞时间
     */
    public Like(String userId, String postId, Timestamp createdAt) {
        // TODO
    }

    /**
     * 获取对象的唯一标识符
     * 实现HasUUID接口的方法
     *
     * @return 点赞记录ID作为UUID
     */
    @Override
    @Exclude
    public String getUUID() {
        // TODO
    }

    /**
     * UUID的setter方法（占位方法）
     * 用于防止Firestore警告，实际UUID通过likeId处理
     *
     * @param uuid UUID值（被忽略）
     */
    @Exclude
    public void setUuid(String uuid) {
        // TODO
    }

    /**
     * 获取点赞记录ID
     * @return 点赞记录唯一标识符（userId_postId格式）
     */
    public String getLikeId() {
        // TODO
    }

    /**
     * 设置点赞记录ID
     * @param likeId 点赞记录唯一标识符
     */
    public void setLikeId(String likeId) {
        // TODO
    }

    /**
     * 获取点赞用户ID
     * @return 用户ID
     */
    public String getUserId() {
        // TODO
    }

    /**
     * 设置点赞用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        // TODO
    }

    /**
     * 获取被点赞的帖子ID
     * @return 帖子ID
     */
    public String getPostId() {
        // TODO
    }

    /**
     * 设置被点赞的帖子ID
     * @param postId 帖子ID
     */
    public void setPostId(String postId) {
        // TODO
    }

    /**
     * 获取点赞创建时间
     * @return Firebase时间戳
     */
    public Timestamp getCreatedAt() {
        // TODO
    }

    /**
     * 设置点赞创建时间
     * @param createdAt Firebase时间戳
     */
    public void setCreatedAt(Timestamp createdAt) {
        // TODO
    }
}
