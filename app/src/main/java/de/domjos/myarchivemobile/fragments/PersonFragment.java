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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.tasks.WikiDataPersonTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;

public class PersonFragment extends AbstractFragment<Person> {
    private EditText txtPersonFirstName, txtPersonLastName, txtPersonBirthDate, txtPersonDescription;
    private ImageButton cmdPersonSearch;
    private Person person;
    private Validator validator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.person_fragment, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtPersonFirstName = view.findViewById(R.id.txtPersonFirstName);
        this.txtPersonLastName = view.findViewById(R.id.txtPersonLastName);
        this.txtPersonBirthDate = view.findViewById(R.id.txtPersonBirthDate);
        this.txtPersonDescription = view.findViewById(R.id.txtPersonDescription);
        this.cmdPersonSearch = view.findViewById(R.id.cmdPersonSearch);

        this.cmdPersonSearch.setOnClickListener(event -> {
            try {
                this.person = this.getMediaObject();
                WikiDataPersonTask wikiDataPersonTask = new WikiDataPersonTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);
                this.abstractPagerAdapter.setMediaObject(wikiDataPersonTask.execute(this.person).get().get(0));
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
            }
        });

        this.validator = this.initValidation(this.validator);
        this.changeMode(false);
    }

    @Override
    public void setMediaObject(Person person) {
        this.person = person;

        this.txtPersonFirstName.setText(this.person.getFirstName());
        this.txtPersonLastName.setText(this.person.getLastName());
        if(this.person.getBirthDate() != null) {
            this.txtPersonBirthDate.setText(ConvertHelper.convertDateToString(this.person.getBirthDate(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtPersonBirthDate.setText("");
        }
        this.txtPersonDescription.setText(this.person.getDescription());
    }

    @Override
    public Person getMediaObject() {
        try {
            this.person.setFirstName(this.txtPersonFirstName.getText().toString());
            this.person.setLastName(this.txtPersonLastName.getText().toString());
            this.person.setDescription(this.txtPersonDescription.getText().toString());
            if(!this.txtPersonBirthDate.getText().toString().isEmpty()) {
                this.person.setBirthDate(ConvertHelper.convertStringToDate(this.txtPersonBirthDate.getText().toString(), this.getString(R.string.sys_date_format)));
            }
        } catch (Exception ignored) {}

        return this.person;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtPersonFirstName.setEnabled(editMode);
        this.txtPersonLastName.setEnabled(editMode);
        this.txtPersonBirthDate.setEnabled(editMode);
        this.txtPersonDescription.setEnabled(editMode);
        this.cmdPersonSearch.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        this.validator = validator;
        if(this.txtPersonLastName != null  && this.validator != null) {
            this.validator.addEmptyValidator(this.txtPersonLastName);
        }
        return this.validator;
    }
}
