/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2024 Dominic Joas.
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

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.util.Arrays;
import java.util.List;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.custom.CustomDatePickerField;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MediaBookFragment extends AbstractFragment<BaseMediaObject> {
    private EditText txtMediaBookNumberOfPages, txtMediaBookPath;
    private CustomDatePickerField txtMediaBookLastRead;
    private EditText txtMediaBookEdition, txtMediaBookTopics;
    private Spinner spMediaBookType;
    private CustomSpinnerAdapter<String> typeAdapter;
    private ImageButton cmdMediaBookPath;

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
        this.cmdMediaBookPath = view.findViewById(R.id.cmdMediaBookPath);

        this.txtMediaBookEdition = view.findViewById(R.id.txtMediaBookEdition);
        this.txtMediaBookTopics = view.findViewById(R.id.txtMediaBookTopics);
        this.txtMediaBookLastRead = view.findViewById(R.id.txtMediaBookLastRead);

        this.spMediaBookType = view.findViewById(R.id.spMediaBookType);
        this.typeAdapter = new CustomSpinnerAdapter<>(this.requireActivity());
        for(Book.Type type : Book.Type.values()) {
            this.typeAdapter.add(type.name());
        }
        this.spMediaBookType.setAdapter(this.typeAdapter);
        this.typeAdapter.notifyDataSetChanged();

        this.cmdMediaBookPath.setOnClickListener(event -> {
            List<String> filter = Arrays.asList("PDF", "pdf");
            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, false, filter, this.requireActivity());
            dialog.setDialogSelectionListener(files -> {
                if ( files != null) {
                    if(files.length != 0) {
                        txtMediaBookPath.setText(files[0]);
                        spMediaBookType.setSelection(this.typeAdapter.getPosition(Book.Type.eBook.name()));
                    }
                }
            });
            dialog.show();
        });

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        if(baseMediaObject instanceof Book) {
            this.book = (Book) baseMediaObject;
            this.txtMediaBookNumberOfPages.setText(String.valueOf(this.book.getNumberOfPages()));
            this.txtMediaBookPath.setText(this.book.getPath());
            this.txtMediaBookEdition.setText(this.book.getEdition());
            this.txtMediaBookTopics.setText(TextUtils.join("\n", this.book.getTopics()));

            if(this.book.getType() != null) {
                this.spMediaBookType.setSelection(this.typeAdapter.getPosition(this.book.getType().name()));
            }
            this.txtMediaBookLastRead.setDate(this.book.getLastRead());
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
        this.book.setLastRead(this.txtMediaBookLastRead.getDate());
        return this.book;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaBookNumberOfPages.setEnabled(editMode);
        this.txtMediaBookPath.setEnabled(editMode);
        this.spMediaBookType.setEnabled(editMode);
        this.txtMediaBookEdition.setEnabled(editMode);
        this.txtMediaBookTopics.setEnabled(editMode);
        this.txtMediaBookLastRead.setEnabled(editMode);
        this.cmdMediaBookPath.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
