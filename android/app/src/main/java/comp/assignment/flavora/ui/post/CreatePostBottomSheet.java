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
 * Full-screen bottom sheet for composing a new post with text, images, and rating.
 * Integrates StorageRepository for uploads and PostFacade for creation.
 */
public class CreatePostBottomSheet extends BottomSheetDialogFragment {

    /** Maximum number of images allowed. */
    private static final int MAX_IMAGES = 5;

    /** ViewBinding instance. */
    private BottomSheetCreatePostBinding binding;

    /** Selected image URIs. */
    private final List<Uri> selectedImageUris = new ArrayList<>();

    /** Adapter displaying selected images. */
    private SelectedImagesAdapter imagesAdapter;

    /** Launcher for the image picker. */
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    /**
     * Initializes the image picker launcher to handle single or multiple selection.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the image picker.
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();

                        // Handle multi-image selection.
                        if (data.getClipData() != null) {
                            int count = data.getClipData().getItemCount();
                            for (int i = 0; i < count && selectedImageUris.size() < MAX_IMAGES; i++) {
                                Uri imageUri = data.getClipData().getItemAt(i).getUri();
                                selectedImageUris.add(imageUri);
                            }
                        }
                        // Handle single-image selection.
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
     * Inflates the bottom sheet layout with ViewBinding.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * Initializes UI components after the view is created.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupRecyclerView();
        setupClickListeners();
        loadUserAvatar();
    }

    /**
     * Configures the bottom sheet to expand full-screen and adjust for the keyboard.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Configure the sheet for full-screen keyboard-aware behavior.
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();

            // Ensure the keyboard resizes the window.
            if (dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }

            FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setSkipCollapsed(true);
                behavior.setDraggable(false); // Disable swipe-to-dismiss.

                // Force full height.
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(layoutParams);
            }
        }
    }

    /**
     * Initializes the horizontal RecyclerView used to preview selected images.
     */
    private void setupRecyclerView() {
        imagesAdapter = new SelectedImagesAdapter(selectedImageUris, position -> {
            // Remove the tapped image.
            selectedImageUris.remove(position);
            updateImagesAdapter();
            checkImageLimit();
        });

        binding.recyclerViewImages.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerViewImages.setAdapter(imagesAdapter);
    }

    /**
     * Wires up handlers for close, add-image, rating, and submit actions.
     */
    private void setupClickListeners() {
        binding.buttonClose.setOnClickListener(v -> dismiss());

        binding.buttonAddImage.setOnClickListener(v -> openImagePicker());

        binding.buttonRating.setOnClickListener(v -> showRatingDialog());

        binding.buttonSubmit.setOnClickListener(v -> attemptCreatePost());
    }

    /**
     * Loads the current user's avatar via UserDAO and Glide, falling back to a default icon.
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
     * Shows a dialog allowing the user to select a rating from 1-5 stars.
     */
    private void showRatingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Rate your experience");

        // Inflate the dialog layout containing the RatingBar.
        View dialogView = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_rating, null);
        android.widget.RatingBar dialogRatingBar = dialogView.findViewById(R.id.dialog_rating_bar);

        // Pre-populate with the existing rating.
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
     * Updates the rating label based on the current value.
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
     * Launches the system picker for selecting images (respecting MAX_IMAGES).
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
     * Refreshes the image adapter and visibility states.
     */
    private void updateImagesAdapter() {
        if (imagesAdapter != null) {
            imagesAdapter.notifyDataSetChanged();
        }

        // Toggle RecyclerView visibility based on selection state.
        if (selectedImageUris.isEmpty()) {
            binding.recyclerViewImages.setVisibility(View.GONE);
        } else {
            binding.recyclerViewImages.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Enables or disables the add-image button based on current count.
     */
    private void checkImageLimit() {
        binding.buttonAddImage.setEnabled(selectedImageUris.size() < MAX_IMAGES);
    }

    /**
     * Validates input, uploads images, and creates the post.
     */
    private void attemptCreatePost() {
        // Gather input.
        String title = binding.editTextTitle.getText().toString().trim();
        String description = binding.editTextDescription.getText().toString().trim();
        float rating = binding.ratingBar.getRating();

        // Validate input.
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

        // Show loading UI.
        setLoading(true);

        // Fetch current user info.
        AuthRepository authRepository = AuthRepository.getInstance();
        String userId = authRepository.getCurrentUserId();

        if (userId == null) {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            setLoading(false);
            return;
        }

        // First, load the user profile.
        UserDAO.getInstance().get(userId, userTask -> {
            if (!userTask.isSuccessful() || userTask.getResult() == null) {
                setLoading(false);
                Toast.makeText(getContext(), "Failed to get user info", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = userTask.getResult();
            String username = user.getUsername();
            String avatarUrl = user.getAvatarUrl() != null ? user.getAvatarUrl() : "";

            // Upload images before creating the post.
            StorageRepository.getInstance().uploadImages(selectedImageUris, task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    List<String> imageUrls = task.getResult();

                    // Create the post with the uploaded URLs.
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
     * Toggles loading UI by disabling inputs and showing the progress bar.
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
     * Clears the binding reference when the view is destroyed to avoid leaks.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
