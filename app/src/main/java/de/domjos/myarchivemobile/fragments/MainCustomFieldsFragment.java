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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MainCustomFieldsFragment extends ParentFragment {
    private ScrollView scrollView;

    private EditText txtCustomFieldTitle, txtCustomFieldDescription, txtCustomFieldAllowed;
    private Spinner spCustomFieldType;
    private CheckBox chkCustomFieldBooks, chkCustomFieldGames, chkCustomFieldMovies, chkCustomFieldAlbums;
    private SwipeRefreshDeleteList lvCustomFields;
    private BottomNavigationView bottomNavigationView;
    private String search;

    private CustomField customField = null;
    private Validator validator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_custom_fields, container, false);
        this.initControls(root);

        this.lvCustomFields.setOnReloadListener(MainCustomFieldsFragment.this::reload);

        this.lvCustomFields.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.customField = (CustomField) listObject.getObject();
            this.setObject(this.customField);
            this.changeMode(false, true);
        });

        this.lvCustomFields.setOnDeleteListener(listObject -> {
            this.customField = (CustomField) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(this.customField);
            this.customField = null;
            this.setObject(new CustomField());
            this.changeMode(false, false);
            this.reload();
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    setObject(new CustomField());
                    this.customField = null;
                    break;
                case R.id.cmdEdit:
                    if(customField != null) {
                        this.changeMode(true, true);
                        setObject(customField);
                    }
                    break;
                case R.id.cmdCancel:
                    changeMode(false, false);
                    setObject(new CustomField());
                    this.customField = null;
                    break;
                case R.id.cmdSave:
                    try {
                        if(this.validator.getState()) {
                            CustomField customField = this.getObject();
                            if(this.customField != null) {
                                customField.setId(this.customField.getId());
                            }
                            if(this.validator.checkDuplicatedEntry(customField.getTitle(), customField.getId(), this.lvCustomFields.getAdapter().getList())) {
                                MainActivity.GLOBALS.getDatabase().insertOrUpdateCustomField(customField);
                                this.changeMode(false, false);
                                this.customField = null;
                                this.setObject(new CustomField());
                                this.reload();
                            }
                        }
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                    }
                    break;
            }
            return true;
        });

        this.changeMode(false, false);
        this.reload();
        return root;
    }

    private void reload() {
        try {
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    this.search = "title like '%" + this.search + "%'";
                }
            } else {
                this.search = "";
            }

            this.lvCustomFields.getAdapter().clear();
            for(CustomField customField : MainActivity.GLOBALS.getDatabase().getCustomFields("")) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(customField.getTitle());
                baseDescriptionObject.setDescription(customField.getDescription());
                baseDescriptionObject.setObject(customField);
                baseDescriptionObject.setId(customField.getId());
                this.lvCustomFields.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    private void setObject(CustomField customField) {
        this.txtCustomFieldTitle.setText(customField.getTitle());
        this.txtCustomFieldDescription.setText(customField.getDescription());
        this.txtCustomFieldAllowed.setText(customField.getAllowedValues());
        this.chkCustomFieldAlbums.setChecked(customField.isAlbums());
        this.chkCustomFieldMovies.setChecked(customField.isMovies());
        this.chkCustomFieldGames.setChecked(customField.isGames());
        this.chkCustomFieldBooks.setChecked(customField.isBooks());
        for(int i = 0; i<=this.spCustomFieldType.getChildCount()-1; i++) {
            String item = this.spCustomFieldType.getItemAtPosition(i).toString();
            if(item.equals(customField.getType())) {
                this.spCustomFieldType.setSelection(i);
                break;
            }
        }
    }

    private CustomField getObject() {
        CustomField customField = new CustomField();
        customField.setTitle(this.txtCustomFieldTitle.getText().toString());
        customField.setDescription(this.txtCustomFieldDescription.getText().toString());
        customField.setType(this.spCustomFieldType.getSelectedItem().toString());
        customField.setAllowedValues(this.txtCustomFieldAllowed.getText().toString());
        customField.setAlbums(this.chkCustomFieldAlbums.isChecked());
        customField.setBooks(this.chkCustomFieldBooks.isChecked());
        customField.setGames(this.chkCustomFieldGames.isChecked());
        customField.setMovies(this.chkCustomFieldMovies.isChecked());
        return customField;
    }

    @Override
    public void reload(String search, boolean reload) {
        this.search = search;

        if(reload) {
            this.reload();
        }
    }

    @Override
    public void select() {

    }

    private void initValidator() {
        this.validator = new Validator(this.getActivity(), R.mipmap.ic_launcher_round);
        this.validator.addEmptyValidator(this.txtCustomFieldTitle);
    }

    private void initControls(View view) {
        this.scrollView = view.findViewById(R.id.scrollView);
        this.lvCustomFields = view.findViewById(R.id.lvCustomFields);

        this.txtCustomFieldTitle = view.findViewById(R.id.txtCustomFieldTitle);
        this.txtCustomFieldDescription = view.findViewById(R.id.txtCustomFieldDescriptions);
        this.txtCustomFieldAllowed = view.findViewById(R.id.txtCustomFieldAllowedValues);
        this.spCustomFieldType = view.findViewById(R.id.spCustomFieldType);
        this.chkCustomFieldAlbums = view.findViewById(R.id.chkCustomFieldAlbums);
        this.chkCustomFieldMovies = view.findViewById(R.id.chkCustomFieldMovies);
        this.chkCustomFieldBooks = view.findViewById(R.id.chkCustomFieldBooks);
        this.chkCustomFieldGames = view.findViewById(R.id.chkCustomFieldGames);

        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        this.initValidator();
        this.changeMode(false, false);
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvCustomFields, 7);
        ControlsHelper.changeScreenIfEditMode(mp, this.scrollView, Objects.requireNonNull(this.getActivity()), editMode);

        this.txtCustomFieldTitle.setEnabled(editMode);
        this.txtCustomFieldDescription.setEnabled(editMode);
        this.txtCustomFieldAllowed.setEnabled(editMode);
        this.spCustomFieldType.setEnabled(editMode);
        this.chkCustomFieldAlbums.setEnabled(editMode);
        this.chkCustomFieldGames.setEnabled(editMode);
        this.chkCustomFieldBooks.setEnabled(editMode);
        this.chkCustomFieldMovies.setEnabled(editMode);
    }
}
