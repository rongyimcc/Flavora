package comp.assignment.flavora.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.List;

/**
 * 帖子模型类
 * 代表Flavora应用中的一条美食帖子，包含美食照片、描述和评分等信息
 *
 * @author Flavora Team
 * @version 1.0
 */
@IgnoreExtraProperties
public class Post implements HasUUID {
    /** 帖子唯一标识符 */
    private String postId;
    /** 作者用户ID */
    private String userId;
    /** 作者用户名（冗余存储以便显示，避免额外查询） */
    private String username;
    /** 作者头像URL（冗余存储以便显示，避免额外查询） */
    private String userAvatarUrl;
    /** 帖子标题 */
    private String title;
    /** 帖子描述内容 */
    private String description;
    /** 图片URL列表，存储在Firebase Storage中 */
    private List<String> imageUrls;
    /** 评分，范围1-5星 */
    private double rating;
    /** 创建时间戳 */
    private Timestamp createdAt;
    /** 点赞数量 */
    private int likeCount;
    /** 收藏数量 */
    private int favoriteCount;

    /** 当前用户是否已点赞（客户端字段，不存储到Firestore） */
    private transient boolean isLikedByCurrentUser;
    /** 当前用户是否已收藏（客户端字段，不存储到Firestore） */
    private transient boolean isFavoritedByCurrentUser;

    /**
     * 无参构造函数
     * Firestore反序列化时需要
     */
    public Post() {
        // TODO
    }

    /**
     * 全参构造函数
     *
     * @param postId 帖子ID
     * @param userId 作者用户ID
     * @param username 作者用户名
     * @param userAvatarUrl 作者头像URL
     * @param title 帖子标题
     * @param description 帖子描述
     * @param imageUrls 图片URL列表
     * @param rating 评分（1-5星）
     * @param createdAt 创建时间
     * @param likeCount 点赞数
     * @param favoriteCount 收藏数
     */
    public Post(String postId, String userId, String username, String userAvatarUrl,
                String title, String description, List<String> imageUrls, double rating,
                Timestamp createdAt, int likeCount, int favoriteCount) {
                    // TODO
                }

    /**
     * 获取对象的唯一标识符
     * 实现HasUUID接口的方法
     *
     * @return 帖子ID作为UUID
     */
    @Override
    @Exclude
    public String getUUID() {
        // TODO
    }

    /**
     * UUID的setter方法（占位方法）
     * 用于防止Firestore警告，实际UUID通过postId处理
     *
     * @param uuid UUID值（被忽略）
     */
    @Exclude
    public void setUuid(String uuid) {
        // TODO
    }

    /**
     * 获取帖子ID
     * @return 帖子唯一标识符
     */
    public String getPostId() {
        // TODO
    }

    /**
     * 设置帖子ID
     * @param postId 帖子唯一标识符
     */
    public void setPostId(String postId) {
        // TODO
    }

    /**
     * 获取作者用户ID
     * @return 用户ID
     */
    public String getUserId() {
        // TODO
    }

    /**
     * 设置作者用户ID
     * @param userId 用户ID
     */
    public void setUserId(String userId) {
        // TODO
    }

    /**
     * 获取作者用户名
     * @return 用户名
     */
    public String getUsername() {
        // TODO
    }

    /**
     * 设置作者用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        // TODO
    }

    /**
     * 获取作者头像URL
     * @return 头像URL地址
     */
    public String getUserAvatarUrl() {
        // TODO
    }

    /**
     * 设置作者头像URL
     * @param userAvatarUrl 头像URL地址
     */
    public void setUserAvatarUrl(String userAvatarUrl) {
        // TODO
    }

    /**
     * 获取帖子标题
     * @return 标题文本
     */
    public String getTitle() {
        // TODO
    }

    /**
     * 设置帖子标题
     * @param title 标题文本
     */
    public void setTitle(String title) {
        // TODO
    }

    /**
     * 获取帖子描述
     * @return 描述内容
     */
    public String getDescription() {
        // TODO
    }

    /**
     * 设置帖子描述
     * @param description 描述内容
     */
    public void setDescription(String description) {
        // TODO
    }

    /**
     * 获取图片URL列表
     * @return 图片URL集合
     */
    public List<String> getImageUrls() {
        // TODO
    }

    /**
     * 设置图片URL列表
     * @param imageUrls 图片URL集合
     */
    public void setImageUrls(List<String> imageUrls) {
        // TODO
    }

    /**
     * 获取评分
     * @return 评分值（1-5星）
     */
    public double getRating() {
        // TODO
    }

    /**
     * 设置评分
     * @param rating 评分值（1-5星）
     */
    public void setRating(double rating) {
        // TODO
    }

    /**
     * 获取创建时间
     * @return Firebase时间戳
     */
    public Timestamp getCreatedAt() {
        // TODO
    }

    /**
     * 设置创建时间
     * @param createdAt Firebase时间戳
     */
    public void setCreatedAt(Timestamp createdAt) {
        // TODO
    }

    /**
     * 获取点赞数量
     * @return 点赞总数
     */
    public int getLikeCount() {
        // TODO
    }

    /**
     * 设置点赞数量
     * @param likeCount 点赞总数
     */
    public void setLikeCount(int likeCount) {
        // TODO
    }

    /**
     * 获取收藏数量
     * @return 收藏总数
     */
    public int getFavoriteCount() {
        // TODO
    }

    /**
     * 设置收藏数量
     * @param favoriteCount 收藏总数
     */
    public void setFavoriteCount(int favoriteCount) {
        // TODO
    }

    /**
     * 检查当前用户是否已点赞
     * @return true表示已点赞，false表示未点赞
     */
    public boolean isLikedByCurrentUser() {
        // TODO
    }

    /**
     * 设置当前用户点赞状态
     * @param likedByCurrentUser true表示已点赞，false表示未点赞
     */
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        // TODO
    }

    /**
     * 检查当前用户是否已收藏
     * @return true表示已收藏，false表示未收藏
     */
    public boolean isFavoritedByCurrentUser() {
        // TODO
    }

    /**
     * 设置当前用户收藏状态
     * @param favoritedByCurrentUser true表示已收藏，false表示未收藏
     */
    public void setFavoritedByCurrentUser(boolean favoritedByCurrentUser) {
        // TODO
    }
}
