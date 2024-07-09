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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.tokenizer.CommaTokenizer;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.CompanyActivity;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.activities.PersonActivity;
import de.domjos.myarchivemobile.adapter.CustomAutoCompleteAdapter;

public class MediaPersonsCompaniesFragment extends AbstractFragment<BaseMediaObject> {
    private MultiAutoCompleteTextView txtMediaPersons, txtMediaCompanies;
    private SwipeRefreshDeleteList lvMediaPersons, lvMediaCompanies;

    private BaseMediaObject baseMediaObject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_persons_companies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtMediaPersons = view.findViewById(R.id.txtMediaPersons);
        this.lvMediaPersons = view.findViewById(R.id.lvMediaPersons);
        this.txtMediaCompanies = view.findViewById(R.id.txtMediaCompanies);
        this.lvMediaCompanies = view.findViewById(R.id.lvMediaCompanies);

        try {
            this.txtMediaPersons.setTokenizer(new CommaTokenizer());
            CustomAutoCompleteAdapter<String> personsAdapter = new CustomAutoCompleteAdapter<>(this.requireActivity(), this.txtMediaPersons);
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
            CustomAutoCompleteAdapter<String> companiesAdapter = new CustomAutoCompleteAdapter<>(this.requireActivity(), this.txtMediaCompanies);
            for(Company company : MainActivity.GLOBALS.getDatabase().getCompanies("", 0)) {
                companiesAdapter.add(company.getTitle());
            }
            this.txtMediaPersons.setAdapter(companiesAdapter);
            companiesAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        this.lvMediaPersons.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) baseDescriptionObject -> {
            Intent intent = new Intent(this.getActivity(), PersonActivity.class);
            intent.putExtra("id", baseDescriptionObject.getId());
            startActivity(intent);
        });

        this.lvMediaCompanies.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) baseDescriptionObject -> {
            Intent intent = new Intent(this.getActivity(), CompanyActivity.class);
            intent.putExtra("id", baseDescriptionObject.getId());
            startActivity(intent);
        });

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(BaseMediaObject baseMediaObject) {
        this.baseMediaObject = baseMediaObject;

        this.lvMediaPersons.getAdapter().clear();
        this.lvMediaCompanies.getAdapter().clear();

        StringBuilder persons = new StringBuilder();
        for(Person person : this.baseMediaObject.getPersons()) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(person.toString());
            baseDescriptionObject.setCover(person.getImage());
            baseDescriptionObject.setId(person.getId());
            if(person.getBirthDate() != null) {
                baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(person.getBirthDate(), this.getString(R.string.sys_date_format)));
            }
            this.lvMediaPersons.getAdapter().add(baseDescriptionObject);

            persons.append(person.getFirstName()).append(" ").append(person.getLastName()).append(", ");
        }
        this.txtMediaPersons.setText(persons.toString());

        StringBuilder companies = new StringBuilder();
        for(Company company : this.baseMediaObject.getCompanies()) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(company.getTitle());
            baseDescriptionObject.setCover(company.getCover());
            baseDescriptionObject.setId(company.getId());
            if(company.getFoundation() != null) {
                baseDescriptionObject.setDescription(ConvertHelper.convertDateToString(company.getFoundation(), this.getString(R.string.sys_date_format)));
            }
            this.lvMediaCompanies.getAdapter().add(baseDescriptionObject);

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

        this.txtMediaCompanies.setVisibility(editMode ? View.VISIBLE : View.GONE);
        this.txtMediaPersons.setVisibility(editMode ? View.VISIBLE : View.GONE);
        this.lvMediaCompanies.setVisibility(editMode ? View.GONE : View.VISIBLE);
        this.lvMediaPersons.setVisibility(editMode ? View.GONE : View.VISIBLE);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
