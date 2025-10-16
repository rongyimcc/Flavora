package comp.assignment.flavora.ui.discover;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import comp.assignment.flavora.databinding.FragmentDiscoverBinding;
import comp.assignment.flavora.facade.PostFacade;
import comp.assignment.flavora.facade.PostInteractionFacade;
import comp.assignment.flavora.model.Post;
import comp.assignment.flavora.ui.post.PostDetailActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Discover Fragment
 * <p>
 * This fragment displays all posts and provides search and interaction features.
 * Users can browse all posts, search, like, favorite, and view post details.
 * </p>
 *
 * <p>Main Features:</p>
 * <ul>
 *   <li>Display a list of all user-posted posts</li>
 *   <li>Real-time search (by title, description, or username)</li>
 *   <li>Pull-to-refresh functionality</li>
 *   <li>Like and favorite interactions</li>
 *   <li>Navigate to post detail page</li>
 * </ul>
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class DiscoverFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    private FragmentDiscoverBinding binding;

    private PostsAdapter postsAdapter;

    private List<Post> allPosts = new ArrayList<>();

    /**
     * Create the view for the fragment
     *
     * @param inflater LayoutInflater to inflate the layout
     * @param container Parent view container
     * @param savedInstanceState Saved instance state
     * @return The root view created
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = FragmentDiscoverBinding.inflate(inflater, container, false);
            return binding.getRoot();
                             }

    /**
     * Callback after the view is created.
     * Initializes RecyclerView, pull-to-refresh, search bar, and loads post data.
     *
     * @param view The created view
     * @param savedInstanceState Saved instance state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSwipeRefresh();
        setupSearchBar();
        loadPosts();
    }

    /**
     * Set up the RecyclerView
     * Initialize adapter and layout manager
     */
    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPosts.setAdapter(postsAdapter);
    }

    /**
     * Set up pull-to-refresh feature
     * Reload post list when user pulls down
     */
    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadPosts);
    }

    /**
     * Set up the search bar
     * Listen for text changes and filter posts in real-time
     */
    private void setupSearchBar() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 文本改变前的回调（未使用）
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 文本改变时立即过滤帖子
                filterPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 文本改变后的回调（未使用）
            }
        });
    }

    /**
     * Filter posts based on the search query.
     * Match against post title, description, and username.
     *
     * @param query Search keyword
     */
    private void filterPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            // 如果搜索框为空，显示所有帖子
            postsAdapter.setPosts(allPosts);
        } else {
            // 根据标题、描述或用户名过滤帖子
            String lowerQuery = query.toLowerCase().trim();
            List<Post> filteredPosts = allPosts.stream()
                    .filter(post ->
                            (post.getTitle() != null && post.getTitle().toLowerCase().contains(lowerQuery)) ||
                                    (post.getDescription() != null && post.getDescription().toLowerCase().contains(lowerQuery)) ||
                                    (post.getUsername() != null && post.getUsername().toLowerCase().contains(lowerQuery))
                    )
                    .collect(Collectors.toList());
            postsAdapter.setPosts(filteredPosts);
        }
    }

    /**
     * Load all post data
     * <p>
     * Steps:
     * 1. Load all posts from the database
     * 2. Load current user's liked post IDs
     * 3. Load current user's favorited post IDs
     * 4. Update each post's like and favorite status
     * 5. Apply current search filter (if any)
     * </p>
     */
    private void loadPosts() {
        binding.swipeRefresh.setRefreshing(true);

        // 第一步：加载所有帖子
        PostFacade.getAllPosts(postsTask -> {
            // 生命周期检查：确保Fragment仍然附加且binding有效
            if (!isAdded() || binding == null) return;

            if (!postsTask.isSuccessful() || postsTask.getResult() == null) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Post> posts = postsTask.getResult();

            // 第二步：加载用户交互状态（点赞和收藏）
            PostInteractionFacade.getLikedPostIds(likedTask -> {
                // 生命周期检查：确保Fragment仍然附加且binding有效
                if (!isAdded() || binding == null) return;

                PostInteractionFacade.getFavoritedPostIds(favoritedTask -> {
                    // 生命周期检查：确保Fragment仍然附加且binding有效
                    if (!isAdded() || binding == null) return;

                    binding.swipeRefresh.setRefreshing(false);

                    List<String> likedPostIds = likedTask.isSuccessful() && likedTask.getResult() != null
                            ? likedTask.getResult() : List.of();
                    List<String> favoritedPostIds = favoritedTask.isSuccessful() && favoritedTask.getResult() != null
                            ? favoritedTask.getResult() : List.of();

                    // 转换为Set以提高查找效率
                    Set<String> likedSet = new HashSet<>(likedPostIds);
                    Set<String> favoritedSet = new HashSet<>(favoritedPostIds);

                    // 更新每个帖子的用户交互状态
                    for (Post post : posts) {
                        post.setLikedByCurrentUser(likedSet.contains(post.getPostId()));
                        post.setFavoritedByCurrentUser(favoritedSet.contains(post.getPostId()));
                    }

                    // 保存所有帖子用于搜索过滤
                    allPosts = new ArrayList<>(posts);

                    // 应用当前的搜索过滤（如果有）
                    String currentQuery = binding.searchEditText.getText().toString();
                    if (currentQuery.trim().isEmpty()) {
                        postsAdapter.setPosts(posts);
                    } else {
                        filterPosts(currentQuery);
                    }
                });
            });
        });
    }

    /**
     * Handle post click event
     * Navigate to the post detail page
     *
     * @param post The clicked post
     */
    @Override
    public void onPostClicked(Post post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getPostId());
        startActivity(intent);
    }

    /**
     * Handle like button click event
     * Toggle like status and update like count
     *
     * @param post The post to like/unlike
     * @param position The post's position in the list
     */
    @Override
    public void onLikeClicked(Post post, int position) {
        // 切换点赞状态
        boolean currentStatus = post.isLikedByCurrentUser();
        PostInteractionFacade.toggleLike(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setLikedByCurrentUser(newStatus);

                // 更新点赞数
                if (newStatus) {
                    post.setLikeCount(post.getLikeCount() + 1);
                } else {
                    post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
                }

                postsAdapter.updatePost(position, post);
            } else {
                String errorMessage = "Failed to update like";
                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handle favorite button click event
     * Toggle favorite status and update favorite count
     *
     * @param post The post to favorite/unfavorite
     * @param position The post's position in the list
     */
    @Override
    public void onFavoriteClicked(Post post, int position) {
        // 切换收藏状态
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setFavoritedByCurrentUser(newStatus);

                // 更新收藏数
                if (newStatus) {
                    post.setFavoriteCount(post.getFavoriteCount() + 1);
                } else {
                    post.setFavoriteCount(Math.max(0, post.getFavoriteCount() - 1));
                }

                postsAdapter.updatePost(position, post);
            } else {
                String errorMessage = "Failed to update favorite";
                if (task.getException() != null) {
                    errorMessage = task.getException().getMessage();
                }
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Callback when fragment view is destroyed
     * Clear the ViewBinding reference to prevent memory leaks
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}