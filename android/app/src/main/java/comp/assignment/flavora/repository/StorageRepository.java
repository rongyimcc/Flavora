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
 * Storage Repository
 * <p>
 * Handles all operations related to Firebase Storage, mainly for uploading,
 * downloading, and deleting images. Uses the Singleton pattern to ensure a
 * single repository instance across the app.
 * </p>
 *
 * <p>Main features:</p>
 * <ul>
 *   <li>Batch image upload: upload multiple images in parallel and return all download URLs</li>
 *   <li>Single image upload: upload a single image and return its download URL</li>
 *   <li>Image deletion: delete an image file in Firebase Storage by its URL</li>
 * </ul>
 *
 * <p>Use cases:</p>
 * <ul>
 *   <li>User avatar upload</li>
 *   <li>Post image upload (supports multiple images)</li>
 *   <li>Image resource management and cleanup</li>
 * </ul>
 *
 * @author
 * Flavora Team
 * @version 1.0
 * @since 1.0
 */
public class StorageRepository {
    /** Singleton instance */
    private static StorageRepository instance;
    /** Firebase Storage instance */
    private final FirebaseStorage storage;
    /** Root storage reference */
    private final StorageReference storageRef;

    /**
     * Private constructor
     * <p>
     * Initializes the Firebase Storage instance and root storage reference.
     * Private to prevent external instantiation and enforce the Singleton pattern.
     * </p>
     */
    private StorageRepository() {
        this.storage = FirebaseStorage.getInstance();
        this.storageRef = storage.getReference();
    }

    /**
     * Returns the singleton instance of StorageRepository
     * <p>
     * Uses double-checked locking (DCL) for thread-safe lazy initialization.
     * </p>
     *
     * @return the unique instance of StorageRepository
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
     * Supports uploading multiple images concurrently. Each image is given a
     * unique filename generated via UUID. After all uploads complete, returns
     * the list of download URLs.
     * </p>
     *
     * <p>Upload steps:</p>
     * <ol>
     *   <li>Validate input; if the list is empty, return an empty list</li>
     *   <li>Generate a unique filename (UUID) for each image</li>
     *   <li>Create an upload task for each image and add it to the task list</li>
     *   <li>Wait for all upload tasks to complete</li>
     *   <li>Collect and return all image download URLs</li>
     * </ol>
     *
     * <p>Filename rule:</p>
     * <pre>
     * images/[UUID].jpg
     * e.g. images/550e8400-e29b-41d4-a716-446655440000.jpg
     * </pre>
     *
     * @param imageUris list of image URIs selected from local storage or gallery
     * @param listener  completion listener:
     *                  on success, returns a list of download URL strings;
     *                  on failure, returns an exception
     */
    public void uploadImages(List<Uri> imageUris, OnCompleteListener<List<String>> listener) {
        // Validate parameters: check whether the image list is empty
        if (imageUris == null || imageUris.isEmpty()) {
            listener.onComplete(Tasks.forResult(new ArrayList<>()));
            return;
        }

        // Prepare tasks for concurrent uploads
        List<Task<Uri>> uploadTasks = new ArrayList<>();

        // Create an upload task for each image
        for (Uri imageUri : imageUris) {
            // Generate a unique filename to avoid conflicts
            String filename = "images/" + UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(filename);

            // Chain: upload file -> get download URL
            Task<Uri> uploadTask = imageRef.putFile(imageUri)
                    .continueWithTask(task -> {
                        if (!task.isSuccessful() && task.getException() != null) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    });

            uploadTasks.add(uploadTask);
        }

        // Wait for all uploads to succeed
        Tasks.whenAllSuccess(uploadTasks).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> downloadUrls = new ArrayList<>();
                for (Object result : task.getResult()) {
                    if (result instanceof Uri) {
                        downloadUrls.add(result.toString());
                    }
                }
                listener.onComplete(Tasks.forResult(downloadUrls));
            } else {
                listener.onComplete(Tasks.forException(
                        task.getException() != null ? task.getException() :
                                new Exception("Failed to upload images")));
            }
        });
    }

    /**
     * Uploads a single image to Firebase Storage.
     * <p>
     * Intended for single-image scenarios such as avatar upload.
     * Returns a publicly accessible download URL upon success.
     * </p>
     *
     * <p>Upload steps:</p>
     * <ol>
     *   <li>Validate that the image URI is not null</li>
     *   <li>Generate a unique filename</li>
     *   <li>Upload the image file</li>
     *   <li>Fetch and return the download URL</li>
     * </ol>
     *
     * @param imageUri the image URI to upload
     * @param listener completion listener:
     *                 on success, returns the download URL as a string;
     *                 on failure, returns an exception (e.g., network error, permission denied)
     */
    public void uploadImage(Uri imageUri, OnCompleteListener<String> listener) {
        // Validate parameter
        if (imageUri == null) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Image URI is null")));
            return;
        }

        // Generate a unique filename
        String filename = "images/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child(filename);

        // Execute upload
        imageRef.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return imageRef.getDownloadUrl();
                })
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        listener.onComplete(Tasks.forResult(task.getResult().toString()));
                    } else {
                        listener.onComplete(Tasks.forException(
                                task.getException() != null ? task.getException() :
                                        new Exception("Failed to upload image")));
                    }
                });
    }

    /**
     * Deletes an image from Firebase Storage.
     * <p>
     * Deletes the file by its download URL. Commonly used when a user deletes a post
     * or changes avatar to clean up the old image resource.
     * </p>
     *
     * <p>Deletion steps:</p>
     * <ol>
     *   <li>Validate that the image URL is valid</li>
     *   <li>Resolve a Storage reference from the URL</li>
     *   <li>Perform the delete operation</li>
     *   <li>Return the deletion result</li>
     * </ol>
     *
     * <p>Cautions:</p>
     * <ul>
     *   <li>Deletion is irreversible—use with care</li>
     *   <li>If the file does not exist, Firebase returns a 404 error</li>
     *   <li>After a successful deletion, the original download URL becomes invalid</li>
     * </ul>
     *
     * @param imageUrl the download URL of the image to delete
     * @param listener completion listener:
     *                 on success returns null;
     *                 on failure returns an exception (e.g., not found, permission denied)
     */
    public void deleteImage(String imageUrl, OnCompleteListener<Void> listener) {
        // Validate parameter
        if (imageUrl == null || imageUrl.isEmpty()) {
            listener.onComplete(Tasks.forException(new IllegalArgumentException("Image URL is null or empty")));
            return;
        }

        // Resolve reference and delete
        StorageReference imageRef = storage.getReferenceFromUrl(imageUrl);
        imageRef.delete().addOnCompleteListener(listener);
    }
}
