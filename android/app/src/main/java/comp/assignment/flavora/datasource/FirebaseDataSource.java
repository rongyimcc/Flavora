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
 * Firebase Data Source
 * <p>
 * Encapsulates all Firebase operations and provides an abstraction layer
 * between the DAO layer and Firebase.
 * This makes switching data sources easier and improves maintainability and testability.
 * </p>
 *
 * <p>Design Features:</p>
 * <ul>
 *   <li>Singleton: globally unique instance, thread-safe</li>
 *   <li>Unified interface: all Firebase operations go through this class</li>
 *   <li>Asynchronous operations: uses OnCompleteListener for async callbacks</li>
 *   <li>Atomic operations: uses WriteBatch to ensure data consistency</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class FirebaseDataSource {
    /** Log tag */
    private static final String TAG = "FirebaseDataSource";

    /** Singleton instance */
    private static FirebaseDataSource instance;

    /** Firestore database instance */
    private final FirebaseFirestore db;

    /** Firebase Auth instance */
    private final FirebaseAuth auth;

    /** Firebase Storage instance */
    private final FirebaseStorage storage;

    /** Collection: users */
    private static final String COLLECTION_USERS = "users";

    /** Collection: posts */
    private static final String COLLECTION_POSTS = "posts";

    /** Collection: likes */
    private static final String COLLECTION_LIKES = "likes";

    /** Collection: favorites */
    private static final String COLLECTION_FAVORITES = "favorites";

    /**
     * Private constructor.
     * Initializes Firebase-related instances.
     */
    private FirebaseDataSource() {
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.storage = FirebaseStorage.getInstance();
    }

    /**
     * Gets the singleton instance of FirebaseDataSource.
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     *
     * @return the singleton FirebaseDataSource instance
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
     * Adds a user to Firestore.
     *
     * @param user     the User object to add
     * @param listener completion listener invoked on operation completion
     */
    public void addUser(User user, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(listener);
    }

    /**
     * Retrieves a user by user ID.
     *
     * @param userId   the user ID
     * @param listener completion listener returning the User or an exception
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
     * Retrieves all users.
     *
     * @param listener completion listener returning the list of users or an exception
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
     * @param user     the User containing new data
     * @param listener completion listener invoked on operation completion
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
     * @param userId   the ID of the user to delete
     * @param listener completion listener invoked on operation completion
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
     * @param username the username
     * @param listener completion listener returning a User or null if not found
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
     * Increments the user's post count.
     *
     * @param userId   the user ID
     * @param listener completion listener invoked on operation completion
     */
    public void incrementUserPostsCount(String userId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .update(FirestoreFields.POSTS_COUNT, FieldValue.increment(1))
                .addOnCompleteListener(listener);
    }

    /**
     * Decrements the user's post count.
     *
     * @param userId   the user ID
     * @param listener completion listener invoked on operation completion
     */
    public void decrementUserPostsCount(String userId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_USERS)
                .document(userId)
                .update(FirestoreFields.POSTS_COUNT, FieldValue.increment(-1))
                .addOnCompleteListener(listener);
    }

    // ==================== Post Operations ====================

    /**
     * Adds a post to Firestore.
     *
     * @param post     the Post to add
     * @param listener completion listener invoked on operation completion
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
     * @param postId   the post ID
     * @param listener completion listener returning the Post or an exception
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
     * Retrieves all posts.
     *
     * @param listener completion listener returning the list of posts or an exception
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
     * @param listener completion listener returning the time-sorted list or an exception
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
     * Retrieves all posts created by a specific user.
     *
     * @param userId   the user ID
     * @param listener completion listener returning the user's posts or an exception
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
     * Updates a post.
     *
     * @param post     the Post containing new data
     * @param listener completion listener invoked on operation completion
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
     * @param postId   the post ID to delete
     * @param listener completion listener invoked on operation completion
     */
    public void deletePost(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Increments a post's like count.
     *
     * @param postId   the post ID
     * @param listener completion listener invoked on operation completion
     */
    public void incrementPostLikeCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.LIKE_COUNT, FieldValue.increment(1))
                .addOnCompleteListener(listener);
    }

    /**
     * Decrements a post's like count.
     *
     * @param postId   the post ID
     * @param listener completion listener invoked on operation completion
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
     * @param like     the Like object
     * @param listener completion listener invoked on operation completion
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
     * @param likeId   the like ID
     * @param listener completion listener returning a Like or null if not found
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
     * Retrieves all like records.
     *
     * @param listener completion listener returning the list of likes or an exception
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
     * @param likeId   the like ID to delete
     * @param listener completion listener invoked on operation completion
     */
    public void deleteLike(String likeId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_LIKES)
                .document(likeId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Atomic operation: add a like and increment the post's like count.
     * Uses WriteBatch to ensure both operations succeed or fail together.
     *
     * @param userId   user ID
     * @param postId   post ID
     * @param listener completion listener invoked on operation completion
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
     * Atomic operation: remove a like and decrement the post's like count.
     * Uses WriteBatch for consistency.
     *
     * @param userId   user ID
     * @param postId   post ID
     * @param listener completion listener invoked on operation completion
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
     * Checks whether a like exists.
     *
     * @param userId   user ID
     * @param postId   post ID
     * @param listener completion listener returning a boolean
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
     * Retrieves all likes for a given post.
     *
     * @param postId   post ID
     * @param listener completion listener returning a list of Like objects or an exception
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
     * Retrieves all post IDs liked by a specific user.
     *
     * @param userId   user ID
     * @param listener completion listener returning a list of post IDs or an exception
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
     * @param favorite the Favorite object
     * @param listener completion listener invoked on operation completion
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
     * @param favoriteId favorite ID
     * @param listener   completion listener returning a Favorite or null if not found
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
     * Retrieves all favorite records.
     *
     * @param listener completion listener returning the list of favorites or an exception
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
     * @param favoriteId favorite ID to delete
     * @param listener   completion listener invoked on operation completion
     */
    public void deleteFavorite(String favoriteId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_FAVORITES)
                .document(favoriteId)
                .delete()
                .addOnCompleteListener(listener);
    }

    /**
     * Atomic operation: add a favorite and increment the post's favorite count.
     * Uses WriteBatch to ensure both operations succeed or fail together.
     *
     * @param userId   user ID
     * @param postId   post ID
     * @param listener completion listener invoked on operation completion
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
     * Atomic operation: remove a favorite and decrement the post's favorite count.
     * Uses WriteBatch to ensure both operations succeed or fail together.
     *
     * @param userId   user ID
     * @param postId   post ID
     * @param listener completion listener invoked on operation completion
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
     * Increments a post's favorite count.
     *
     * @param postId   the post ID
     * @param listener completion listener invoked on operation completion
     */
    public void incrementPostFavoriteCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.FAVORITE_COUNT, FieldValue.increment(1))
                .addOnCompleteListener(listener);
    }

    /**
     * Decrements a post's favorite count.
     *
     * @param postId   the post ID
     * @param listener completion listener invoked on operation completion
     */
    public void decrementPostFavoriteCount(String postId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_POSTS)
                .document(postId)
                .update(FirestoreFields.FAVORITE_COUNT, FieldValue.increment(-1))
                .addOnCompleteListener(listener);
    }

    /**
     * Checks whether a favorite exists.
     *
     * @param userId   user ID
     * @param postId   post ID
     * @param listener completion listener returning a boolean
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
     * Retrieves all post IDs favorited by a specific user.
     *
     * @param userId   user ID
     * @param listener completion listener returning a list of post IDs or an exception
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
     * Retrieves all favorited posts by a user (returns full Post objects).
     * <p>
     * Two steps:
     * 1. Fetch the list of favorited post IDs for the user.
     * 2. Query posts by those IDs to obtain full Post objects.
     * </p>
     *
     * @param userId   user ID
     * @param listener completion listener returning a list of posts or an exception
     */
    public void getFavoritedPostsByUser(String userId, OnCompleteListener<List<Post>> listener) {
        // Step 1: get favorited post IDs
        getFavoritesByUser(userId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> postIds = task.getResult();
                if (postIds.isEmpty()) {
                    listener.onComplete(Tasks.forResult(new ArrayList<>()));
                    return;
                }

                // Step 2: fetch full posts by IDs
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
     * Computes the total number of likes received across all posts by the given user.
     * <p>
     * Implementation: query all posts by the user and sum each post's likeCount.
     * </p>
     *
     * @param userId   user ID
     * @param listener completion listener returning the total like count or an exception
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
