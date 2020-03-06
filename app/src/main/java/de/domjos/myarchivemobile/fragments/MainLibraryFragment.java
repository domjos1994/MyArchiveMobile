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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomAutoCompleteAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MainLibraryFragment extends ParentFragment {
    private EditText txtLibraryNumberOfDays, txtMediaLibraryNumberOfWeeks;
    private EditText txtLibraryDeadline, txtLibraryReturnedAt;
    private AutoCompleteTextView txtLibraryPerson;
    private CustomAutoCompleteAdapter<Person> arrayAdapter;
    private ScrollView scrollView;

    private SwipeRefreshDeleteList lvMediaLibrary, lvMediaHistory;
    private BottomNavigationView bottomNavigationView;
    private String search;

    private BaseDescriptionObject currentObject = null;
    private LibraryObject libraryObject = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_library, container, false);
        this.initControls(root);

        this.lvMediaLibrary.setOnReloadListener(MainLibraryFragment.this::reload);

        this.lvMediaLibrary.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.currentObject = listObject;
            this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(true);
            this.reloadLibraryObjects();
        });

        this.lvMediaHistory.setOnReloadListener(MainLibraryFragment.this::reloadLibraryObjects);

        this.lvMediaHistory.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.libraryObject = (LibraryObject) listObject.getObject();
            this.changeMode(false, true);
            this.setObject(this.libraryObject);
        });

        this.lvMediaHistory.setOnDeleteListener(listObject -> {
            long id = ((LibraryObject) listObject.getObject()).getId();
            MainActivity.GLOBALS.getDatabase().deleteItem((LibraryObject) listObject.getObject());
            BaseMediaObject baseMediaObject = (BaseMediaObject) this.currentObject.getObject();
            for(int i = 0; i<=baseMediaObject.getLibraryObjects().size()-1; i++) {
                if(baseMediaObject.getLibraryObjects().get(i).getId()==id) {
                    baseMediaObject.getLibraryObjects().remove(i);
                    break;
                }
            }
            this.changeMode(false, false);
            this.setObject(new LibraryObject());
            this.libraryObject = null;
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            LibraryObject libraryObject;
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    libraryObject = new LibraryObject();
                    libraryObject.setDeadLine(new Date());
                    setObject(libraryObject);
                    this.libraryObject = null;
                    break;
                case R.id.cmdEdit:
                    if(this.libraryObject != null) {
                        this.changeMode(true, true);
                        setObject(this.libraryObject);
                    }
                    break;
                case R.id.cmdCancel:
                    changeMode(false, false);
                    setObject(new LibraryObject());
                    this.libraryObject = null;
                    reloadLibraryObjects();
                    break;
                case R.id.cmdSave:
                    try {
                        libraryObject = this.getObject();
                        if(this.libraryObject != null) {
                            libraryObject.setId(this.libraryObject.getId());
                        }
                        MainActivity.GLOBALS.getDatabase().insertOrUpdateLibraryObject(libraryObject, (BaseMediaObject) currentObject.getObject());

                        if(this.libraryObject != null) {
                            if(this.libraryObject.getId() != 0) {
                                BaseMediaObject baseMediaObject = (BaseMediaObject) currentObject.getObject();
                                for(int i = 0; i<=baseMediaObject.getLibraryObjects().size()-1; i++) {
                                    if(baseMediaObject.getLibraryObjects().get(i).getId()==this.libraryObject.getId()) {
                                        baseMediaObject.getLibraryObjects().set(i, this.libraryObject);
                                        break;
                                    }
                                }
                            } else {
                                ((BaseMediaObject) currentObject.getObject()).getLibraryObjects().add(this.libraryObject);
                            }
                        } else {
                            ((BaseMediaObject) currentObject.getObject()).getLibraryObjects().add(this.libraryObject);
                        }

                        this.changeMode(false, false);
                        this.libraryObject = null;
                        this.reloadLibraryObjects();
                        this.setObject(new LibraryObject());
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                    }
                    break;
            }
            return true;
        });

        this.changeMode(false, false);
        this.reload();
        this.select();
        return root;
    }

    private void reload() {
        try {
            String searchString = "";
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    searchString = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                searchString = "";
            }

            this.lvMediaLibrary.getAdapter().clear();
            for(BaseDescriptionObject baseDescriptionObject : ControlsHelper.getAllMediaItems(this.getActivity(), searchString)) {
                baseDescriptionObject.setTitle(baseDescriptionObject.getTitle());
                baseDescriptionObject.setDescription(baseDescriptionObject.getDescription());
                baseDescriptionObject.setId(baseDescriptionObject.getId());
                this.lvMediaLibrary.getAdapter().add(baseDescriptionObject);
            }
            this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(false);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    private void setObject(LibraryObject libraryObject) {
        this.txtLibraryNumberOfDays.setText(String.valueOf(libraryObject.getNumberOfDays()));
        this.txtMediaLibraryNumberOfWeeks.setText(String.valueOf(libraryObject.getNumberOfWeeks()));
        if(libraryObject.getPerson() != null) {
            this.txtLibraryPerson.setText(String.format("%s %s", libraryObject.getPerson().getFirstName(), libraryObject.getPerson().getLastName()).trim());
        } else {
            this.txtLibraryPerson.setText("");
        }
        if(libraryObject.getDeadLine() != null) {
            this.txtLibraryDeadline.setText(ConvertHelper.convertDateToString(libraryObject.getDeadLine(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtLibraryDeadline.setText("");
        }
        if(libraryObject.getReturned() != null) {
            this.txtLibraryReturnedAt.setText(ConvertHelper.convertDateToString(libraryObject.getReturned(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtLibraryReturnedAt.setText("");
        }
    }

    private LibraryObject getObject() throws ParseException {
        LibraryObject libraryObject = new LibraryObject();
        libraryObject.setNumberOfDays(Integer.parseInt(this.txtLibraryNumberOfDays.getText().toString()));
        libraryObject.setNumberOfWeeks(Integer.parseInt(this.txtMediaLibraryNumberOfWeeks.getText().toString()));
        if(!this.txtLibraryPerson.getText().toString().isEmpty()) {
            for(int i = 0; i<=this.arrayAdapter.getCount() - 1; i++) {
                if(this.txtLibraryPerson.getText().toString().trim().equals(Objects.requireNonNull(this.arrayAdapter.getItem(i)).toString().trim())) {
                    libraryObject.setPerson(this.arrayAdapter.getItem(i));
                    break;
                }
            }
        }
        if(!this.txtLibraryDeadline.getText().toString().isEmpty()) {
            libraryObject.setDeadLine(ConvertHelper.convertStringToDate(this.txtLibraryDeadline.getText().toString(), this.getString(R.string.sys_date_format)));
        }
        if(!this.txtLibraryReturnedAt.getText().toString().isEmpty()) {
            libraryObject.setReturned(ConvertHelper.convertStringToDate(this.txtLibraryReturnedAt.getText().toString(), this.getString(R.string.sys_date_format)));
        }
        return libraryObject;
    }

    private void reloadLibraryObjects() {
        try {
            this.lvMediaHistory.getAdapter().clear();
            if(this.currentObject != null) {
                DatabaseObject databaseObject = (DatabaseObject) this.currentObject.getObject();
                for(LibraryObject libraryObject : MainActivity.GLOBALS.getDatabase().getLibraryObjects("type='" + databaseObject.getTable() + "' AND media=" + databaseObject.getId())) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(String.format("%s %s", libraryObject.getPerson().getFirstName(), libraryObject.getPerson().getLastName()).trim());
                    String description = "";
                    if(libraryObject.getDeadLine() != null) {
                        description = ConvertHelper.convertDateToString(libraryObject.getDeadLine(), this.getString(R.string.sys_date_format));
                    }
                    if(libraryObject.getReturned() != null) {
                        description += " - " + ConvertHelper.convertDateToString(libraryObject.getReturned(), this.getString(R.string.sys_date_format));
                    }
                    baseDescriptionObject.setDescription(description);
                    baseDescriptionObject.setObject(libraryObject);
                    baseDescriptionObject.setId(libraryObject.getId());
                    this.lvMediaHistory.getAdapter().add(baseDescriptionObject);
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
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
        try {
            long id = Objects.requireNonNull(this.getArguments()).getLong("id");
            if(id != 0) {
                for(int i = 0; i<=this.lvMediaLibrary.getAdapter().getItemCount() - 1; i++) {
                    this.currentObject = this.lvMediaLibrary.getAdapter().getItem(i);
                    if(this.currentObject != null) {
                        this.lvMediaLibrary.select(this.currentObject);
                        this.reloadLibraryObjects();

                        for(int j = 0; j<=this.lvMediaLibrary.getAdapter().getItemCount() - 1; j++) {
                            BaseDescriptionObject historyObject = this.lvMediaHistory.getAdapter().getItem(j);
                            if(historyObject.getId() == id) {
                                this.lvMediaHistory.select(historyObject);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private void initControls(View view) {
        this.scrollView = view.findViewById(R.id.scrollView);
        this.lvMediaLibrary = view.findViewById(R.id.lvMediaLibrary);
        this.lvMediaHistory = view.findViewById(R.id.lvMediaHistory);

        this.txtLibraryNumberOfDays = view.findViewById(R.id.txtLibraryNumberOfDays);
        this.txtMediaLibraryNumberOfWeeks = view.findViewById(R.id.txtLibraryNumberOfWeeks);
        this.txtLibraryDeadline = view.findViewById(R.id.txtLibraryDeadLine);
        this.txtLibraryReturnedAt = view.findViewById(R.id.txtLibraryReturnedAt);
        this.txtLibraryPerson = view.findViewById(R.id.txtLibraryPerson);

        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);

        try {
            this.arrayAdapter = new CustomAutoCompleteAdapter<>(Objects.requireNonNull(this.getContext()), this.txtLibraryPerson);
            for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("", 0)) {
                arrayAdapter.add(person);
            }
            this.txtLibraryPerson.setAdapter(arrayAdapter);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected && this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).isVisible());
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvMediaLibrary, 4);
        mp.put(this.lvMediaHistory, 3);
        ControlsHelper.changeScreenIfEditMode(mp, this.scrollView, Objects.requireNonNull(this.getActivity()), editMode);

        this.lvMediaHistory.setReadOnly(editMode);
        this.txtLibraryPerson.setEnabled(editMode);
        this.txtLibraryNumberOfDays.setEnabled(editMode);
        this.txtMediaLibraryNumberOfWeeks.setEnabled(editMode);
        this.txtLibraryReturnedAt.setEnabled(editMode);
        this.txtLibraryDeadline.setEnabled(editMode);
    }
}
