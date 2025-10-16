package comp.assignment.flavora.dao;

import com.google.android.gms.tasks.OnCompleteListener;
import comp.assignment.flavora.datasource.FirebaseDataSource;
import comp.assignment.flavora.model.Post;

import java.util.List;

/**
 * Post Data Access Object (DAO)
 * Handles all data access operations related to Post entities.
 * Implements the Singleton pattern and follows the reference-app design conventions.
 *
 * @author
 * Flavora Team
 * @version 1.0
 */
public class PostDAO extends DAO<Post> {
    /** Singleton instance */
    private static PostDAO instance;
    /** Firebase data source instance */
    private final FirebaseDataSource dataSource;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the Firebase data source.
     */
    private PostDAO() {
        this.dataSource = FirebaseDataSource.getInstance();
    }

    /**
     * Returns the singleton instance of PostDAO.
     * Implements thread-safe lazy initialization using Double-Checked Locking (DCL).
     *
     * @return PostDAO singleton instance
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
     * @param post     the post to add
     * @param listener completion listener for operation callback
     */
    @Override
    public void add(Post post, OnCompleteListener<Void> listener) {
        dataSource.addPost(post, listener);
    }

    /**
     * Retrieves a post by ID.
     *
     * @param id       the post ID
     * @param listener completion listener returning the Post object
     */
    @Override
    public void get(String id, OnCompleteListener<Post> listener) {
        dataSource.getPost(id, listener);
    }

    /**
     * Retrieves all posts.
     *
     * @param listener completion listener returning a list of posts
     */
    @Override
    public void getAll(OnCompleteListener<List<Post>> listener) {
        dataSource.getAllPosts(listener);
    }

    /**
     * Deletes a post by ID.
     *
     * @param id       the post ID
     * @param listener completion listener for operation callback
     */
    @Override
    public void delete(String id, OnCompleteListener<Void> listener) {
        dataSource.deletePost(id, listener);
    }

    /**
     * Updates post information.
     *
     * @param post     the post object containing updated data
     * @param listener completion listener for operation callback
     */
    @Override
    public void update(Post post, OnCompleteListener<Void> listener) {
        dataSource.updatePost(post, listener);
    }

    /**
     * Retrieves all posts published by a specific user.
     *
     * @param userId   the user ID
     * @param listener completion listener returning the user's posts
     */
    public void getPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        dataSource.getPostsByUser(userId, listener);
    }

    /**
     * Retrieves all posts ordered by creation time (newest first).
     *
     * @param listener completion listener returning a time-ordered list of posts
     */
    public void getPostsOrderByTime(OnCompleteListener<List<Post>> listener) {
        dataSource.getPostsOrderByTime(listener);
    }

    /**
     * Increments a post's like count.
     *
     * @param postId   the post ID
     * @param listener completion listener for operation callback
     */
    public void incrementLikeCount(String postId, OnCompleteListener<Void> listener) {
        dataSource.incrementPostLikeCount(postId, listener);
    }

    /**
     * Decrements a post's like count.
     *
     * @param postId   the post ID
     * @param listener completion listener for operation callback
     */
    public void decrementLikeCount(String postId, OnCompleteListener<Void> listener) {
        dataSource.decrementPostLikeCount(postId, listener);
    }

    /**
     * Retrieves all posts favorited by a specific user.
     *
     * @param userId   the user ID
     * @param listener completion listener returning a list of favorited posts
     */
    public void getFavoritedPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        dataSource.getFavoritedPostsByUser(userId, listener);
    }

    /**
     * Retrieves the total number of likes received across all posts of a user.
     *
     * @param userId   the user ID
     * @param listener completion listener returning the total like count
     */
    public void getTotalLikesForUser(String userId, OnCompleteListener<Integer> listener) {
        dataSource.getTotalLikesForUser(userId, listener);
    }
}
