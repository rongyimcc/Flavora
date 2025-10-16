package comp.assignment.flavora.ui.post;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import comp.assignment.flavora.R;
import comp.assignment.flavora.dao.UserDAO;
import comp.assignment.flavora.databinding.BottomSheetCreatePostBinding;
import comp.assignment.flavora.facade.PostFacade;
import comp.assignment.flavora.model.User;
import comp.assignment.flavora.repository.AuthRepository;
import comp.assignment.flavora.repository.StorageRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Create Post Bottom Sheet
 *
 * <p>Full-screen bottom sheet dialog for creating a new post. Features:</p>
 * <ul>
 *   <li>Input title and description</li>
 *   <li>Select and preview up to 5 images</li>
 *   <li>Rating (1–5 stars)</li>
 *   <li>Upload images and create the post</li>
 * </ul>
 *
 * <p>Uses ViewBinding for view access, ActivityResultLauncher for image picking,
 * and integrates StorageRepository and PostFacade to upload images and create a post.</p>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class CreatePostBottomSheet extends BottomSheetDialogFragment {

    /** Max allowed image count. */
    private static final int MAX_IMAGES = 5;

    /** ViewBinding instance */
    private BottomSheetCreatePostBinding binding;

    /** Selected image URIs. */
    private final List<Uri> selectedImageUris = new ArrayList<>();

    /** Adapter for selected images list. */
    private SelectedImagesAdapter imagesAdapter;

    /** Image picker launcher. */
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    /**
     * Fragment onCreate.
     *
     * <p>Initialize the image picker and handle single/multiple selections.
     * Limit selection to at most 5 images.</p>
     *
     * @param savedInstanceState saved state
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Init image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        // Multiple selection
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count && selectedImageUris.size() < MAX_IMAGES; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                selectedImageUris.add(imageUri);
                            }
                        }
                        // Single selection
                        else if (data.getData() != null) {
                            if (selectedImageUris.size() < MAX_IMAGES) {
                                selectedImageUris.add(data.getData());
                            }
                        }

                        updateImagesAdapter();
                        checkImageLimit();
                    }
                });
    }

    /**
     * Create view with ViewBinding.
     *
     * @param inflater  layout inflater
     * @param container parent
     * @param savedInstanceState saved state
     * @return root view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
                             }

    /**
     * View created callback.
     *
     * <p>Initialize RecyclerView, set click listeners, and load user avatar.</p>
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupClickListeners();
        loadUserAvatar();
    }

    /**
     * Dialog start callback.
     *
     * <p>Configure the bottom sheet as full-screen expanded, disable swipe-to-dismiss,
     * and adjust for soft keyboard.</p>
     */
    @Override
    public void onStart() {
        super.onStart();

        // Full-screen bottom sheet and keyboard adjustment
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();

            // AdjustResize for soft keyboard
            if (dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }

            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setDraggable(false);

                // Full height
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * Init images RecyclerView.
     *
     * <p>Use horizontal layout and adapter. Support removing images.</p>
     */
    private void setupRecyclerView() {
        imagesAdapter = new SelectedImagesAdapter(selectedImageUris, position -> {
            // Remove the image at the given index
            selectedImageUris.remove(position);
            updateImagesAdapter();
            checkImageLimit();
        });

        binding.recyclerViewImages.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewImages.setAdapter(imagesAdapter);
    }

    /**
     * Wire up button click listeners.
     */
    private void setupClickListeners() {
        binding.buttonClose.setOnClickListener(v -> dismiss());

        binding.buttonAddImage.setOnClickListener(v -> openImagePicker());

        binding.buttonRating.setOnClickListener(v -> showRatingDialog());

        binding.buttonSubmit.setOnClickListener(v -> attemptCreatePost());
    }

    /**
     * Load current user's avatar.
     *
     * <p>Fetch user info from UserDAO and load a circular avatar via Glide.
     * Fallback to default icon if missing or failed.</p>
     */
    private void loadUserAvatar() {
        AuthRepository authRepository = AuthRepository.getInstance();
        String userId = authRepository.getCurrentUserId();

        if (userId != null) {
            UserDAO.getInstance().get(userId, userTask -> {
                if (userTask.isSuccessful() && userTask.getResult() != null) {
                    User user = userTask.getResult();
                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(this)
                                .load(user.getAvatarUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_person_24)
                                .error(R.drawable.ic_person_24)
                                .into(binding.imageAvatar);
                    }
                }
            });
        }
    }

    /**
     * Show rating dialog.
     *
     * <p>Dialog with a RatingBar allowing 1–5 stars. On confirm, update the display.</p>
     */
    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rate your experience");

        // Inflate dialog layout with RatingBar
        View dialogView = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_rating, null);
        android.widget.RatingBar dialogRatingBar = dialogView.findViewById(R.id.dialog_rating_bar);

        // Set current rating
        dialogRatingBar.setRating(binding.ratingBar.getRating());

        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> {
            float rating = dialogRatingBar.getRating();
            binding.ratingBar.setRating(rating);
            updateRatingDisplay(rating);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    /**
     * Update rating display.
     *
     * <p>Update the text based on the rating value. When the rating is > 0,
     * show a star and the numeric value; otherwise hide the display.</p>
     *
     * @param rating Rating value (0–5)
     */
    private void updateRatingDisplay(float rating) {
        if (rating > 0) {
            binding.textRatingDisplay.setText("★ " + rating);
            binding.textRatingDisplay.setVisibility(View.VISIBLE);
        } else {
            binding.textRatingDisplay.setVisibility(View.GONE);
        }
    }

    /**
     * Open the image picker.
     *
     * <p>After checking the image count limit, launch the system picker.
     * Multiple selection is supported, but the total cannot exceed MAX_IMAGES.</p>
     */
    private void openImagePicker() {
        if (selectedImageUris.size() >= MAX_IMAGES) {
            Toast.makeText(getContext(), "Maximum " + MAX_IMAGES + " images allowed",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imagePickerLauncher.launch(Intent.createChooser(intent, "Select Images"));
    }

    /**
     * Update the image list adapter.
     *
     * <p>Refresh the adapter, and toggle the RecyclerView visibility
     * based on whether images are selected.</p>
     */
    private void updateImagesAdapter() {
        if (imagesAdapter != null) {
            imagesAdapter.notifyDataSetChanged();
        }

        // Toggle RecyclerView by selection state
        if (selectedImageUris.isEmpty()) {
            binding.recyclerViewImages.setVisibility(View.GONE);
        } else {
            binding.recyclerViewImages.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Check the image count limit.
     *
     * <p>Enable or disable the “Add Image” button based on the current
     * number of selected images. Disable the button when the limit is reached.</p>
     */
    private void checkImageLimit() {
        binding.buttonAddImage.setEnabled(selectedImageUris.size() < MAX_IMAGES);
    }

    /**
     * Attempt to create a post.
     *
     * <p>Runs the full post-creation flow:
     * 1) Validate input (title, description, images)
     * 2) Fetch current user info
     * 3) Upload images to Firebase Storage
     * 4) Create the post document in Firestore
     * 5) Show the result and close the dialog
     * </p>
     */
    private void attemptCreatePost() {

        String title = binding.editTextTitle.getText().toString().trim();
        String description = binding.editTextDescription.getText().toString().trim();
        float rating = binding.ratingBar.getRating();

        boolean hasError = false;

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(getContext(), "Title is required", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (TextUtils.isEmpty(description)) {
            Toast.makeText(getContext(), "Description is required", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (selectedImageUris.isEmpty()) {
            Toast.makeText(getContext(), "Please add at least one image", Toast.LENGTH_SHORT).show();
            hasError = true;
        }

        if (hasError) {
            return;
        }

        setLoading(true);

        // Current User
        AuthRepository authRepository = AuthRepository.getInstance();
        String userId = authRepository.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        // Get User Information
        UserDAO.getInstance().get(userId, userTask -> {
            if (!userTask.isSuccessful() || userTask.getResult() == null) {
                setLoading(false);
                Toast.makeText(getContext(), "Failed to get user info", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userTask.getResult();
            String username = user.getUsername();
            String avatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : "";

            // Upload images then create post
            StorageRepository.getInstance().uploadImages(selectedImageUris, task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> imageUrls = task.getResult();

                    // Create post with uploaded image URLs
                    PostFacade.createPost(
                            userId,
                            username,
                            avatarUrl,
                            title,
                            description,
                            imageUrls,
                            rating,
                            postTask -> {
                                setLoading(false);

                                if (postTask.isSuccessful()) {
                                    Toast.makeText(getContext(), "Post created successfully!",
                                            Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else {
                                    String errorMessage = "Failed to create post";
                                    if (postTask.getException() != null) {
                                        errorMessage = postTask.getException().getMessage();
                                    }
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                                }
                            });
                } else {
                    setLoading(false);
                    String errorMessage = "Failed to upload images";
                    if (task.getException() != null) {
                        errorMessage = task.getException().getMessage();
                    }
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    /**
     * Set loading state.
     *
     * <p>While loading, disable all interactive controls and show the progress bar;
     * when finished, restore controls and hide the progress bar.</p>
     *
     * @param loading true to indicate loading; false when loading is finished
     */
    private void setLoading(boolean loading) {
        binding.buttonSubmit.setEnabled(!loading);
        binding.buttonAddImage.setEnabled(!loading && selectedImageUris.size() < MAX_IMAGES);
        binding.buttonRating.setEnabled(!loading);
        binding.editTextTitle.setEnabled(!loading);
        binding.editTextDescription.setEnabled(!loading);
        binding.ratingBar.setEnabled(!loading);
        binding.buttonClose.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    /**
     * Callback when the view is destroyed.
     *
     * <p>Clear the ViewBinding reference to prevent memory leaks.</p>
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
