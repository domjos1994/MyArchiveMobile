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
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivemobile.R;

public class MediaGameFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaGameLength;
    private Spinner spMediaGameType;
    private ArrayAdapter<String> typeAdapter;

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

        this.typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_item);
        for(Game.Type type : Game.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaGameType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.game = (Game) baseMediaObject;
        this.txtMediaGameLength.setText(String.valueOf(this.game.getLength()));

        if(this.game.getType() != null) {
            this.spMediaGameType.setSelection(this.typeAdapter.getPosition(this.game.getType().name()));
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaGameLength.getText().toString().isEmpty()) {
            this.game.setLength(Double.parseDouble(this.txtMediaGameLength.getText().toString()));
        }
        this.game.setType(Game.Type.valueOf(this.spMediaGameType.getSelectedItem().toString()));
        return this.game;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaGameLength.setEnabled(editMode);
        this.spMediaGameType.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
