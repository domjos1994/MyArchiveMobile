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
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import de.domjos.customwidgets.tokenizer.CommaTokenizer;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomAutoCompleteAdapter;

public class MediaPersonsCompaniesFragment extends AbstractFragment<BaseMediaObject> {
    private MultiAutoCompleteTextView txtMediaPersons, txtMediaCompanies;

    private BaseMediaObject baseMediaObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_persons_companies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaPersons = view.findViewById(R.id.txtMediaPersons);
        this.txtMediaCompanies = view.findViewById(R.id.txtMediaCompanies);

        try {
            this.txtMediaPersons.setTokenizer(new CommaTokenizer());
            CustomAutoCompleteAdapter<String> personsAdapter = new CustomAutoCompleteAdapter<>(Objects.requireNonNull(this.getActivity()), this.txtMediaPersons);
            for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("", 0)) {
                personsAdapter.add(person.getFirstName() + " " + person.getLastName());
            }
            this.txtMediaPersons.setAdapter(personsAdapter);
            personsAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        try {
            this.txtMediaCompanies.setTokenizer(new CommaTokenizer());
            CustomAutoCompleteAdapter<String> companiesAdapter = new CustomAutoCompleteAdapter<>(Objects.requireNonNull(this.getActivity()), this.txtMediaCompanies);
            for(Company company : MainActivity.GLOBALS.getDatabase().getCompanies("", 0)) {
                companiesAdapter.add(company.getTitle());
            }
            this.txtMediaPersons.setAdapter(companiesAdapter);
            companiesAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.baseMediaObject = baseMediaObject;

        StringBuilder persons = new StringBuilder();
        for(Person person : this.baseMediaObject.getPersons()) {
            persons.append(person.getFirstName()).append(" ").append(person.getLastName()).append(", ");
        }
        this.txtMediaPersons.setText(persons.toString());

        StringBuilder companies = new StringBuilder();
        for(Company company : this.baseMediaObject.getCompanies()) {
            companies.append(company.getTitle()).append(", ");
        }
        this.txtMediaCompanies.setText(companies.toString());
    }

    @Override
    public BaseMediaObject getMediaObject() {
        for(String person : this.txtMediaPersons.getText().toString().split(",")) {
            if(!person.trim().isEmpty()) {
                String[] spl = person.trim().split(" ");
                Person tmp = new Person();
                tmp.setFirstName(spl[0].trim());
                tmp.setLastName(person.replace(spl[0], "").trim());

                if(this.baseMediaObject.getPersons() != null) {
                    boolean contains = false;
                    if(!this.baseMediaObject.getPersons().isEmpty()) {
                        for(Person current : this.baseMediaObject.getPersons()) {
                            if(current.getFirstName().trim().equalsIgnoreCase(tmp.getFirstName().trim()) && current.getLastName().trim().equalsIgnoreCase(tmp.getLastName().trim())) {
                                contains = true;
                            }
                        }
                    }
                    if(!contains) {
                        this.baseMediaObject.getPersons().add(tmp);
                    }
                }
            }
        }

        for(String company : this.txtMediaCompanies.getText().toString().split(",")) {
            if(!company.trim().isEmpty()) {
                Company tmp = new Company();
                tmp.setTitle(company.trim());

                if(this.baseMediaObject.getCompanies() != null) {
                    boolean contains = false;
                    if(!this.baseMediaObject.getCompanies().isEmpty()) {
                        for(Company current : this.baseMediaObject.getCompanies()) {
                            if(current.getTitle().trim().equalsIgnoreCase(tmp.getTitle().trim())) {
                                contains = true;
                            }
                        }
                    }

                    if(!contains) {
                        this.baseMediaObject.getCompanies().add(tmp);
                    }
                }
            }
        }

        return this.baseMediaObject;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtMediaCompanies.setEnabled(editMode);
        this.txtMediaPersons.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
