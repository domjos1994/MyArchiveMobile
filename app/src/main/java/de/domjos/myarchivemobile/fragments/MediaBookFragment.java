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
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivemobile.R;

public class MediaBookFragment extends AbstractFragment {
    private EditText txtMediaBookNumberOfPages, txtMediaBookPath;
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

        this.spMediaBookType = view.findViewById(R.id.spMediaBookType);
        this.typeAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getActivity()), android.R.layout.simple_spinner_item);
        for(Book.Type type : Book.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaBookType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.book = (Book) baseMediaObject;
        if(this.txtMediaBookNumberOfPages != null) {
            this.txtMediaBookNumberOfPages.setText(String.valueOf(this.book.getNumberOfPages()));
            this.txtMediaBookPath.setText(this.book.getPath());

            if(this.book.getType() != null) {
                this.spMediaBookType.setSelection(this.typeAdapter.getPosition(this.book.getType().name()));
            }
        }
    }

    @Override
    public BaseMediaObject getMediaObject() {
        if(!this.txtMediaBookNumberOfPages.getText().toString().isEmpty()) {
            this.book.setNumberOfPages(Integer.parseInt(this.txtMediaBookNumberOfPages.getText().toString()));
        }
        this.book.setPath(this.txtMediaBookPath.getText().toString());
        this.book.setType(Book.Type.valueOf(this.spMediaBookType.getSelectedItem().toString()));
        return this.book;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaBookNumberOfPages.setEnabled(editMode);
        this.txtMediaBookPath.setEnabled(editMode);
        this.spMediaBookType.setEnabled(editMode);
    }

    @Override
    public void initValidation() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
