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

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchiveservices.mediaTasks.WikiDataCompanyTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.custom.CustomDatePickerField;

public class CompanyFragment extends AbstractFragment<Company> {
    private EditText txtCompanyTitle, txtCompanyDescription;
    private CustomDatePickerField txtCompanyFoundation;
    private ImageButton cmdCompanySearch;
    private Company company;
    private Validator validator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.company_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.txtCompanyTitle = view.findViewById(R.id.txtCompanyTitle);
        this.txtCompanyFoundation = view.findViewById(R.id.txtCompanyFoundation);
        this.txtCompanyDescription = view.findViewById(R.id.txtCompanyDescription);
        this.cmdCompanySearch = view.findViewById(R.id.cmdCompanySearch);

        this.cmdCompanySearch.setOnClickListener(event -> {
            try {
                this.company = this.getMediaObject();
                WikiDataCompanyTask wikiDataCompanyTask = new WikiDataCompanyTask(this.getActivity(), MainActivity.GLOBALS.getSettings(this.requireContext()).isNotifications(), R.drawable.icon_notification);
                wikiDataCompanyTask.after(companies -> {
                    abstractPagerAdapter.setMediaObject(companies.get(0));
                });
                wikiDataCompanyTask.execute(new Company[] {this.company});
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
            }
        });

        this.validator = this.initValidation(this.validator);
        this.changeMode(false);
    }

    @Override
    public void setMediaObject(Company company) {
        this.company = company;

        this.txtCompanyTitle.setText(this.company.getTitle());
        this.txtCompanyFoundation.setDate(this.company.getFoundation());
        this.txtCompanyDescription.setText(this.company.getDescription());
    }

    @Override
    public Company getMediaObject() {
        try {
            this.company.setTitle(this.txtCompanyTitle.getText().toString());
            this.company.setDescription(this.txtCompanyDescription.getText().toString());
            this.company.setFoundation(this.txtCompanyFoundation.getDate());
        } catch (Exception ignored) {}

        return this.company;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.txtCompanyTitle.setEnabled(editMode);
        this.txtCompanyFoundation.setEnabled(editMode);
        this.txtCompanyDescription.setEnabled(editMode);
        this.cmdCompanySearch.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        this.validator = validator;
        if(this.txtCompanyTitle != null  && this.validator != null) {
            this.validator.addEmptyValidator(this.txtCompanyTitle);
        }
        return this.validator;
    }
}
