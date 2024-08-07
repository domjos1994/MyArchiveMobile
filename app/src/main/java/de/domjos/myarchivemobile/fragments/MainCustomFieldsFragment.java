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
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivelibrary.custom.AbstractTask;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.tasks.LoadingTask;

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

        this.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.cmdAdd) {
                this.changeMode(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_add)), false);
                this.setObject(new CustomField());
                this.customField = null;
            } else if(menuItem.getItemId() == R.id.cmdEdit) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_edit))) {
                    if(customField != null) {
                        this.changeMode(true, true);
                        setObject(customField);
                    }
                } else {
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
                }
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
            LoadingTask<CustomField> loadingTask = new LoadingTask<>(this.getActivity(), new CustomField(), null, this.search, this.lvCustomFields, "customFields");
            loadingTask.after((AbstractTask.PostExecuteListener<List<CustomField>>) customFields -> {
                for(CustomField customField : customFields) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(customField.getTitle());
                    baseDescriptionObject.setDescription(customField.getDescription());
                    baseDescriptionObject.setObject(customField);
                    baseDescriptionObject.setId(customField.getId());
                    this.lvCustomFields.getAdapter().add(baseDescriptionObject);
                }
            });
            loadingTask.execute((Void) null);
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

        String[] typeArray = this.getResources().getStringArray(R.array.customFields_type_values);
        this.spCustomFieldType = view.findViewById(R.id.spCustomFieldType);
        CustomSpinnerAdapter<String> adapter = new CustomSpinnerAdapter<>(this.requireContext(), typeArray);
        this.spCustomFieldType.setAdapter(adapter);

        this.chkCustomFieldAlbums = view.findViewById(R.id.chkCustomFieldAlbums);
        this.chkCustomFieldMovies = view.findViewById(R.id.chkCustomFieldMovies);
        this.chkCustomFieldBooks = view.findViewById(R.id.chkCustomFieldBooks);
        this.chkCustomFieldGames = view.findViewById(R.id.chkCustomFieldGames);

        this.bottomNavigationView = view.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdPrevious).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdNext).setVisible(false);

        this.initValidator();
        this.changeMode(false, false);
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);

        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvCustomFields, 7);
        ControlsHelper.changeScreenIfEditMode(mp, this.scrollView, this.requireActivity(), editMode);

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
