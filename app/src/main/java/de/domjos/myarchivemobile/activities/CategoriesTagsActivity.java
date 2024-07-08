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

package de.domjos.myarchivemobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.myarchivelibrary.custom.AbstractTask;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public final class CategoriesTagsActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvItems, lvMedia;
    private SearchView searchView;
    private Spinner spItems;
    private EditText txtTitle, txtDescription;
    private BottomNavigationView bottomNavigationView;

    private BaseDescriptionObject baseDescriptionObject;
    private String table = "tags";
    private Validator validator;

    public CategoriesTagsActivity() {
        super(R.layout.categories_tags_activity);
    }

    @Override
    protected void initActions() {
        this.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                reload(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        this.spItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reload();
                lvMedia.getAdapter().clear();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        this.lvItems.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.baseDescriptionObject = (BaseDescriptionObject) listObject.getObject();
            this.setObject(this.baseDescriptionObject);
            this.changeMode(false, true);
        });

        this.lvItems.setOnReloadListener(CategoriesTagsActivity.this::reload);

        this.lvItems.setOnDeleteListener(listObject -> MainActivity.GLOBALS.getDatabase().deleteItem((BaseDescriptionObject) listObject.getObject(), this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_tags)) ? "tags" : "categories"));

        this.lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            Intent intent = new Intent();
            intent.putExtra("type", listObject.getDescription());
            intent.putExtra("id", ((BaseMediaObject) listObject.getObject()).getId());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        this.bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if(menuItem.getItemId() == R.id.cmdAdd) {
                this.changeMode(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_add)), false);
                this.setObject(new BaseDescriptionObject());
                this.baseDescriptionObject = null;
            } else if(menuItem.getItemId() == R.id.cmdEdit) {
                if(Objects.equals(menuItem.getTitle(), this.getString(R.string.sys_edit))) {
                    this.changeMode(true, true);
                    this.setObject(baseDescriptionObject);
                } else {
                    if(this.validator.getState()) {
                        BaseDescriptionObject baseDescriptionObject = this.getObject();
                        if(this.baseDescriptionObject != null) {
                            baseDescriptionObject.setId(this.baseDescriptionObject.getId());
                        }
                        if(this.validator.checkDuplicatedEntry(baseDescriptionObject.getTitle(), baseDescriptionObject.getId(), this.lvItems.getAdapter().getList())) {
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateBaseObject(baseDescriptionObject, this.table, "", 0);
                            this.changeMode(false, false);
                            this.setObject(new BaseDescriptionObject());
                            this.baseDescriptionObject = null;
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, CategoriesTagsActivity.this);
                    }
                }
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        ControlsHelper.addToolbar(this);

        this.lvItems = this.findViewById(R.id.lvCategoriesOrTags);
        this.lvMedia = this.findViewById(R.id.lvMedia);
        this.searchView = this.findViewById(R.id.cmdSearch);

        this.spItems = this.findViewById(R.id.spItems);
        CustomSpinnerAdapter<String> itemsAdapter = new CustomSpinnerAdapter<String>(this.getApplicationContext()) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                TextView tv = (TextView) super.getView(position, convertView, parent);
                tv.setTextColor(getResources().getColor(R.color.textColorPrimary, getTheme()));
                return tv;
            }

            @Override
            public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent){
                TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
                tv.setTextColor(getResources().getColor(R.color.textColorPrimary, getTheme()));
                return tv;
            }
        };
        itemsAdapter.add(this.getString(R.string.media_general_tags));
        itemsAdapter.add(this.getString(R.string.media_general_category));
        this.spItems.setAdapter(itemsAdapter);
        itemsAdapter.notifyDataSetChanged();
        this.spItems.setSelection(0);

        this.txtTitle = this.findViewById(R.id.txtTitle);
        this.txtDescription = this.findViewById(R.id.txtDescription);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdNext).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdPrevious).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);

        this.changeMode(false, false);
        ControlsHelper.checkNetwork(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings().isDebugMode());
        menu.findItem(R.id.menMainScanner).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int requestCode = 0;
        Intent intent = null;
        if(item.getItemId() == R.id.menMainPersons) {
            intent = new Intent(this, PersonActivity.class);
            requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
        } else if(item.getItemId() == R.id.menMainCompanies) {
            intent = new Intent(this, CompanyActivity.class);
            requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
        } else if(item.getItemId() == R.id.menMainCategoriesAndTags) {
            intent = new Intent(this, CategoriesTagsActivity.class);
            requestCode = MainActivity.PER_COMP_TAG_CAT_REQUEST;
        } else if(item.getItemId() == R.id.menMainSettings) {
            intent = new Intent(this, SettingsActivity.class);
            requestCode = MainActivity.SETTINGS_REQUEST;
        } else if(item.getItemId() == R.id.menMainLog) {
            intent = new Intent(this, LogActivity.class);
        }
        if(intent != null) {
            this.startActivityForResult(intent, requestCode);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(CategoriesTagsActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addEmptyValidator(this.txtTitle);
    }

    @Override
    protected void reload() {
        this.reload("");
    }

    private void reload(String search) {
        if(!search.trim().isEmpty()) {
            search = "title like '%" + search + "%'";
        }

        if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_tags))) {
            table = this.getString(R.string.media_general_tags).toLowerCase();
        } else {
            table = "categories";
        }
        String empty = this.getString(R.string.sys_empty);
        Database database = MainActivity.GLOBALS.getDatabase();

        this.lvItems.getAdapter().clear();
        LoadingTask<BaseDescriptionObject> task = new LoadingTask<>(this, new BaseDescriptionObject(), null, search, this.lvItems, table);
        task.after((AbstractTask.PostExecuteListener<List<BaseDescriptionObject>>) o -> {
            if(o != null) {
                for(BaseDescriptionObject baseDescriptionObject : o) {
                    de.domjos.customwidgets.model.BaseDescriptionObject current = new de.domjos.customwidgets.model.BaseDescriptionObject();
                    String title = baseDescriptionObject.getTitle();
                    current.setTitle(database.getObjects(table, baseDescriptionObject.getId(), MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("tags")).isEmpty() ? title + empty : title);
                    current.setDescription(baseDescriptionObject.getDescription());
                    current.setId(baseDescriptionObject.getId());
                    current.setObject(baseDescriptionObject);
                    this.lvItems.getAdapter().add(current);
                }
            }
        });
        task.execute();
    }

    private void setObject(BaseDescriptionObject baseDescriptionObject) {
        this.txtTitle.setText(baseDescriptionObject.getTitle());
        this.txtDescription.setText(baseDescriptionObject.getDescription());
        this.reloadMedia();
    }

    private BaseDescriptionObject getObject() {
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setTitle(this.txtTitle.getText().toString());
        baseDescriptionObject.setDescription(this.txtDescription.getText().toString());
        return baseDescriptionObject;
    }

    private void changeMode(boolean editMode, boolean selected) {
        if(this.validator != null) {
            this.validator.clear();
        }
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);

        this.txtTitle.setEnabled(editMode);
        this.txtDescription.setEnabled(editMode);
    }

    private void reloadMedia() {
        try {
            this.lvMedia.getAdapter().clear();
            if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_tags))) {
                table = this.getString(R.string.media_general_tags).toLowerCase();
            } else {
                table = "categories";
            }
            if(baseDescriptionObject!=null) {
                List<BaseMediaObject> mediaObjectList = MainActivity.GLOBALS.getDatabase().getObjects(table, baseDescriptionObject.getId(), MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("tags"));
                if(!mediaObjectList.isEmpty()) {
                    for(BaseMediaObject baseMediaObject : mediaObjectList) {
                        this.lvMedia.getAdapter().add(ControlsHelper.convertMediaToDescriptionObject(baseMediaObject, this.getApplicationContext()));
                    }
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, CategoriesTagsActivity.this);
        }
    }
}
