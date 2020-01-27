package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MainLibraryFragment extends ParentFragment {
    private EditText txtLibraryNumberOfDays, txtMediaLibraryNumberOfWeeks;
    private EditText txtLibraryDeadline, txtLibraryReturnedAt;
    private AutoCompleteTextView txtLibraryPerson;

    private SwipeRefreshDeleteList lvMediaLibrary, lvMediaHistory;
    private BottomNavigationView bottomNavigationView;
    private String search;

    private BaseDescriptionObject currentObject = null;
    private LibraryObject libraryObject = null;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_library, container, false);
        this.initControls(root);

        this.lvMediaLibrary.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                MainLibraryFragment.this.reload();
            }
        });

        this.lvMediaLibrary.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                currentObject = listObject;
                bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(true);
                reloadLibraryObjects();
            }
        });

        this.lvMediaHistory.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                MainLibraryFragment.this.reloadLibraryObjects();
            }
        });

        this.lvMediaHistory.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                libraryObject = (LibraryObject) listObject.getObject();
                changeMode(false, true);
                setObject(libraryObject);
            }
        });

        this.lvMediaHistory.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                long id = ((LibraryObject) listObject.getObject()).getId();
                MainActivity.GLOBALS.getDatabase().deleteItem((LibraryObject) listObject.getObject());
                BaseMediaObject baseMediaObject = (BaseMediaObject) currentObject.getObject();
                for(int i = 0; i<=baseMediaObject.getLibraryObjects().size()-1; i++) {
                    if(baseMediaObject.getLibraryObjects().get(i).getId()==id) {
                        baseMediaObject.getLibraryObjects().remove(i);
                        break;
                    }
                }
                changeMode(false, false);
                setObject(new LibraryObject());
                libraryObject = null;
            }
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    setObject(new LibraryObject());
                    this.libraryObject = null;
                    break;
                case R.id.cmdEdit:
                    if(libraryObject != null) {
                        this.changeMode(true, true);
                        setObject(libraryObject);
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
                        LibraryObject libraryObject = this.getObject();
                        if(this.libraryObject != null) {
                            libraryObject.setId(this.libraryObject.getId());
                        }
                        MainActivity.GLOBALS.getDatabase().insertOrUpdateLibraryObject(libraryObject, (BaseMediaObject) currentObject.getObject());

                        if(libraryObject.getId() != 0) {
                            BaseMediaObject baseMediaObject = (BaseMediaObject) currentObject.getObject();
                            for(int i = 0; i<=baseMediaObject.getLibraryObjects().size()-1; i++) {
                                if(baseMediaObject.getLibraryObjects().get(i).getId()==libraryObject.getId()) {
                                    baseMediaObject.getLibraryObjects().set(i, libraryObject);
                                    break;
                                }
                            }
                        } else {
                            ((BaseMediaObject) currentObject.getObject()).getLibraryObjects().add(libraryObject);
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
        return root;
    }

    private void reload() {
        try {
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    this.search = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                this.search = "";
            }

            this.lvMediaLibrary.getAdapter().clear();
            for(BaseDescriptionObject baseDescriptionObject : ControlsHelper.getAllMediaItems(this.getActivity(), this.search)) {
                baseDescriptionObject.setTitle(baseDescriptionObject.getTitle());
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
            this.txtLibraryDeadline.setText(Converter.convertDateToString(libraryObject.getDeadLine(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtLibraryDeadline.setText("");
        }
        if(libraryObject.getReturned() != null) {
            this.txtLibraryReturnedAt.setText(Converter.convertDateToString(libraryObject.getReturned(), this.getString(R.string.sys_date_format)));
        } else {
            this.txtLibraryReturnedAt.setText("");
        }
    }

    private LibraryObject getObject() throws Exception {
        LibraryObject libraryObject = new LibraryObject();
        libraryObject.setNumberOfDays(Integer.parseInt(this.txtLibraryNumberOfDays.getText().toString()));
        libraryObject.setNumberOfWeeks(Integer.parseInt(this.txtMediaLibraryNumberOfWeeks.getText().toString()));
        if(!this.txtLibraryPerson.getText().toString().isEmpty()) {
            Person person = new Person();
            String[] spl = this.txtLibraryPerson.getText().toString().split(" ");
            person.setFirstName(spl[0]);
            person.setLastName(this.txtLibraryPerson.getText().toString().replace(spl[0], "").trim());
            libraryObject.setPerson(person);
        }
        if(!this.txtLibraryDeadline.getText().toString().isEmpty()) {
            libraryObject.setDeadLine(Converter.convertStringToDate(this.txtLibraryDeadline.getText().toString(), this.getString(R.string.sys_date_format)));
        }
        if(!this.txtLibraryReturnedAt.getText().toString().isEmpty()) {
            libraryObject.setReturned(Converter.convertStringToDate(this.txtLibraryReturnedAt.getText().toString(), this.getString(R.string.sys_date_format)));
        }
        return libraryObject;
    }

    private void reloadLibraryObjects() {
        this.lvMediaHistory.getAdapter().clear();
        if(this.currentObject != null) {
            BaseMediaObject baseMediaObject = (BaseMediaObject) this.currentObject.getObject();
            for(LibraryObject libraryObject : baseMediaObject.getLibraryObjects()) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(String.format("%s %s", libraryObject.getPerson().getFirstName(), libraryObject.getPerson().getLastName()).trim());
                String description = "";
                if(libraryObject.getDeadLine() != null) {
                    description = Converter.convertDateToString(libraryObject.getDeadLine(), this.getString(R.string.sys_date_format));
                }
                if(libraryObject.getReturned() != null) {
                    description += " - " + Converter.convertDateToString(libraryObject.getReturned(), this.getString(R.string.sys_date_format));
                }
                baseDescriptionObject.setDescription(description);
                baseDescriptionObject.setObject(libraryObject);
                this.lvMediaHistory.getAdapter().add(baseDescriptionObject);
            }
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

    }

    private void initControls(View view) {
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
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), android.R.layout.simple_expandable_list_item_1);
            for(Person person : MainActivity.GLOBALS.getDatabase().getPersons("", 0)) {
                arrayAdapter.add(String.format("%s %s", person.getFirstName(), person.getLastName()).trim());
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

        this.txtLibraryPerson.setEnabled(editMode);
        this.txtLibraryNumberOfDays.setEnabled(editMode);
        this.txtMediaLibraryNumberOfWeeks.setEnabled(editMode);
        this.txtLibraryReturnedAt.setEnabled(editMode);
        this.txtLibraryDeadline.setEnabled(editMode);
    }
}
