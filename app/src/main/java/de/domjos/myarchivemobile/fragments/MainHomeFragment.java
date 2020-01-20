package de.domjos.myarchivemobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MainHomeFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvMedia;
    private String search;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_home, container, false);
        this.lvMedia = root.findViewById(R.id.lvMedia);

        this.lvMedia.addButtonClick(R.drawable.ic_local_library_black_24dp, new SwipeRefreshDeleteList.ButtonClickListener() {
            @Override
            public void onClick(List<BaseDescriptionObject> objectList) {
                try {
                    AlertDialog.Builder b = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                    b.setTitle(getString(R.string.media_persons));
                    List<String> personsString = new LinkedList<>();
                    List<Person> people = MainActivity.GLOBALS.getDatabase().getPersons("", 0);
                    for(Person person : people) {
                        personsString.add(String.format("%s %s", person.getFirstName(), person.getLastName()).trim());
                    }
                    String[] types = personsString.toArray(new String[0]);
                    b.setItems(types, (dialogInterface, i) -> {
                        for(BaseDescriptionObject baseDescriptionObject : objectList) {
                            LibraryObject libraryObject = new LibraryObject();
                            libraryObject.setDeadLine(new Date());
                            libraryObject.setNumberOfDays(7);
                            libraryObject.setPerson(people.get(i));
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateLibraryObject(libraryObject, (BaseMediaObject) baseDescriptionObject.getObject());
                        }
                    });
                    b.show();
                } catch (Exception ex) {
                    MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getContext());
                }
            }
        });

        this.lvMedia.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                reload();
            }
        });

        this.lvMedia.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
                if(mainActivity != null) {
                    mainActivity.selectTab(listObject.getDescription(), ((BaseMediaObject) listObject.getObject()).getId());
                }
            }
        });

        this.lvMedia.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                BaseMediaObject baseMediaObject = (BaseMediaObject) listObject.getObject();
                if(baseMediaObject instanceof Book) {
                    MainActivity.GLOBALS.getDatabase().deleteItem((Book) baseMediaObject);
                }
                if(baseMediaObject instanceof Movie) {
                    MainActivity.GLOBALS.getDatabase().deleteItem((Movie) baseMediaObject);
                }
                if(baseMediaObject instanceof Game) {
                    MainActivity.GLOBALS.getDatabase().deleteItem((Game) baseMediaObject);
                }
                if(baseMediaObject instanceof Album) {
                    MainActivity.GLOBALS.getDatabase().deleteItem((Album) baseMediaObject);
                }
                reload();
            }
        });

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

            this.lvMedia.getAdapter().clear();
            for(BaseDescriptionObject baseDescriptionObject : ControlsHelper.getAllMediaItems(this.getActivity(), this.search)) {
                this.lvMedia.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String parent) {

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
}