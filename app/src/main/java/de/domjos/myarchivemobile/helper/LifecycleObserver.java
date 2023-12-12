package de.domjos.myarchivemobile.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.FileInputStream;
import java.io.IOException;

import de.domjos.myarchivelibrary.utils.IntentHelper;

public class LifecycleObserver implements DefaultLifecycleObserver {
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<PickVisualMediaRequest> pickLauncher;
    private ActivityResultLauncher<Void> cameraLauncher;
    private final IntentHelper.Image gallery;
    private final IntentHelper.Image camera;
    private final Context context;

    public LifecycleObserver(
            @NonNull ActivityResultRegistry registry,
            Context context,
            IntentHelper.Image gallery, IntentHelper.Image camera) {
        this.mRegistry = registry;
        this.gallery = gallery;
        this.camera = camera;
        this.context = context;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        this.pickLauncher = this.mRegistry.register("pickLauncher",
                new ActivityResultContracts.PickVisualMedia(),
                (result) -> {
                    ContentResolver resolver = this.context.getContentResolver();
                    try (ParcelFileDescriptor parcelFileDescriptor = resolver.openFileDescriptor(result, "r")) {
                        if(parcelFileDescriptor != null) {
                            this.gallery.getImage(BitmapFactory.decodeStream(new FileInputStream(parcelFileDescriptor.getFileDescriptor())));
                        }
                    } catch (IOException ignored) {}
                });
        this.cameraLauncher = this.mRegistry.register("camera",
                new ActivityResultContracts.TakePicturePreview(),
                result -> {
                    if(result != null) this.camera.getImage(result);
                });
    }

    public void startGallery() {
        this.pickLauncher.launch(new PickVisualMediaRequest());
    }

    public void startCamera() {
        this.cameraLauncher.launch(null);
    }
}
