/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivemobile.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;

public class MediaCoverFragment<T> extends AbstractFragment<T> {
    private ImageButton cmdMediaCoverPhoto, cmdMediaCoverGallery;
    private ImageView ivMediaCover;
    private Uri imageUri;

    private T object;

    ActivityResultLauncher<androidx.activity.result.PickVisualMediaRequest> galleryResultLauncher = registerForActivityResult(
            new ActivityResultContracts.PickVisualMedia(),
            result -> {
                if(result != null) {
                    Bitmap bitmap = uriToBitmap(result);
                    if(bitmap!=null) {
                        this.ivMediaCover.setImageBitmap(bitmap);
                    }
                }
            });
    ActivityResultLauncher<Uri> cameraResultLauncher = registerForActivityResult(
            new ActivityResultContracts.TakePicture(),
            result -> {
                if (result) {
                    this.ivMediaCover.setImageBitmap(uriToBitmap(this.imageUri));
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_cover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.cmdMediaCoverPhoto = view.findViewById(R.id.cmdMediaCoverPhoto);
        this.cmdMediaCoverGallery = view.findViewById(R.id.cmdMediaCoverGallery);
        this.ivMediaCover = view.findViewById(R.id.ivMediaCover);

        this.cmdMediaCoverGallery.setOnClickListener(view1 -> {
            PickVisualMediaRequest.Builder builder = new PickVisualMediaRequest.Builder();
            builder.setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE);
            galleryResultLauncher.launch(builder.build());
        });
        this.cmdMediaCoverPhoto.setOnClickListener(view1 -> {
            this.imageUri = this.createImageUri();
            cameraResultLauncher.launch(this.imageUri);
        });

        this.changeMode(false);
    }

    private Uri createImageUri() {
        Activity act = this.requireActivity();
        File image = new File(act.getExternalCacheDir(), Calendar.getInstance().getTimeInMillis() + ".jpg");
        return FileProvider.getUriForFile(act, act.getPackageName() + ".provider", image);
    }

    private Bitmap uriToBitmap(Uri uri) {
        try {
            InputStream inputStream = requireActivity().getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception ignored) {}
        return null;
    }

    @Override
    public void setMediaObject(T object) {
        this.object = object;

        if(this.ivMediaCover != null) {
            if(this.object instanceof BaseMediaObject baseMediaObject) {
                if(baseMediaObject.getCover() != null) {
                    this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(baseMediaObject.getCover(), 0, baseMediaObject.getCover().length));
                } else {
                    this.ivMediaCover.setImageBitmap(null);
                }
            }
            if(this.object instanceof Person person) {
                if(person.getImage() != null) {
                    this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(person.getImage(), 0, person.getImage().length));
                } else {
                    this.ivMediaCover.setImageBitmap(null);
                }
            }
            if(this.object instanceof Company company) {
                if(company.getCover() != null) {
                    this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(company.getCover(), 0, company.getCover().length));
                } else {
                    this.ivMediaCover.setImageBitmap(null);
                }
            }
        }
    }

    @Override
    public T getMediaObject() {
        if(this.object instanceof BaseMediaObject && this.ivMediaCover.getDrawable()!=null) {
            try {
                ((BaseMediaObject) this.object).setCover(ConvertHelper.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }
        if(this.object instanceof Person && this.ivMediaCover.getDrawable() != null) {
            try {
                ((Person) this.object).setImage(ConvertHelper.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }
        if(this.object instanceof Company && this.ivMediaCover.getDrawable() != null) {
            try {
                ((Company) this.object).setCover(ConvertHelper.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }

        return this.object;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.cmdMediaCoverPhoto.setEnabled(editMode);
        this.cmdMediaCoverGallery.setEnabled(editMode);
        this.ivMediaCover.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
