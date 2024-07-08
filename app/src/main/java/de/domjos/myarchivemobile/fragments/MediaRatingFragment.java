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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;

public class MediaRatingFragment extends AbstractFragment<BaseMediaObject> {
    private RatingBar rbRatingOwn, rbRatingWeb;
    private TextView lblRatingOwn, lblRatingWeb;
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
        this.lblRatingOwn = view.findViewById(R.id.lblRatingOwn);
        this.rbRatingWeb = view.findViewById(R.id.rbRatingWeb);
        this.lblRatingWeb = view.findViewById(R.id.lblRatingWeb);
        this.txtRatingNote = view.findViewById(R.id.txtRatingNote);

        this.rbRatingOwn.setOnRatingBarChangeListener((ratingBar, v, b) -> this.lblRatingOwn.setText(ConvertHelper.convertDoubleToString(v * 10.0)));
        this.rbRatingWeb.setOnRatingBarChangeListener((ratingBar, v, b) -> this.lblRatingWeb.setText(ConvertHelper.convertDoubleToString(v * 10.0)));

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
}
