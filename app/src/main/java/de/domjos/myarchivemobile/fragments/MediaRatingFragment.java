package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;

public class MediaRatingFragment extends AbstractFragment<BaseMediaObject> {
    private RatingBar rbRatingOwn, rbRatingWeb;
    private EditText txtRatingNote;

    private BaseMediaObject baseMediaObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_rating, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.rbRatingOwn = view.findViewById(R.id.rbRatingOwn);
        this.rbRatingWeb = view.findViewById(R.id.rbRatingWeb);
        this.txtRatingNote = view.findViewById(R.id.txtRatingNote);

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.baseMediaObject = baseMediaObject;

        this.rbRatingOwn.setRating((float) this.baseMediaObject.getRatingOwn());
        this.rbRatingWeb.setRating((float) this.baseMediaObject.getRatingWeb());
        this.txtRatingNote.setText(this.baseMediaObject.getRatingNote());
    }

    @Override
    public BaseMediaObject getMediaObject() {
        this.baseMediaObject.setRatingOwn(this.rbRatingOwn.getRating());
        this.baseMediaObject.setRatingWeb(this.rbRatingWeb.getRating());
        this.baseMediaObject.setRatingNote(this.txtRatingNote.getText().toString());

        return this.baseMediaObject;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.rbRatingWeb.setEnabled(editMode);
        this.rbRatingOwn.setEnabled(editMode);
        this.txtRatingNote.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
