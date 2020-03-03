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
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;

public class MediaGameFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaGameLength, txtMediaGameLastPlayed;
    private Spinner spMediaGameType;
    private CustomSpinnerAdapter<String> typeAdapter;

    private Game game;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_game, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaGameLength = view.findViewById(R.id.txtMediaGameLength);
        this.spMediaGameType = view.findViewById(R.id.spMediaGameType);
        this.txtMediaGameLastPlayed = view.findViewById(R.id.txtMediaGameLastPlayed);

        this.typeAdapter = new CustomSpinnerAdapter<>(Objects.requireNonNull(this.getActivity()));
        for(Game.Type type : Game.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaGameType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        if(baseMediaObject instanceof Game) {
            this.game = (Game) baseMediaObject;
            this.txtMediaGameLength.setText(ConvertHelper.convertDoubleToString(this.game.getLength()));

            if(this.game.getType() != null) {
                this.spMediaGameType.setSelection(this.typeAdapter.getPosition(this.game.getType().name()));
            }
            if(this.game.getLastPlayed() != null) {
                this.txtMediaGameLastPlayed.setText(ConvertHelper.convertDateToString(this.game.getLastPlayed(), this.getString(R.string.sys_date_format)));
            } else {
                this.txtMediaGameLastPlayed.setText("");
            }
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        try {
            if(!this.txtMediaGameLength.getText().toString().isEmpty()) {
                this.game.setLength(ConvertHelper.convertStringToDouble(this.txtMediaGameLength.getText().toString()));
            }
        } catch (Exception ignored) {}
        this.game.setType(Game.Type.valueOf(this.spMediaGameType.getSelectedItem().toString()));
        try {
            if(!this.txtMediaGameLastPlayed.getText().toString().isEmpty()) {
                this.game.setLastPlayed(ConvertHelper.convertStringToDate(this.txtMediaGameLastPlayed.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ex) {
            this.game.setLastPlayed(null);
        }
        return this.game;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaGameLength.setEnabled(editMode);
        this.spMediaGameType.setEnabled(editMode);
        this.txtMediaGameLastPlayed.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
