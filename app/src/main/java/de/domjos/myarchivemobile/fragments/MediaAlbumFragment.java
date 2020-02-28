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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;

public class MediaAlbumFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaAlbumNumberOfDisks, txtMediaAlbumLastHeard;
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
        this.txtMediaAlbumLastHeard = view.findViewById(R.id.txtMediaAlbumLastHeard);

        this.typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), R.layout.spinner_item);
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
            if(this.album.getLastHeard() != null) {
                this.txtMediaAlbumLastHeard.setText(ConvertHelper.convertDateToString(this.album.getLastHeard(), this.getString(R.string.sys_date_format)));
            } else {
                this.txtMediaAlbumLastHeard.setText("");
            }
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaAlbumNumberOfDisks.getText().toString().isEmpty()) {
            this.album.setNumberOfDisks(Integer.parseInt(this.txtMediaAlbumNumberOfDisks.getText().toString()));
        }
        this.album.setType(Album.Type.valueOf(this.spMediaAlbumType.getSelectedItem().toString()));
        try {
            if(!this.txtMediaAlbumLastHeard.getText().toString().isEmpty()) {
                this.album.setLastHeard(ConvertHelper.convertStringToDate(this.txtMediaAlbumLastHeard.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ex) {
            this.album.setLastHeard(null);
        }
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
