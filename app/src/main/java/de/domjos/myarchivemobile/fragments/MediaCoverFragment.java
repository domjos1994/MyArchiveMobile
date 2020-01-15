package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.utils.IntentHelper;
import de.domjos.myarchivemobile.R;

public class MediaCoverFragment extends AbstractFragment {
    private ImageButton cmdMediaCoverPhoto, cmdMediaCoverGallery;
    private ImageView ivMediaCover;

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

        this.cmdMediaCoverGallery.setOnClickListener(view1 -> IntentHelper.startGalleryIntent(Objects.requireNonNull(this.getActivity())));
        this.cmdMediaCoverPhoto.setOnClickListener(view1 -> IntentHelper.startCameraIntent(Objects.requireNonNull(this.getActivity())));

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        if(this.ivMediaCover != null) {
            if(baseMediaObject.getCover() != null) {
                this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(baseMediaObject.getCover(), 0, baseMediaObject.getCover().length));
            } else {
                this.ivMediaCover.setImageBitmap(null);
            }
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        BaseMediaObject baseMediaObject = new BaseMediaObject();
        if(this.ivMediaCover.getDrawable()!=null) {
            try {
                baseMediaObject.setCover(Converter.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }
        return baseMediaObject;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.cmdMediaCoverPhoto.setEnabled(editMode);
        this.cmdMediaCoverGallery.setEnabled(editMode);
        this.ivMediaCover.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Bitmap bitmap = IntentHelper.getCameraIntentResult(requestCode, resultCode, intent);
        if(bitmap!=null) {
            this.ivMediaCover.setImageBitmap(bitmap);
        } else {
            bitmap = IntentHelper.getGalleryIntentResult(requestCode, resultCode, intent, this.getActivity());
            if(bitmap != null) {
                this.ivMediaCover.setImageBitmap(bitmap);
            }
        }
    }
}
