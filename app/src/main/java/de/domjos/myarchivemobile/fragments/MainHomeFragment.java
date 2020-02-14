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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class MainHomeFragment extends ParentFragment {
    private Animation fabOpen, fabClose, fabClock, fabAntiClock;
    private FloatingActionButton fabAppAdd, fabAppBooks, fabAppMusic, fabAppMovies, fabAppGames;
    private TextView lblEntriesCount;

    private TableLayout filter;
    private MediaFilter tempFilter;
    private Spinner spFilter;
    private ArrayAdapter<MediaFilter> arrayAdapter;
    private EditText txtFilterName, txtFilterSearch, txtFilterCategories, txtFilterTags;
    private ImageButton cmdFilterExpand, cmdFilterSave, cmdFilterDelete;
    private CheckBox chkFilterBooks, chkFilterMovies, chkFilterMusic, chkFilterGames;
    private TableRow rowName, rowMedia1, rowMedia2;

    private SwipeRefreshDeleteList lvMedia;
    private String search;
    private boolean isOpen = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_home, container, false);

        this.fabOpen = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fab_open);
        this.fabClose = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fab_close);
        this.fabClock = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fab_rotate_clock);
        this.fabAntiClock = AnimationUtils.loadAnimation(this.getActivity(), R.anim.fab_rotate_anticlock);

        this.fabAppAdd = root.findViewById(R.id.fabAppAdd);
        this.fabAppBooks = root.findViewById(R.id.fabAppBook);
        this.fabAppMusic = root.findViewById(R.id.fabAppMusic);
        this.fabAppMovies = root.findViewById(R.id.fabAppMovies);
        this.fabAppGames = root.findViewById(R.id.fabAppGames);

        this.lblEntriesCount = root.findViewById(R.id.lblEntriesCount);


        this.lvMedia = root.findViewById(R.id.lvMedia);
        this.initFilter(root);
        this.initFilterActions();

        this.fabAppAdd.setOnClickListener(view -> {
            this.showAnimation(this.fabAppBooks);
            this.showAnimation(this.fabAppMusic);
            this.showAnimation(this.fabAppMovies);
            this.showAnimation(this.fabAppGames);
            if(this.isOpen) {
                this.fabAppAdd.startAnimation(this.fabAntiClock);
            } else {
                this.fabAppAdd.startAnimation(this.fabClock);
            }
            this.isOpen = !this.isOpen;
        });

        this.fabAppBooks.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                mainActivity.selectTab(this.getString(R.string.book), 0);
            }
        });

        this.fabAppMusic.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                mainActivity.selectTab(this.getString(R.string.album), 0);
            }
        });

        this.fabAppMovies.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                mainActivity.selectTab(this.getString(R.string.movie), 0);
            }
        });

        this.fabAppGames.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                mainActivity.selectTab(this.getString(R.string.game), 0);
            }
        });

        this.lvMedia.addButtonClick(R.drawable.ic_local_library_black_24dp, this.getString(R.string.library), objectList -> {
            try {
                AlertDialog.Builder b = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
                b.setTitle(getString(R.string.media_persons));
                List<String> personsString = new LinkedList<>();
                List<Person> people = MainActivity.GLOBALS.getDatabase().getPersons("", 0);
                for(Person person : people) {
                    personsString.add(String.format("%s %s", person.getFirstName(), person.getLastName()).trim());
                }
                if(!personsString.isEmpty()) {
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
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getContext());
            }
        });

        this.lvMedia.setOnReloadListener(()->this.reload(this.arrayAdapter.getItem(this.spFilter.getSelectedItemPosition())));

        this.lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                mainActivity.selectTab(listObject.getDescription(), ((BaseMediaObject) listObject.getObject()).getId());
            }
        });

        this.lvMedia.setOnDeleteListener(listObject -> {
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
            reload(this.arrayAdapter.getItem(this.spFilter.getSelectedItemPosition()));
        });

        this.reload(null);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(this.getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initFilter(View view) {
        this.filter = view.findViewById(R.id.filter);
        this.spFilter = view.findViewById(R.id.spFilter);
        this.cmdFilterExpand = view.findViewById(R.id.cmdFilterExpand);
        this.txtFilterName = view.findViewById(R.id.txtFilterName);
        this.cmdFilterSave = view.findViewById(R.id.cmdFilterSave);
        this.cmdFilterDelete = view.findViewById(R.id.cmdFilterDelete);
        this.chkFilterBooks = view.findViewById(R.id.chkFilterBooks);
        this.chkFilterMovies = view.findViewById(R.id.chkFilterMovies);
        this.chkFilterMusic = view.findViewById(R.id.chkFilterMusic);
        this.chkFilterGames = view.findViewById(R.id.chkFilterGames);
        this.txtFilterSearch = view.findViewById(R.id.txtFilterSearch);
        this.txtFilterCategories = view.findViewById(R.id.txtFilterCategory);
        this.txtFilterTags = view.findViewById(R.id.txtFilterTags);

        this.rowName = view.findViewById(R.id.rowName);
        this.rowMedia1 = view.findViewById(R.id.rowMedia1);
        this.rowMedia2 = view.findViewById(R.id.rowMedia2);

        this.arrayAdapter = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), android.R.layout.simple_spinner_item);
        this.spFilter.setAdapter(this.arrayAdapter);
        this.arrayAdapter.notifyDataSetChanged();
    }

    private void initFilterActions() {
        this.chkFilterGames.setOnCheckedChangeListener((compoundButton, b) -> this.setTempFilter());
        this.chkFilterMovies.setOnCheckedChangeListener((compoundButton, b) -> this.setTempFilter());
        this.chkFilterMusic.setOnCheckedChangeListener((compoundButton, b) -> this.setTempFilter());
        this.chkFilterBooks.setOnCheckedChangeListener((compoundButton, b) -> this.setTempFilter());
        this.txtFilterTags.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTempFilter();
            }
        });
        this.txtFilterCategories.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTempFilter();
            }
        });
        this.txtFilterSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setTempFilter();
            }
        });

        this.cmdFilterExpand.setOnClickListener(view -> {
            if(this.rowName != null) {
                if (this.rowName.getVisibility() == View.GONE) {
                    this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(this.getActivity(), R.drawable.ic_expand_less_black_24dp));
                    this.rowName.setVisibility(View.VISIBLE);
                    this.rowMedia1.setVisibility(View.VISIBLE);
                    this.rowMedia2.setVisibility(View.VISIBLE);
                    this.txtFilterSearch.setVisibility(View.VISIBLE);
                    this.txtFilterCategories.setVisibility(View.VISIBLE);
                    this.txtFilterTags.setVisibility(View.VISIBLE);
                } else {
                    this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(this.getActivity(), R.drawable.ic_expand_more_black_24dp));
                    this.rowName.setVisibility(View.GONE);
                    this.rowMedia1.setVisibility(View.GONE);
                    this.rowMedia2.setVisibility(View.GONE);
                    this.txtFilterSearch.setVisibility(View.GONE);
                    this.txtFilterCategories.setVisibility(View.GONE);
                    this.txtFilterTags.setVisibility(View.GONE);
                }
            } else {
                int px48 = ConvertHelper.convertDPToPixels(48, Objects.requireNonNull(this.getContext()));
                if(this.filter.getLayoutParams().height == px48) {
                    this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(this.getActivity(), R.drawable.ic_expand_less_black_24dp));
                    this.filter.getLayoutParams().height = TableLayout.LayoutParams.WRAP_CONTENT;
                } else {
                    this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(this.getActivity(), R.drawable.ic_expand_more_black_24dp));
                    this.filter.getLayoutParams().height = px48;
                }
            }

            this.filter.requestLayout();
        });
        this.cmdFilterDelete.setOnClickListener(view -> {
            MediaFilter mediaFilter = this.arrayAdapter.getItem(this.spFilter.getSelectedItemPosition());
            if(mediaFilter!=null) {
                MainActivity.GLOBALS.getDatabase().deleteItem(mediaFilter);
                this.reloadFilter();
            }
        });
        this.cmdFilterSave.setOnClickListener(view -> {
            this.getObject(this.tempFilter);
            if(!this.tempFilter.getTitle().equals(this.getString(R.string.filter_temp)) && !this.tempFilter.getTitle().equals(this.getString(R.string.filter_no_filter))) {
                MainActivity.GLOBALS.getDatabase().insertOrUpdateFilter(this.tempFilter);
                this.reloadFilter();
                this.setObject(this.tempFilter);
            }
        });
        this.spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(Objects.requireNonNull(arrayAdapter.getItem(i)).getTitle().equals(getString(R.string.filter_temp))) {
                    getObject(tempFilter);
                    reload(tempFilter);
                } else {
                    reload(arrayAdapter.getItem(i));
                }

                setObject(Objects.requireNonNull(arrayAdapter.getItem(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        this.reloadFilter();
    }

    private void reloadFilter() {
        this.arrayAdapter.clear();

        MediaFilter mediaFilter = new MediaFilter();
        mediaFilter.setTitle(this.getString(R.string.filter_no_filter));
        this.arrayAdapter.add(mediaFilter);

        this.tempFilter = new MediaFilter();
        this.tempFilter.setTitle(this.getString(R.string.filter_temp));
        this.arrayAdapter.add(this.tempFilter);

        MediaFilter bookFilter = new MediaFilter();
        bookFilter.setTitle("[" + this.getString(R.string.main_navigation_media_books) + "]");
        bookFilter.setBooks(true);
        bookFilter.setMusic(false);
        bookFilter.setGames(false);
        bookFilter.setMovies(false);
        this.arrayAdapter.add(bookFilter);

        MediaFilter movieFilter = new MediaFilter();
        movieFilter.setTitle("[" + this.getString(R.string.main_navigation_media_movies) + "]");
        movieFilter.setBooks(false);
        movieFilter.setMusic(false);
        movieFilter.setGames(false);
        movieFilter.setMovies(true);
        this.arrayAdapter.add(movieFilter);

        MediaFilter musicFilter = new MediaFilter();
        musicFilter.setTitle("[" + this.getString(R.string.main_navigation_media_music) + "]");
        musicFilter.setBooks(false);
        musicFilter.setMusic(true);
        musicFilter.setGames(false);
        musicFilter.setMovies(false);
        this.arrayAdapter.add(musicFilter);

        MediaFilter gamesFilter = new MediaFilter();
        gamesFilter.setTitle("[" + this.getString(R.string.main_navigation_media_games) + "]");
        gamesFilter.setBooks(false);
        gamesFilter.setMusic(false);
        gamesFilter.setGames(true);
        gamesFilter.setMovies(false);
        this.arrayAdapter.add(gamesFilter);

        for(MediaFilter filter : MainActivity.GLOBALS.getDatabase().getFilters("")) {
            this.arrayAdapter.add(filter);
        }
        this.reload(null);
    }

    private void setObject(MediaFilter mediaFilter) {
        if(!mediaFilter.getTitle().equals(this.getString(R.string.filter_temp))) {
            this.txtFilterName.setText(mediaFilter.getTitle());
        } else {
            this.txtFilterName.setText("");
        }
        this.txtFilterSearch.setText(mediaFilter.getSearch());
        this.txtFilterCategories.setText(mediaFilter.getCategories());
        this.txtFilterTags.setText(mediaFilter.getTags());

        this.chkFilterBooks.setChecked(mediaFilter.isBooks());
        this.chkFilterMovies.setChecked(mediaFilter.isMovies());
        this.chkFilterMusic.setChecked(mediaFilter.isMusic());
        this.chkFilterGames.setChecked(mediaFilter.isGames());
    }

    private void getObject(MediaFilter mediaFilter) {
        if(!mediaFilter.getTitle().equals(this.getString(R.string.filter_no_filter)) && !txtFilterName.getText().toString().trim().isEmpty()) {
            mediaFilter.setTitle(txtFilterName.getText().toString());
        }
        mediaFilter.setSearch(txtFilterSearch.getText().toString());
        mediaFilter.setCategories(txtFilterCategories.getText().toString());
        mediaFilter.setTags(txtFilterTags.getText().toString());

        mediaFilter.setBooks(chkFilterBooks.isChecked());
        mediaFilter.setMovies(chkFilterMovies.isChecked());
        mediaFilter.setMusic(chkFilterMusic.isChecked());
        mediaFilter.setGames(chkFilterGames.isChecked());
    }

    private void setTempFilter() {
        try {
            tempFilter = Objects.requireNonNull(this.arrayAdapter.getItem(this.spFilter.getSelectedItemPosition()));
            if(tempFilter.getTitle().equals(this.getString(R.string.filter_temp))) {
                this.getObject(tempFilter);
                reload(tempFilter);
            }
        } catch (Exception ignored) {}
    }

    private void reload(MediaFilter mediaFilter) {
        try {
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    this.search = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                this.search = "";
            }

            int counter = 0;
            this.lvMedia.getAdapter().clear();
            List<BaseDescriptionObject> baseDescriptionObjects;
            if(mediaFilter==null) {
                baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getActivity(), this.search);
            } else {
                if(mediaFilter.getTitle().trim().equals(this.getString(R.string.filter_no_filter))) {
                    baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getActivity(), this.search);
                } else {
                    baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getActivity(), mediaFilter);
                }
            }
            for(BaseDescriptionObject baseDescriptionObject : baseDescriptionObjects) {
                this.lvMedia.getAdapter().add(baseDescriptionObject);
                counter++;
            }

            String count = String.format("%s: %s", Objects.requireNonNull(this.getActivity()).getString(R.string.main_navigation_media), counter);
            this.lblEntriesCount.setText(count);
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
            this.reload(null);
        }
    }

    @Override
    public void select() {

    }

    private void showAnimation(FloatingActionButton fab) {
        if(this.isOpen) {
            fab.startAnimation(this.fabClose);
            fab.setClickable(false);
        } else {
            fab.startAnimation(this.fabOpen);
            fab.setClickable(true);
        }
    }
}