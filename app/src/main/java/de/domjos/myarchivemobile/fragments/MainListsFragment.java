package de.domjos.myarchivemobile.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MainListsFragment extends ParentFragment {
    private EditText txtListTitle, txtListDescription, txtListDeadline;

    private SwipeRefreshDeleteList lvMediaLists, lvMediaObjects;
    private BottomNavigationView bottomNavigationView;
    private String search;

    private MediaList mediaList = null;
    private Validator validator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_lists, container, false);
        this.initControls(root);

        this.lvMediaLists.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                MainListsFragment.this.reload();
            }
        });

        this.lvMediaLists.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                mediaList = (MediaList) listObject.getObject();
                setObject(mediaList);
                changeMode(false, true);
            }
        });

        this.lvMediaLists.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                mediaList = (MediaList) listObject.getObject();
                MainActivity.GLOBALS.getDatabase().deleteItem(mediaList);
                mediaList = null;
                setObject(new MediaList());
                changeMode(false, false);
                reload();
            }
        });

        this.lvMediaObjects.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                MainListsFragment.this.reloadMediaObjects();
            }
        });

        this.lvMediaObjects.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                if(mediaList!=null) {
                    for(int i = 0; i<=mediaList.getBaseMediaObjects().size()-1; i++) {
                        if(((BaseMediaObject) listObject.getObject()).getId()==mediaList.getBaseMediaObjects().get(i).getId()) {
                            mediaList.getBaseMediaObjects().remove(i);
                            break;
                        }
                    }
                }
            }
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    setObject(new MediaList());
                    this.mediaList = null;
                    break;
                case R.id.cmdEdit:
                    if(mediaList != null) {
                        this.changeMode(true, true);
                        setObject(mediaList);
                    }
                    break;
                case R.id.cmdCancel:
                    changeMode(false, false);
                    setObject(new MediaList());
                    this.mediaList = null;
                    break;
                case R.id.cmdSave:
                    try {
                        if(this.validator.getState()) {
                            MediaList mediaList = this.getObject();
                            if(this.validator.checkDuplicatedEntry(mediaList.getTitle(), this.lvMediaLists.getAdapter().getList())) {
                                if(this.mediaList != null) {
                                    mediaList.setId(this.mediaList.getId());
                                }
                                MainActivity.GLOBALS.getDatabase().insertOrUpdateMediaList(mediaList);

                                this.changeMode(false, false);
                                this.mediaList = null;
                                this.setObject(new MediaList());
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

            this.lvMediaLists.getAdapter().clear();
            for(MediaList mediaList : MainActivity.GLOBALS.getDatabase().getMediaLists("")) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(mediaList.getTitle());
                baseDescriptionObject.setDescription(mediaList.getDescription());
                baseDescriptionObject.setObject(mediaList);
                this.lvMediaLists.getAdapter().add(baseDescriptionObject);
            }
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
            this.txtListDeadline.setText(Converter.convertDateToString(mediaList.getDeadLine(), this.getString(R.string.sys_date_format)));
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
            mediaList.setDeadLine(Converter.convertStringToDate(this.txtListDeadline.getText().toString(), this.getString(R.string.sys_date_format)));
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
                    List<BaseDescriptionObject> descriptionObjectList = ControlsHelper.getAllMediaItems(this.getActivity(), "");
                    boolean[] checkedItems = new boolean[descriptionObjectList.size()];
                    Map<String, BaseDescriptionObject> arrayList = new LinkedHashMap<>();
                    int i = 0;
                    for (BaseDescriptionObject baseDescriptionObject : descriptionObjectList) {
                        arrayList.put(String.format("%s: %s", baseDescriptionObject.getDescription(), baseDescriptionObject.getTitle()), baseDescriptionObject);
                        checkedItems[i] = false;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
                    builder.setTitle(R.string.lists_dialog_title);
                    builder.setMultiChoiceItems(arrayList.keySet().toArray(new CharSequence[]{}), checkedItems, (dialogInterface, i1, b) -> checkedItems[i1] = b);
                    builder.setPositiveButton(R.string.sys_add, (dialogInterface, i12) -> {
                        for (int j = 0; j <= checkedItems.length - 1; j++) {
                            if (checkedItems[j]) {
                                mediaList.getBaseMediaObjects().add((BaseMediaObject) Objects.requireNonNull(arrayList.get(arrayList.keySet().toArray(new String[]{})[j])).getObject());
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
        Objects.requireNonNull(this.getActivity()).getMenuInflater().inflate(R.menu.list_menu, menu);
    }

    @Override
    public void select() {

    }

    private void initValidator() {
        this.validator = new Validator(this.getActivity(), R.mipmap.ic_launcher_round);
        this.validator.addEmptyValidator(this.txtListTitle);
    }

    private void initControls(View view) {
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
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);

        this.txtListTitle.setEnabled(editMode);
        this.txtListDeadline.setEnabled(editMode);
        this.txtListDescription.setEnabled(editMode);
    }
}
