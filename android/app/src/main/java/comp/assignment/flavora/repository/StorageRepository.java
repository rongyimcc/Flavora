package comp.assignment.flavora.repository;

import android.net.Uri;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Storage repository.
 * <p>
 * Handles every Firebase Storage operation: uploading, downloading, and deleting images.
 * Implemented as a singleton so there's a single storage entry point.
 * </p>
 *
 * <p>Core capabilities:</p>
 * <ul>
 *   <li>Batch image upload: upload multiple images at once and return their download URLs.</li>
 *   <li>Single image upload: upload a single image and return its download URL.</li>
 *   <li>Image deletion: remove files from Firebase Storage using their URL.</li>
 * </ul>
 *
 * <p>Typical scenarios:</p>
 * <ul>
 *   <li>Uploading user avatars.</li>
 *   <li>Uploading post images (supports multiple images).</li>
 *   <li>Managing and cleaning up image assets.</li>
 * </ul>
 *
 * @author Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class StorageRepository {
    /** Singleton instance. */
    private static StorageRepository instance;
    /** Firebase Storage instance. */
    private final FirebaseStorage storage;
    /** Root storage reference. */
    private final StorageReference storageRef;

    /**
     * Private constructor.
     * <p>
     * Initializes Firebase Storage and the root reference while keeping the singleton invariant.
     * </p>
     */
    private StorageRepository() {
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
    }

    /**
     * Returns the singleton StorageRepository instance.
     * <p>
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     * </p>
     *
     * @return The unique StorageRepository instance.
     */
    public static StorageRepository getInstance() {
        if (instance == null) {
            synchronized (StorageRepository.class) {
                if (instance == null) {
                    instance = new StorageRepository();
                }
            }
        }
        return instance;
    }

    /**
     * Uploads multiple images to Firebase Storage.
     * <p>
     * Supports uploading several images concurrently, each with a UUID filename.
     * Returns the list of download URLs once every upload finishes.
     * </p>
     *
     * <p>Upload steps:</p>
     * <ol>
     *   <li>Validate input; return an empty list when no images are provided.</li>
     *   <li>Generate a unique filename (UUID) for each image.</li>
     *   <li>Create an upload task per image and collect them.</li>
     *   <li>Wait for all upload tasks to finish.</li>
     *   <li>Return the download URLs for every image.</li>
     * </ol>
     *
     * <p>Naming convention:</p>
     * <pre>
     * images/[UUID].jpg
     * Example: images/550e8400-e29b-41d4-a716-446655440000.jpg
     * </pre>
     *
     * @param imageUris Image URIs selected from local storage or gallery.
     * @param listener Completion listener.
     *                 Returns the list of download URLs on success.
     *                 Returns the exception on failure.
     */
    public void uploadImages(List<Uri> imageUris, OnCompleteListener<List<String>> listener) {
        // Validate input: ensure the list is not empty.
        if (imageUris == null || imageUris.isEmpty()) {
            listener.onComplete(Tasks.forResult(new ArrayList<>()));
            return;
        }

        // Collect upload tasks so images can upload in parallel.
        List<Task<Uri>> uploadTasks = new ArrayList<>();

        // Create an individual upload task for each image.
        for (Uri imageUri : imageUris) {
            // Generate a unique filename to avoid collisions.
            String filename = "images/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(filename);

            // Upload the file then fetch its download URL.
            Task<Uri> uploadTask = imageRef.putFile(imageUri)
                    .continueWithTask(task -> {
                        // Ensure the upload succeeded.
                        if (!task.isSuccessful() && task.getException() != null) {
                            throw task.getException();
                        }
                        // Return the download URL after a successful upload.
                        return imageRef.getDownloadUrl();
                    });

            uploadTasks.add(uploadTask);
        }

        // Wait for every upload task to complete.
        Tasks.whenAllSuccess(uploadTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Collect each download URL.
                List<String> downloadUrls = new ArrayList<>();
                for (Object result : task.getResult()) {
                    // Type-check: ensure the result is a Uri.
                    if (result instanceof Uri) {
                        downloadUrls.add(result.toString());
                    }
                }
                // Return the successful result.
                listener.onComplete(Tasks.forResult(downloadUrls));
            } else {
                // Return the failure reason.
                listener.onComplete(Tasks.forException(
                        task.getException() != null ? task.getException() :
                                new Exception("Failed to upload images")));
            }
        });
    }

    /**
     * Uploads a single image to Firebase Storage.
     * <p>
     * Ideal for scenarios like avatar uploads. Returns the download URL when complete.
     * </p>
     *
     * <p>Upload steps:</p>
     * <ol>
     *   <li>Validate that the image URI is not null.</li>
     *   <li>Generate a unique filename.</li>
     *   <li>Upload the file.</li>
     *   <li>Return the download URL.</li>
     * </ol>
     *
     * @param imageUri Image URI to upload.
     * @param listener Completion listener.
     *                 Returns the download URL string on success.
     *                 Returns the exception on failure (network issues, permission errors, etc.).
     */
    public void uploadImage(Uri imageUri, OnCompleteListener<String> listener) {
        // Validate that the URI is present.
        if (imageUri == null) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Image URI is null")));
            return;
        }

        // Generate a unique filename.
        String filename = "images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(filename);

        // Perform the upload.
        imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    // Ensure the upload succeeded.
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    // Return the download URL.
                    return imageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Return the download URL as a string.
                        listener.onComplete(Tasks.forResult(task.getResult().toString()));
                    } else {
                        // Return the failure exception.
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ? task.getException() :
                                        new Exception("Failed to upload image")));
                    }
                });
    }

    /**
     * Deletes an image from Firebase Storage.
     * <p>
     * Removes the file referenced by the download URL; useful when posts are deleted or avatars replaced.
     * </p>
     *
     * <p>Deletion steps:</p>
     * <ol>
     *   <li>Validate that the image URL is valid.</li>
     *   <li>Resolve a storage reference from the URL.</li>
     *   <li>Execute the delete operation.</li>
     *   <li>Return the result.</li>
     * </ol>
     *
     * <p>Caveats:</p>
     * <ul>
     *   <li>Deletion is irreversible.</li>
     *   <li>If the file is missing, Firebase returns a 404 error.</li>
     *   <li>After deletion, the original download URL no longer works.</li>
     * </ul>
     *
     * @param imageUrl Download URL of the image to remove.
     * @param listener Completion listener.
     *                 Returns null on success.
     *                 Returns the exception on failure (e.g., missing file, insufficient permissions).
     */
    public void deleteImage(String imageUrl, OnCompleteListener<Void> listener) {
        // Validate that the URL is usable.
        if (imageUrl == null || imageUrl.isEmpty()) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Image URL is null or empty")));
            return;
        }

        // Resolve a storage reference from the download URL.
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        // Perform the delete operation.
        imageRef.delete().addOnCompleteListener(listener);
    }
}
