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

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.model.tasks.AbstractTask;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public class MainListsFragment extends ParentFragment {
    private ScrollView scrollView;
    private EditText txtListTitle, txtListDescription, txtListDeadline;

    private SwipeRefreshDeleteList lvMediaLists, lvMediaObjects;
    private BottomNavigationView bottomNavigationView;
    private String search;

    private MediaList mediaList = null;
    private Validator validator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_lists, container, false);
        this.initControls(root);

        this.lvMediaLists.setOnReloadListener(MainListsFragment.this::reload);

        this.lvMediaLists.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.mediaList = (MediaList) listObject.getObject();
            this.setObject(this.mediaList);
            this.changeMode(false, true);
        });

        this.lvMediaLists.setOnDeleteListener(listObject -> {
            this.mediaList = (MediaList) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(this.mediaList);
            this.mediaList = null;
            this.setObject(new MediaList());
            this.changeMode(false, false);
            this.reload();
        });

        this.lvMediaObjects.setOnReloadListener(MainListsFragment.this::reloadMediaObjects);

        this.lvMediaObjects.setOnDeleteListener(listObject -> {
            if(this.mediaList!=null) {
                for(int i = 0; i<=this.mediaList.getBaseMediaObjects().size()-1; i++) {
                    if(((BaseMediaObject) listObject.getObject()).getId()==this.mediaList.getBaseMediaObjects().get(i).getId()) {
                        this.mediaList.getBaseMediaObjects().remove(i);
                        MainActivity.GLOBALS.getDatabase().insertOrUpdateMediaList(this.mediaList);
                        break;
                    }
                }
            }
        });

        this.lvMediaObjects.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            MainActivity mainActivity = ((MainActivity) MainListsFragment.this.getActivity());
            if(mainActivity != null) {
                mainActivity.selectTab(listObject.getDescription(), ((BaseMediaObject) listObject.getObject()).getId());
            }
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    if(menuItem.getTitle().equals(this.getString(R.string.sys_add))) {
                        this.changeMode(true, false);
                        this.setObject(new MediaList());
                        this.mediaList = null;
                    } else {
                        changeMode(false, false);
                        this.setObject(new MediaList());
                        this.mediaList = null;
                    }
                    break;
                case R.id.cmdEdit:
                    if(menuItem.getTitle().equals(this.getString(R.string.sys_edit))) {
                        if(mediaList != null) {
                            this.changeMode(true, true);
                            this.setObject(mediaList);
                        }
                    } else {
                        try {
                            if(this.validator.getState()) {
                                MediaList mediaList = this.getObject();
                                if(this.mediaList != null) {
                                    mediaList.setId(this.mediaList.getId());
                                }
                                if(this.validator.checkDuplicatedEntry(mediaList.getTitle(), mediaList.getId(), this.lvMediaLists.getAdapter().getList())) {
                                    MainActivity.GLOBALS.getDatabase().insertOrUpdateMediaList(mediaList);
                                    this.changeMode(false, false);
                                    this.setObject(new MediaList());
                                    this.mediaList = null;
                                    this.reload();
                                }
                            }
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getContext());
                        }
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

            this.lvMediaLists.getAdapter().clear();
            LoadingTask<MediaList> loadingTask = new LoadingTask<>(this.getActivity(), new MediaList(), null, "", this.lvMediaLists, Globals.LISTS);
            loadingTask.after((AbstractTask.PostExecuteListener<List<MediaList>>) mediaLists -> {
                for(MediaList mediaList : mediaLists) {
                    BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                    baseDescriptionObject.setTitle(mediaList.getTitle());
                    baseDescriptionObject.setDescription(mediaList.getDescription());
                    baseDescriptionObject.setObject(mediaList);
                    baseDescriptionObject.setId(mediaList.getId());
                    this.lvMediaLists.getAdapter().add(baseDescriptionObject);
                }
            });
            loadingTask.execute();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private void reloadMediaObjects() {
        if(mediaList != null) {
            this.lvMediaObjects.getAdapter().clear();
            for(BaseMediaObject baseMediaObject : mediaList.getBaseMediaObjects()) {
                this.lvMediaObjects.getAdapter().add(ControlsHelper.convertMediaToDescriptionObject(baseMediaObject, this.getContext()));
            }
        }
    }

    @Override
    public void setCodes(String codes, String label) {

    }

    private void setObject(MediaList mediaList) {
        this.txtListTitle.setText(mediaList.getTitle());
        if(mediaList.getDeadLine() != null) {
            this.txtListDeadline.setText(ConvertHelper.convertDateToString(mediaList.getDeadLine(), this.getString(R.string.sys_date_format)));
        } else  {
            this.txtListDeadline.setText("");
        }
        this.txtListDescription.setText(mediaList.getDescription());

        this.reloadMediaObjects();
    }

    private MediaList getObject() throws ParseException {
        MediaList mediaList = new MediaList();
        mediaList.setTitle(this.txtListTitle.getText().toString());
        if(!this.txtListDeadline.getText().toString().isEmpty()) {
            mediaList.setDeadLine(ConvertHelper.convertStringToDate(this.txtListDeadline.getText().toString(), this.getString(R.string.sys_date_format)));
        } else {
            mediaList.setDeadLine(null);
        }
        mediaList.setDescription(this.txtListDescription.getText().toString());

        for(int i = 0; i<=this.lvMediaObjects.getAdapter().getItemCount()-1; i++) {
            BaseDescriptionObject baseDescriptionObject = this.lvMediaObjects.getAdapter().getItem(i);
            if(baseDescriptionObject != null) {
                mediaList.getBaseMediaObjects().add((BaseMediaObject) baseDescriptionObject.getObject());
            }
        }

        return mediaList;
    }

    @Override
    public void reload(String search, boolean reload) {
        this.search = search;

        if(reload) {
            this.reload();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menListsMediaAdd) {
            try {
                if(mediaList != null) {
                    List<BaseDescriptionObject> descriptionObjectList = ControlsHelper.getAllMediaItems(this.getActivity(), "", Globals.LISTS);
                    boolean[] checkedItems = new boolean[descriptionObjectList.size()];
                    Map<String, BaseDescriptionObject> arrayList = new LinkedHashMap<>();
                    int i = 0;
                    for (BaseDescriptionObject baseDescriptionObject : descriptionObjectList) {
                        arrayList.put(String.format("%s: %s", baseDescriptionObject.getDescription(), baseDescriptionObject.getTitle()), baseDescriptionObject);
                        checkedItems[i] = false;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity(), R.style.appCompatAlert);
                    builder.setTitle(R.string.lists_dialog_title);
                    builder.setMultiChoiceItems(arrayList.keySet().toArray(new CharSequence[]{}), checkedItems, (dialogInterface, i1, b) -> checkedItems[i1] = b);
                    builder.setPositiveButton(R.string.sys_add, (dialogInterface, i12) -> {
                        for (int j = 0; j <= checkedItems.length - 1; j++) {
                            if (checkedItems[j]) {
                                mediaList.getBaseMediaObjects().add((BaseMediaObject) Objects.requireNonNull(arrayList.get(arrayList.keySet().toArray(new String[]{})[j])).getObject());
                                MainActivity.GLOBALS.getDatabase().insertOrUpdateMediaList(mediaList);
                            }
                        }
                    });
                    builder.show();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
            }
        }

        return true;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        this.requireActivity().getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public void select() {

    }

    private void initValidator() {
        this.validator = new Validator(this.getActivity(), R.mipmap.ic_launcher_round);
        this.validator.addEmptyValidator(this.txtListTitle);
    }

    private void initControls(View view) {
        this.scrollView = view.findViewById(R.id.scrollView);
        this.lvMediaLists = view.findViewById(R.id.lvMediaLists);
        this.lvMediaObjects = view.findViewById(R.id.lvMediaObjects);
        this.lvMediaObjects.setContextMenu(R.menu.list_menu);

        this.txtListTitle = view.findViewById(R.id.txtListsTitle);
        this.txtListDeadline = view.findViewById(R.id.txtListsDeadline);
        this.txtListDescription = view.findViewById(R.id.txtListsDescription);

        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        this.initValidator();
        this.changeMode(false, false);
    }

    @Override
    public  void changeMode(boolean editMode, boolean selected) {
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);

        Map<SwipeRefreshDeleteList, Integer> mp = new LinkedHashMap<>();
        mp.put(this.lvMediaLists, 4);
        mp.put(this.lvMediaObjects, 4);
        ControlsHelper.changeScreenIfEditMode(mp, this.scrollView, this.requireActivity(), editMode);

        this.txtListTitle.setEnabled(editMode);
        this.txtListDeadline.setEnabled(editMode);
        this.txtListDescription.setEnabled(editMode);
    }
}
