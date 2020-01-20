package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;

public class MediaAlbumFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaAlbumNumberOfDisks;
    private Spinner spMediaAlbumType;
    private ArrayAdapter<String> typeAdapter;

    private Album album;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaAlbumNumberOfDisks = view.findViewById(R.id.txtMediaAlbumNumberOfDisks);
        this.spMediaAlbumType = view.findViewById(R.id.spMediaAlbumType);

        this.typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_item);
        for(Album.Type type : Album.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaAlbumType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.album = (Album) baseMediaObject;
        this.txtMediaAlbumNumberOfDisks.setText(String.valueOf(this.album.getNumberOfDisks()));

        if(this.album.getType() != null) {
            this.spMediaAlbumType.setSelection(this.typeAdapter.getPosition(this.album.getType().name()));
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaAlbumNumberOfDisks.getText().toString().isEmpty()) {
            this.album.setNumberOfDisks(Integer.parseInt(this.txtMediaAlbumNumberOfDisks.getText().toString()));
        }
        this.album.setType(Album.Type.valueOf(this.spMediaAlbumType.getSelectedItem().toString()));
        return this.album;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaAlbumNumberOfDisks.setEnabled(editMode);
        this.spMediaAlbumType.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
