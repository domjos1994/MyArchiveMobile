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

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivemobile.R;

public class MediaMovieFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaMovieLength, txtMediaMoviePath;
    private Spinner spMediaMovieType;
    private ArrayAdapter<String> typeAdapter;

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

        this.typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_item);
        for(Movie.Type type : Movie.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaMovieType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.movie = (Movie) baseMediaObject;
        this.txtMediaMovieLength.setText(String.valueOf(this.movie.getLength()));
        this.txtMediaMoviePath.setText(this.movie.getPath());

        if(this.movie.getType() != null) {
            this.spMediaMovieType.setSelection(this.typeAdapter.getPosition(this.movie.getType().name()));
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaMovieLength.getText().toString().isEmpty()) {
            this.movie.setLength(Double.parseDouble(this.txtMediaMovieLength.getText().toString()));
        }
        this.movie.setPath(this.txtMediaMoviePath.getText().toString());
        this.movie.setType(Movie.Type.valueOf(this.spMediaMovieType.getSelectedItem().toString()));
        return this.movie;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaMovieLength.setEnabled(editMode);
        this.txtMediaMoviePath.setEnabled(editMode);
        this.spMediaMovieType.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
