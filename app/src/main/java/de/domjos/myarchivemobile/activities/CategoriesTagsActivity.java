package de.domjos.myarchivemobile.activities;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
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

    public CategoriesTagsActivity() {
        super(R.layout.categories_tags_activity);
    }

    @Override
    protected void initActions() {
        this.spItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                reload();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        this.lvItems.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(de.domjos.customwidgets.model.objects.BaseDescriptionObject listObject) {
                baseDescriptionObject = (BaseDescriptionObject) listObject.getObject();
                setObject(baseDescriptionObject);
                changeMode(false, true);
            }
        });

        this.lvItems.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                CategoriesTagsActivity.this.reload();
            }
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
                    BaseDescriptionObject baseDescriptionObject = this.getObject();
                    if(this.baseDescriptionObject != null) {
                        baseDescriptionObject.setId(this.baseDescriptionObject.getId());
                    }
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateBaseObject(baseDescriptionObject, this.table, "", 0);
                    this.changeMode(false, false);
                    this.setObject(new BaseDescriptionObject());
                    this.baseDescriptionObject = null;
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
    protected void reload() {
        this.lvItems.getAdapter().clear();
        if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_tags))) {
            for (BaseDescriptionObject baseDescriptionObject : MainActivity.GLOBALS.getDatabase().getBaseObjects("tags", "", 0, "")) {
                de.domjos.customwidgets.model.objects.BaseDescriptionObject current = new de.domjos.customwidgets.model.objects.BaseDescriptionObject();
                current.setTitle(baseDescriptionObject.getTitle());
                current.setDescription(baseDescriptionObject.getDescription());
                current.setObject(baseDescriptionObject);
                this.lvItems.getAdapter().add(current);
            }
        }
        if(this.spItems.getSelectedItem().toString().equals(this.getString(R.string.media_general_category))) {
            for (BaseDescriptionObject baseDescriptionObject : MainActivity.GLOBALS.getDatabase().getBaseObjects("categories", "", 0, "")) {
                de.domjos.customwidgets.model.objects.BaseDescriptionObject current = new de.domjos.customwidgets.model.objects.BaseDescriptionObject();
                current.setTitle(baseDescriptionObject.getTitle());
                current.setDescription(baseDescriptionObject.getDescription());
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
                table = "tags";
            } else {
                table = "categories";
            }
            if(baseDescriptionObject!=null) {
                for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getObjects(table, baseDescriptionObject.getId())) {
                    this.lvMedia.getAdapter().add(ControlsHelper.convertMediaToDescriptionObject(baseMediaObject, this.getApplicationContext()));
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, CategoriesTagsActivity.this);
        }
    }
}
