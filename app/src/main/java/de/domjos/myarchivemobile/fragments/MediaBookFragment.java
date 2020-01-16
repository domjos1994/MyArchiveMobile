package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Objects;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivemobile.R;

public class MediaBookFragment extends AbstractFragment {
    private EditText txtMediaBookNumberOfPages, txtMediaBookPath;
    private EditText txtMediaBookEdition, txtMediaBookTopics;
    private Spinner spMediaBookType;
    private ArrayAdapter<String> typeAdapter;

    private Book book;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_book, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaBookNumberOfPages = view.findViewById(R.id.txtMediaBookNumberOfPages);
        this.txtMediaBookPath = view.findViewById(R.id.txtMediaBookPath);

        this.txtMediaBookEdition = view.findViewById(R.id.txtMediaBookEdition);
        this.txtMediaBookTopics = view.findViewById(R.id.txtMediaBookTopics);

        this.spMediaBookType = view.findViewById(R.id.spMediaBookType);
        this.typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_item);
        for(Book.Type type : Book.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaBookType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.book = (Book) baseMediaObject;
        this.txtMediaBookNumberOfPages.setText(String.valueOf(this.book.getNumberOfPages()));
        this.txtMediaBookPath.setText(this.book.getPath());
        this.txtMediaBookEdition.setText(this.book.getEdition());
        this.txtMediaBookTopics.setText(TextUtils.join("\n", this.book.getTopics()));

        if(this.book.getType() != null) {
            this.spMediaBookType.setSelection(this.typeAdapter.getPosition(this.book.getType().name()));
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaBookNumberOfPages.getText().toString().isEmpty()) {
            this.book.setNumberOfPages(Integer.parseInt(this.txtMediaBookNumberOfPages.getText().toString()));
        }
        this.book.setPath(this.txtMediaBookPath.getText().toString());
        this.book.setEdition(this.txtMediaBookEdition.getText().toString());
        this.book.setTopics(Arrays.asList(this.txtMediaBookTopics.getText().toString().split("\n")));
        this.book.setType(Book.Type.valueOf(this.spMediaBookType.getSelectedItem().toString()));
        return this.book;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaBookNumberOfPages.setEnabled(editMode);
        this.txtMediaBookPath.setEnabled(editMode);
        this.spMediaBookType.setEnabled(editMode);
        this.txtMediaBookEdition.setEnabled(editMode);
        this.txtMediaBookTopics.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
