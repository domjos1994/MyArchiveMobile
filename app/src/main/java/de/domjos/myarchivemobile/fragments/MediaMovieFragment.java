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

import java.util.Objects;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;

public class MediaMovieFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaMovieLength, txtMediaMoviePath, txtMediaMovieLastSeen;
    private Spinner spMediaMovieType;
    private CustomSpinnerAdapter<String> typeAdapter;

    private Movie movie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaMovieLength = view.findViewById(R.id.txtMediaMovieLength);
        this.txtMediaMoviePath = view.findViewById(R.id.txtMediaMoviePath);
        this.spMediaMovieType = view.findViewById(R.id.spMediaMovieType);
        this.txtMediaMovieLastSeen = view.findViewById(R.id.txtMediaMovieLastSeen);

        this.typeAdapter = new CustomSpinnerAdapter<>(Objects.requireNonNull(this.getActivity()));
        for(Movie.Type type : Movie.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaMovieType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        if(baseMediaObject instanceof Movie) {
            this.movie = (Movie) baseMediaObject;
            this.txtMediaMovieLength.setText(ConvertHelper.convertDoubleToString(this.movie.getLength()));
            this.txtMediaMoviePath.setText(this.movie.getPath());

            if(this.movie.getType() != null) {
                this.spMediaMovieType.setSelection(this.typeAdapter.getPosition(this.movie.getType().name()));
            }
            if(this.movie.getLastSeen() != null) {
                this.txtMediaMovieLastSeen.setText(ConvertHelper.convertDateToString(this.movie.getLastSeen(), this.getString(R.string.sys_date_format)));
            } else {
                this.txtMediaMovieLastSeen.setText("");
            }
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaMovieLength.getText().toString().isEmpty()) {
            this.movie.setLength(ConvertHelper.convertStringToDouble(this.txtMediaMovieLength.getText().toString()));
        }
        this.movie.setPath(this.txtMediaMoviePath.getText().toString());
        this.movie.setType(Movie.Type.valueOf(this.spMediaMovieType.getSelectedItem().toString()));
        try {
            if(!this.txtMediaMovieLastSeen.getText().toString().isEmpty()) {
                this.movie.setLastSeen(ConvertHelper.convertStringToDate(this.txtMediaMovieLastSeen.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ex) {
            this.movie.setLastSeen(null);
        }
        return this.movie;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaMovieLength.setEnabled(editMode);
        this.txtMediaMoviePath.setEnabled(editMode);
        this.spMediaMovieType.setEnabled(editMode);
        this.txtMediaMovieLastSeen.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
