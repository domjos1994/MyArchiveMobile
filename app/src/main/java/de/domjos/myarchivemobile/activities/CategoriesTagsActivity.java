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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivelibrary.model.base.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public final class CategoriesTagsActivity extends AbstractActivity {
    private SwipeRefreshDeleteList lvItems, lvMedia;
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

        this.lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            Intent intent = new Intent();
            intent.putExtra("type", listObject.getDescription());
            intent.putExtra("id", ((BaseMediaObject) listObject.getObject()).getId());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.setObject(new BaseDescriptionObject());
                    this.baseDescriptionObject = null;
                    break;
                case R.id.cmdEdit:
                    this.changeMode(true, true);
                    this.setObject(baseDescriptionObject);
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    this.setObject(new BaseDescriptionObject());
                    this.baseDescriptionObject = null;
                    break;
                case R.id.cmdSave:
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
                    break;
            }
            return true;
        });
    }

    @Override
    protected void initControls() {
        this.lvItems = this.findViewById(R.id.lvCategoriesOrTags);
        this.lvMedia = this.findViewById(R.id.lvMedia);

        this.spItems = this.findViewById(R.id.spItems);
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_spinner_item);
        itemsAdapter.add(this.getString(R.string.media_general_tags));
        itemsAdapter.add(this.getString(R.string.media_general_category));
        this.spItems.setAdapter(itemsAdapter);
        itemsAdapter.notifyDataSetChanged();
        this.spItems.setSelection(0);

        this.txtTitle = this.findViewById(R.id.txtTitle);
        this.txtDescription = this.findViewById(R.id.txtDescription);
        this.bottomNavigationView = this.findViewById(R.id.navigationView);

        this.changeMode(false, false);
    }

    @Override
    protected void initValidator() {
        this.validator = new Validator(CategoriesTagsActivity.this, R.mipmap.ic_launcher_round);
        this.validator.addEmptyValidator(this.txtTitle);
    }

    @Override
    protected void reload() {
        if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_tags))) {
            table = this.getString(R.string.media_general_tags).toLowerCase();
        } else {
            table = "categories";
        }
        String empty = this.getString(R.string.sys_empty);
        Database database = MainActivity.GLOBALS.getDatabase();

        this.lvItems.getAdapter().clear();
        if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_tags))) {
            for (BaseDescriptionObject baseDescriptionObject : database.getBaseObjects(table, "", 0, "")) {
                de.domjos.customwidgets.model.objects.BaseDescriptionObject current = new de.domjos.customwidgets.model.objects.BaseDescriptionObject();
                String title = baseDescriptionObject.getTitle();
                current.setTitle(database.getObjects(table, baseDescriptionObject.getId()).isEmpty() ? title + empty : title);
                current.setDescription(baseDescriptionObject.getDescription());
                current.setId(baseDescriptionObject.getId());
                current.setObject(baseDescriptionObject);
                this.lvItems.getAdapter().add(current);
            }
        }
        if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_category))) {
            for (BaseDescriptionObject baseDescriptionObject : database.getBaseObjects("categories", "", 0, "")) {
                de.domjos.customwidgets.model.objects.BaseDescriptionObject current = new de.domjos.customwidgets.model.objects.BaseDescriptionObject();
                String title = baseDescriptionObject.getTitle();
                current.setTitle(database.getObjects(table, baseDescriptionObject.getId()).isEmpty() ? title + empty : title);
                current.setDescription(baseDescriptionObject.getDescription());
                current.setId(baseDescriptionObject.getId());
                current.setObject(baseDescriptionObject);
                this.lvItems.getAdapter().add(current);
            }
        }
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
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

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
                List<BaseMediaObject> mediaObjectList = MainActivity.GLOBALS.getDatabase().getObjects(table, baseDescriptionObject.getId());
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
