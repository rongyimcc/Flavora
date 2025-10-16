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
 * Fragment that shows the discover feed with search, refresh, and interaction capabilities.
 */
public class DiscoverFragment extends Fragment implements PostsAdapter.OnPostInteractionListener {

    /** ViewBinding instance. */
    private FragmentDiscoverBinding binding;

    /** Adapter for the posts list. */
    private PostsAdapter postsAdapter;

    /** Cached list of all posts for search filtering. */
    private List<Post> allPosts = new ArrayList<>();

    /** Inflates the fragment layout with ViewBinding. */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDiscoverBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /** Configures UI components once the view is created. */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupSwipeRefresh();
        setupSearchBar();
        loadPosts();
    }

    /** Sets up the RecyclerView and adapter. */
    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter();
        postsAdapter.setOnPostInteractionListener(this);
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerViewPosts.setAdapter(postsAdapter);
    }

    /** Enables pull-to-refresh to reload posts. */
    private void setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener(this::loadPosts);
    }

    /** Configures the search bar to filter posts on text changes. */
    private void setupSearchBar() {
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Filter as the user types.
                filterPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used.
            }
        });
    }

    /** Filters posts by title, description, or username. */
    private void filterPosts(String query) {
        if (query == null || query.trim().isEmpty()) {
            // Show all posts when there's no query.
            postsAdapter.setPosts(allPosts);
        } else {
            // Filter based on text matches.
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

    /** Loads posts along with the user's like/favorite state and reapplies the current filter. */
    private void loadPosts() {
        binding.swipeRefresh.setRefreshing(true);

        // Step 1: load all posts.
        PostFacade.getAllPosts(postsTask -> {
            // Lifecycle guard.
            if (!isAdded() || binding == null) return;

            if (!postsTask.isSuccessful() || postsTask.getResult() == null) {
                binding.swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Failed to load posts", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Post> posts = postsTask.getResult();

            // Step 2: load interaction state.
            PostInteractionFacade.getLikedPostIds(likedTask -> {
                // Lifecycle guard.
                if (!isAdded() || binding == null) return;

                PostInteractionFacade.getFavoritedPostIds(favoritedTask -> {
                    // Lifecycle guard.
                    if (!isAdded() || binding == null) return;

                    binding.swipeRefresh.setRefreshing(false);

                    List<String> likedPostIds = likedTask.isSuccessful() && likedTask.getResult() != null
                            ? likedTask.getResult() : List.of();
                    List<String> favoritedPostIds = favoritedTask.isSuccessful() && favoritedTask.getResult() != null
                            ? favoritedTask.getResult() : List.of();

                    // Convert to sets for efficient lookups.
                    Set<String> likedSet = new HashSet<>(likedPostIds);
                    Set<String> favoritedSet = new HashSet<>(favoritedPostIds);

                    // Apply interaction state to each post.
                    for (Post post : posts) {
                        post.setLikedByCurrentUser(likedSet.contains(post.getPostId()));
                        post.setFavoritedByCurrentUser(favoritedSet.contains(post.getPostId()));
                    }

                    // Cache posts for search filtering.
                    allPosts = new ArrayList<>(posts);

                    // Reapply any active search query.
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

    /** Opens the post detail screen when a post row is tapped. */
    @Override
    public void onPostClicked(Post post) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getPostId());
        startActivity(intent);
    }

    /** Handles like toggles and updates the list item. */
    @Override
    public void onLikeClicked(Post post, int position) {
        // Toggle like state.
        boolean currentStatus = post.isLikedByCurrentUser();
        PostInteractionFacade.toggleLike(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setLikedByCurrentUser(newStatus);

                // Update counts.
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

    /** Handles favorite toggles and updates the list item. */
    @Override
    public void onFavoriteClicked(Post post, int position) {
        // Toggle favorite state.
        boolean currentStatus = post.isFavoritedByCurrentUser();
        PostInteractionFacade.toggleFavorite(post.getPostId(), currentStatus, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                boolean newStatus = task.getResult();
                post.setFavoritedByCurrentUser(newStatus);

                // Update counts.
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

    /** Clears the binding reference when the view is destroyed to avoid leaks. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
