package comp.assignment.flavora.datasource;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;
import comp.assignment.flavora.model.Favorite;
import comp.assignment.flavora.model.Like;
import comp.assignment.flavora.model.Post;
import comp.assignment.flavora.model.User;
import comp.assignment.flavora.util.FirestoreFields;
import comp.assignment.flavora.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Firebase data source.
 * <p>
 * Encapsulates every Firebase operation and provides an abstraction layer between Firebase and the DAO.
 * Makes it easier to swap data sources and improves maintainability and testability.
 * </p>
 *
 * <p>Design highlights:</p>
 * <ul>
 *   <li>Singleton: single global instance with thread safety.</li>
 *   <li>Unified entry point: all Firebase operations flow through this class.</li>
 *   <li>Asynchronous operations: uses OnCompleteListener for async callbacks.</li>
 *   <li>Atomic operations: relies on WriteBatch to keep data consistent.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class FirebaseDataSource {
    /** Log tag. */
    private static final String TAG = "FirebaseDataSource";

    /** Singleton instance. */
    private static FirebaseDataSource instance;

    /** Firestore database instance. */
    private final FirebaseFirestore db;

    /** Firebase auth instance. */
    private final FirebaseAuth auth;

    /** Firebase storage instance. */
    private final FirebaseStorage storage;

    /** Users collection name. */
    private static final String COLLECTION_USERS = "users";

    /** Posts collection name. */
    private static final String COLLECTION_POSTS = "posts";

    /** Likes collection name. */
    private static final String COLLECTION_LIKES = "likes";

    /** Favorites collection name. */
    private static final String COLLECTION_FAVORITES = "favorites";

    /**
     * Private constructor.
     * Initializes the Firebase components.
     */
    private FirebaseDataSource() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Returns the singleton FirebaseDataSource instance.
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     *
     * @return Singleton FirebaseDataSource instance.
     */
    public static FirebaseDataSource getInstance() {
        if (instance == null) {
            synchronized (FirebaseDataSource.class) {
                if (instance == null) {
                    instance = new FirebaseDataSource();
                }
            }
        }
        return instance;
    }

    // ==================== User Operations ====================

    /**
     * Adds a user to the Firestore database.
     *
     * @param user User to insert.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void addUser(User user, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param userId User ID.
     * @param listener Completion listener returning the User or an exception.
     */
    public void getUser(String userId, OnCompleteListener<User> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        User user = task.getResult().toObject(User.class);
                        listener.onComplete(Tasks.forResult(user));
                    } else {
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ? task.getException() :
                                        new Exception("User not found")));
                    }
                });
    }

    /**
     * Retrieves the full list of users.
     *
     * @param listener Completion listener returning the user list or an exception.
     */
    public void getAllUsers(OnCompleteListener<List<User>> listener) {
        db.collection(COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> users = task.getResult().toObjects(User.class);
                        listener.onComplete(Tasks.forResult(users));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Updates user information.
     *
     * @param user User containing the new data.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void updateUser(User user, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Deletes a user.
     *
     * @param userId ID of the user to remove.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void deleteUser(String userId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a user by username.
     *
     * @param username Username to search.
     * @param listener Completion listener returning the User or null if not found.
     */
    public void getUserByUsername(String username, OnCompleteListener<User> listener) {
        db.collection(COLLECTION_USERS)
                .whereEqualTo(FirestoreFields.USERNAME, username)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        User user = task.getResult().getDocuments().get(0).toObject(User.class);
                        listener.onComplete(Tasks.forResult(user));
                    } else {
                        listener.onComplete(Tasks.forResult(null));
                    }
                });
    }

    /**
     * Increments the post count for a user.
     *
     * @param userId User ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void incrementUserPostsCount(String userId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .update(FirestoreFields.POSTS_COUNT, FieldValue.increment(1))
                .addOnCompleteListener(listener);
    }

    /**
     * Decrements the post count for a user.
     *
     * @param userId User ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void decrementUserPostsCount(String userId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .update(FirestoreFields.POSTS_COUNT, FieldValue.increment(-1))
                .addOnCompleteListener(listener);
    }

    // ==================== Post Operations ====================

    /**
     * Adds a post to the Firestore database.
     *
     * @param post Post to insert.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void addPost(Post post, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(post.getPostId())
                .set(post)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a post by ID.
     *
     * @param postId Post ID.
     * @param listener Completion listener returning the Post or an exception.
     */
    public void getPost(String postId, OnCompleteListener<Post> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Post post = task.getResult().toObject(Post.class);
                        listener.onComplete(Tasks.forResult(post));
                    } else {
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ? task.getException() :
                                        new Exception("Post not found")));
                    }
                });
    }

    /**
     * Retrieves the full list of posts.
     *
     * @param listener Completion listener returning the post list or an exception.
     */
    public void getAllPosts(OnCompleteListener<List<Post>> listener) {
        db.collection(COLLECTION_POSTS)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> posts = task.getResult().toObjects(Post.class);
                        listener.onComplete(Tasks.forResult(posts));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves posts ordered by creation time in descending order (newest first).
     *
     * @param listener Completion listener returning the time-ordered post list or an exception.
     */
    public void getPostsOrderByTime(OnCompleteListener<List<Post>> listener) {
        db.collection(COLLECTION_POSTS)
                .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> posts = task.getResult().toObjects(Post.class);
                        listener.onComplete(Tasks.forResult(posts));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves all posts published by the specified user.
     *
     * @param userId User ID.
     * @param listener Completion listener returning the user's posts or an exception.
     */
    public void getPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        db.collection(COLLECTION_POSTS)
                .whereEqualTo(FirestoreFields.USER_ID, userId)
                .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Post> posts = task.getResult().toObjects(Post.class);
                        listener.onComplete(Tasks.forResult(posts));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Updates post information.
     *
     * @param post Post containing the new data.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void updatePost(Post post, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(post.getPostId())
                .set(post)
                .addOnCompleteListener(listener);
    }

    /**
     * Deletes a post.
     *
     * @param postId ID of the post to remove.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void deletePost(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Increments the like count for a post.
     *
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void incrementPostLikeCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.LIKE_COUNT, FieldValue.increment(1))
                .addOnCompleteListener(listener);
    }

    /**
     * Decrements the like count for a post.
     *
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void decrementPostLikeCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.LIKE_COUNT, FieldValue.increment(-1))
                .addOnCompleteListener(listener);
    }

    // ==================== Like Operations ====================

    /**
     * Adds a like record.
     *
     * @param like Like entity.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void addLike(Like like, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_LIKES)
                .document(like.getLikeId())
                .set(like)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a like record by ID.
     *
     * @param likeId Like ID.
     * @param listener Completion listener returning the Like or null when missing.
     */
    public void getLike(String likeId, OnCompleteListener<Like> listener) {
        db.collection(COLLECTION_LIKES)
                .document(likeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Like like = task.getResult().toObject(Like.class);
                        listener.onComplete(Tasks.forResult(like));
                    } else {
                        listener.onComplete(Tasks.forResult(null));
                    }
                });
    }

    /**
     * Retrieves every like record.
     *
     * @param listener Completion listener returning the like list or an exception.
     */
    public void getAllLikes(OnCompleteListener<List<Like>> listener) {
        db.collection(COLLECTION_LIKES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Like> likes = task.getResult().toObjects(Like.class);
                        listener.onComplete(Tasks.forResult(likes));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Deletes a like record.
     *
     * @param likeId ID of the like to remove.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void deleteLike(String likeId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_LIKES)
                .document(likeId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Atomic operation: add a like and increment the post like count.
     * Uses WriteBatch so both updates succeed or fail together, keeping data consistent.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void addLikeAndIncrementCount(String userId, String postId, OnCompleteListener<Void> listener) {
        String likeId = IdGenerator.generateLikeId(userId, postId);
        Like like = new Like(userId, postId, Timestamp.now());

        WriteBatch batch = db.batch();
        batch.set(db.collection(COLLECTION_LIKES).document(likeId), like);
        batch.update(db.collection(COLLECTION_POSTS).document(postId),
                FirestoreFields.LIKE_COUNT, FieldValue.increment(1));

        batch.commit().addOnCompleteListener(listener);
    }

    /**
     * Atomic operation: remove a like and decrement the post like count.
     * Uses WriteBatch so both updates succeed or fail together, keeping data consistent.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void removeLikeAndDecrementCount(String userId, String postId, OnCompleteListener<Void> listener) {
        String likeId = IdGenerator.generateLikeId(userId, postId);

        WriteBatch batch = db.batch();
        batch.delete(db.collection(COLLECTION_LIKES).document(likeId));
        batch.update(db.collection(COLLECTION_POSTS).document(postId),
                FirestoreFields.LIKE_COUNT, FieldValue.increment(-1));

        batch.commit().addOnCompleteListener(listener);
    }

    /**
     * Checks if a like already exists.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @param listener Completion listener returning whether the like exists.
     */
    public void checkLikeExists(String userId, String postId, OnCompleteListener<Boolean> listener) {
        String likeId = IdGenerator.generateLikeId(userId, postId);
        db.collection(COLLECTION_LIKES)
                .document(likeId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = task.getResult().exists();
                        listener.onComplete(Tasks.forResult(exists));
                    } else {
                        listener.onComplete(Tasks.forResult(false));
                    }
                });
    }

    /**
     * Retrieves all like records for a post.
     *
     * @param postId Post ID.
     * @param listener Completion listener returning the post's likes or an exception.
     */
    public void getLikesByPost(String postId, OnCompleteListener<List<Like>> listener) {
        db.collection(COLLECTION_LIKES)
                .whereEqualTo(FirestoreFields.LIKE_POST_ID, postId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Like> likes = task.getResult().toObjects(Like.class);
                        listener.onComplete(Tasks.forResult(likes));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves every post ID liked by the specified user.
     *
     * @param userId User ID.
     * @param listener Completion listener returning the post ID list or an exception.
     */
    public void getLikesByUser(String userId, OnCompleteListener<List<String>> listener) {
        db.collection(COLLECTION_LIKES)
                .whereEqualTo(FirestoreFields.LIKE_USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> postIds = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Like like = doc.toObject(Like.class);
                            if (like != null) {
                                postIds.add(like.getPostId());
                            }
                        }
                        listener.onComplete(Tasks.forResult(postIds));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    // ==================== Favorite Operations ====================

    /**
     * Adds a favorite record.
     *
     * @param favorite Favorite entity.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void addFavorite(Favorite favorite, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_FAVORITES)
                .document(favorite.getFavoriteId())
                .set(favorite)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a favorite record by ID.
     *
     * @param favoriteId Favorite ID.
     * @param listener Completion listener returning the Favorite or null when missing.
     */
    public void getFavorite(String favoriteId, OnCompleteListener<Favorite> listener) {
        db.collection(COLLECTION_FAVORITES)
                .document(favoriteId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Favorite favorite = task.getResult().toObject(Favorite.class);
                        listener.onComplete(Tasks.forResult(favorite));
                    } else {
                        listener.onComplete(Tasks.forResult(null));
                    }
                });
    }

    /**
     * Retrieves every favorite record.
     *
     * @param listener Completion listener returning the favorite list or an exception.
     */
    public void getAllFavorites(OnCompleteListener<List<Favorite>> listener) {
        db.collection(COLLECTION_FAVORITES)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Favorite> favorites = task.getResult().toObjects(Favorite.class);
                        listener.onComplete(Tasks.forResult(favorites));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Deletes a favorite record.
     *
     * @param favoriteId ID of the favorite to remove.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void deleteFavorite(String favoriteId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_FAVORITES)
                .document(favoriteId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Atomic operation: add a favorite and increment the post favorite count.
     * Uses WriteBatch so both updates succeed or fail together, keeping data consistent.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void addFavoriteAndIncrementCount(String userId, String postId, OnCompleteListener<Void> listener) {
        String favoriteId = IdGenerator.generateFavoriteId(userId, postId);
        Favorite favorite = new Favorite(userId, postId, Timestamp.now());

        WriteBatch batch = db.batch();
        batch.set(db.collection(COLLECTION_FAVORITES).document(favoriteId), favorite);
        batch.update(db.collection(COLLECTION_POSTS).document(postId),
                FirestoreFields.FAVORITE_COUNT, FieldValue.increment(1));

        batch.commit().addOnCompleteListener(listener);
    }

    /**
     * Atomic operation: remove a favorite and decrement the post favorite count.
     * Uses WriteBatch so both updates succeed or fail together, keeping data consistent.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void removeFavoriteAndDecrementCount(String userId, String postId, OnCompleteListener<Void> listener) {
        String favoriteId = IdGenerator.generateFavoriteId(userId, postId);

        WriteBatch batch = db.batch();
        batch.delete(db.collection(COLLECTION_FAVORITES).document(favoriteId));
        batch.update(db.collection(COLLECTION_POSTS).document(postId),
                FirestoreFields.FAVORITE_COUNT, FieldValue.increment(-1));

        batch.commit().addOnCompleteListener(listener);
    }

    /**
     * Increments the favorite count for a post.
     *
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void incrementPostFavoriteCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.FAVORITE_COUNT, FieldValue.increment(1))
                .addOnCompleteListener(listener);
    }

    /**
     * Decrements the favorite count for a post.
     *
     * @param postId Post ID.
     * @param listener Completion listener invoked when the operation finishes.
     */
    public void decrementPostFavoriteCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.FAVORITE_COUNT, FieldValue.increment(-1))
                .addOnCompleteListener(listener);
    }

    /**
     * Checks if a favorite already exists.
     *
     * @param userId User ID.
     * @param postId Post ID.
     * @param listener Completion listener returning whether the favorite exists.
     */
    public void checkFavoriteExists(String userId, String postId, OnCompleteListener<Boolean> listener) {
        String favoriteId = IdGenerator.generateFavoriteId(userId, postId);
        db.collection(COLLECTION_FAVORITES)
                .document(favoriteId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean exists = task.getResult().exists();
                        listener.onComplete(Tasks.forResult(exists));
                    } else {
                        listener.onComplete(Tasks.forResult(false));
                    }
                });
    }

    /**
     * Retrieves every post ID favorited by the specified user.
     *
     * @param userId User ID.
     * @param listener Completion listener returning the post ID list or an exception.
     */
    public void getFavoritesByUser(String userId, OnCompleteListener<List<String>> listener) {
        db.collection(COLLECTION_FAVORITES)
                .whereEqualTo(FirestoreFields.FAVORITE_USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> postIds = new ArrayList<>();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Favorite favorite = doc.toObject(Favorite.class);
                            if (favorite != null) {
                                postIds.add(favorite.getPostId());
                            }
                        }
                        listener.onComplete(Tasks.forResult(postIds));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }

    /**
     * Retrieves every post favorited by the specified user (returns full Post objects).
     * <p>
     * Two-step flow:
     * 1. Fetch the list of post IDs the user has favorited.
     * 2. Query the complete post documents using those IDs.
     * </p>
     *
     * @param userId User ID.
     * @param listener Completion listener returning the post list or an exception.
     */
    public void getFavoritedPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        // Step 1: fetch the IDs of the posts the user favorited.
        getFavoritesByUser(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> postIds = task.getResult();
                if (postIds.isEmpty()) {
                    listener.onComplete(Tasks.forResult(new ArrayList<>()));
                    return;
                }

                // Step 2: fetch the full post objects for those IDs.
                db.collection(COLLECTION_POSTS)
                        .whereIn(FieldPath.documentId(), postIds)
                        .orderBy(FirestoreFields.CREATED_AT, Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(postsTask -> {
                            if (postsTask.isSuccessful()) {
                                List<Post> posts = postsTask.getResult().toObjects(Post.class);
                                listener.onComplete(Tasks.forResult(posts));
                            } else {
                                listener.onComplete(Tasks.forException(postsTask.getException()));
                            }
                        });
            } else {
                listener.onComplete(Tasks.forException(task.getException()));
            }
        });
    }

    /**
     * Retrieves the total number of likes across all posts by the specified user.
     * <p>
     * Fetches every post owned by the user and sums their like counts.
     * </p>
     *
     * @param userId User ID.
     * @param listener Completion listener returning the total like count or an exception.
     */
    public void getTotalLikesForUser(String userId, OnCompleteListener<Integer> listener) {
        db.collection(COLLECTION_POSTS)
                .whereEqualTo(FirestoreFields.USER_ID, userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int totalLikes = 0;
                        for (DocumentSnapshot doc : task.getResult()) {
                            Post post = doc.toObject(Post.class);
                            if (post != null) {
                                totalLikes += post.getLikeCount();
                            }
                        }
                        listener.onComplete(Tasks.forResult(totalLikes));
                    } else {
                        listener.onComplete(Tasks.forException(task.getException()));
                    }
                });
    }
}
