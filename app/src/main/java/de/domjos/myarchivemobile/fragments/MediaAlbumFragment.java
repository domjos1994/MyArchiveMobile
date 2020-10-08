/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.custom.CustomDatePickerField;

public class MediaAlbumFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaAlbumNumberOfDisks;
    private CustomDatePickerField txtMediaAlbumLastHeard;
    private Spinner spMediaAlbumType;
    private CustomSpinnerAdapter<String> typeAdapter;

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
        this.txtMediaAlbumLastHeard = view.findViewById(R.id.txtMediaAlbumLastHeard);

        this.typeAdapter = new CustomSpinnerAdapter<>(this.requireActivity());
        for(Album.Type type : Album.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaAlbumType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        if(baseMediaObject instanceof Album) {
            this.album = (Album) baseMediaObject;
            this.txtMediaAlbumNumberOfDisks.setText(String.valueOf(this.album.getNumberOfDisks()));

            if(this.album.getType() != null) {
                this.spMediaAlbumType.setSelection(this.typeAdapter.getPosition(this.album.getType().name()));
            }
            this.txtMediaAlbumLastHeard.setDate(this.album.getLastHeard());
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaAlbumNumberOfDisks.getText().toString().isEmpty()) {
            this.album.setNumberOfDisks(Integer.parseInt(this.txtMediaAlbumNumberOfDisks.getText().toString()));
        }
        this.album.setType(Album.Type.valueOf(this.spMediaAlbumType.getSelectedItem().toString()));
        this.album.setLastHeard(this.txtMediaAlbumLastHeard.getDate());
        return this.album;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaAlbumNumberOfDisks.setEnabled(editMode);
        this.spMediaAlbumType.setEnabled(editMode);
        this.txtMediaAlbumLastHeard.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
