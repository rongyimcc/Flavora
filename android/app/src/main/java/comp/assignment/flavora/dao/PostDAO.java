package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.Post;

import java.util.List;

/**
 * Data access object for posts.
 * Handles persistence operations for post entities.
 * Implements the singleton pattern in line with the reference-app design.
 *
 * @author Flavora Team
 * @version 1.0
 */
public class PostDAO extends DAO<Post> {
    /** Singleton instance. */
    private static PostDAO instance;
    /** Firebase data source instance. */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private PostDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton PostDAO instance.
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     *
     * @return Singleton PostDAO instance.
     */
    public static PostDAO getInstance() {
        if (instance == null) {
            synchronized (PostDAO.class) {
                if (instance == null) {
                    instance = new PostDAO();
                }
            }
        }
        return instance;
    }

    /**
     * Adds a post to the database.
     *
     * @param post     Post to add.
     * @param listener Completion callback.
     */
    @Override
    public void add(Post post, OnCompleteListener<Void> listener) {
        dataSource.addPost(post, listener);
    }

    /**
     * Retrieves a post by ID.
     *
     * @param id       Post ID.
     * @param listener Completion callback returning the post.
     */
    @Override
    public void get(String id, OnCompleteListener<Post> listener) {
        dataSource.getPost(id, listener);
    }

    /**
     * Retrieves every post.
     *
     * @param listener Completion callback with the post list.
     */
    @Override
    public void getAll(OnCompleteListener<List<Post>> listener) {
        dataSource.getAllPosts(listener);
    }

    /**
     * Deletes a post by ID.
     *
     * @param id       Post ID.
     * @param listener Completion callback.
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deletePost(id, listener);
    }

    /**
     * Updates post information.
     *
     * @param post     Post containing the updated data.
     * @param listener Completion callback.
     */
    @Override
    public void update(Post post, OnCompleteListener<Void> listener) {
        dataSource.updatePost(post, listener);
    }

    /**
     * Retrieves all posts authored by the specified user.
     *
     * @param userId   User ID.
     * @param listener Completion callback with the user's posts.
     */
    public void getPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        dataSource.getPostsByUser(userId, listener);
    }

    /**
     * Retrieves all posts ordered by creation time (newest first).
     *
     * @param listener Completion callback with the time-sorted list.
     */
    public void getPostsOrderByTime(OnCompleteListener<List<Post>> listener) {
        dataSource.getPostsOrderByTime(listener);
    }

    /**
     * Increments the like count for a post.
     *
     * @param postId   Post ID.
     * @param listener Completion callback.
     */
    public void incrementLikeCount(String postId, OnCompleteListener<Void> listener) {
        dataSource.incrementPostLikeCount(postId, listener);
    }

    /**
     * Decrements the like count for a post.
     *
     * @param postId   Post ID.
     * @param listener Completion callback.
     */
    public void decrementLikeCount(String postId, OnCompleteListener<Void> listener) {
        dataSource.decrementPostLikeCount(postId, listener);
    }

    /**
     * Retrieves every post favorited by the specified user.
     *
     * @param userId   User ID.
     * @param listener Completion callback with the user's favorites.
     */
    public void getFavoritedPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        dataSource.getFavoritedPostsByUser(userId, listener);
    }

    /**
     * Retrieves the total number of likes received across a user's posts.
     *
     * @param userId   User ID.
     * @param listener Completion callback with the total like count.
     */
    public void getTotalLikesForUser(String userId, OnCompleteListener<Integer> listener) {
        dataSource.getTotalLikesForUser(userId, listener);
    }
}
