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

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivelibrary.custom.AbstractTask;
import de.domjos.customwidgets.tokenizer.CommaTokenizer;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.CustomAutoCompleteAdapter;
import de.domjos.myarchivemobile.adapter.CustomSpinnerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public class MainHomeFragment extends ParentFragment {
    private Animation fabOpen, fabClose, fabClock, fabAntiClock;
    private FloatingActionButton fabAppAdd, fabAppBooks, fabAppMusic, fabAppMovies, fabAppGames;
    private TextView lblEntriesCount;

    private TableLayout filter;
    private MediaFilter tempFilter;
    private Spinner spFilter;
    private CustomSpinnerAdapter<MediaFilter> arrayAdapter;
    private EditText txtFilterName, txtFilterSearch, txtFilterCategories, txtFilterTags;
    private MultiAutoCompleteTextView txtFilterCustomFields;
    private ImageButton cmdFilterExpand, cmdFilterSave, cmdFilterDelete;
    private CheckBox chkFilterBooks, chkFilterMovies, chkFilterMusic, chkFilterGames;
    private Spinner spFilterList;
    private CustomSpinnerAdapter<MediaList> filterListAdapter;
    private TableRow rowName, rowMedia1, rowMedia2, rowList;

    private SwipeRefreshDeleteList lvMedia;
    private String search;
    private boolean isOpen = false, changePage;

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
        ImageButton cmdNext = root.findViewById(R.id.cmdNext);
        ImageButton cmdPrevious = root.findViewById(R.id.cmdPrevious);

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
                ControlsHelper.fromHome = true;
                mainActivity.selectTab(this.getString(R.string.book), 0);
            }
        });

        this.fabAppMusic.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                ControlsHelper.fromHome = true;
                mainActivity.selectTab(this.getString(R.string.album), 0);
            }
        });

        this.fabAppMovies.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                ControlsHelper.fromHome = true;
                mainActivity.selectTab(this.getString(R.string.movie), 0);
            }
        });

        this.fabAppGames.setOnClickListener(view -> {
            MainActivity mainActivity = ((MainActivity)MainHomeFragment.this.getActivity());
            if(mainActivity != null) {
                ControlsHelper.fromHome = true;
                mainActivity.selectTab(this.getString(R.string.game), 0);
            }
        });

        cmdPrevious.setOnClickListener(view -> {
            this.changePage = true;
            MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.HOME) - 1, Globals.HOME);
            this.reload(search, true);
        });

        cmdNext.setOnClickListener(view -> {
            this.changePage = true;
            MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.HOME) + 1, Globals.HOME);
            this.reload(search, true);
        });

        this.lvMedia.addButtonClick(R.drawable.icon_library, this.getString(R.string.library), objectList -> {
            try {
                AlertDialog.Builder b = new AlertDialog.Builder(requireActivity());
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
                        MessageHelper.printMessage(String.format(this.getString(R.string.sys_success), this.getString(R.string.sys_add)), R.mipmap.ic_launcher_round, this.getActivity());
                    });
                    b.show();
                }
            } catch (Exception ex) {
                MessageHelper.printException(ex, R.mipmap.ic_launcher_round, getContext());
            }
        });

        this.lvMedia.addButtonClick(R.drawable.icon_list, this.getString(R.string.lists), objectList -> {
            try {
                AlertDialog.Builder b = new AlertDialog.Builder(requireActivity());
                b.setTitle(getString(R.string.lists));
                List<String> listString = new LinkedList<>();
                List<MediaList> lists = MainActivity.GLOBALS.getDatabase().getMediaLists("", MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("home"));
                for(MediaList list : lists) {
                    listString.add(list.getTitle());
                }
                if(!listString.isEmpty()) {
                    String[] listArray = listString.toArray(new String[]{});
                    b.setItems(listArray, ((dialogInterface, i) -> {
                        MediaList mediaList = lists.get(i);
                        for(BaseDescriptionObject baseDescriptionObject : objectList) {
                            mediaList.getBaseMediaObjects().add((BaseMediaObject) baseDescriptionObject.getObject());
                        }
                        MainActivity.GLOBALS.getDatabase().insertOrUpdateMediaList(mediaList);
                        MessageHelper.printMessage(String.format(this.getString(R.string.sys_success), this.getString(R.string.sys_add)), R.mipmap.ic_launcher_round, this.getActivity());
                        reloadFilter();
                    }));
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
                ControlsHelper.fromHome = true;
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

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

        this.txtFilterCustomFields = view.findViewById(R.id.txtFilterCustomFields);
        this.txtFilterCustomFields.setTokenizer(new CommaTokenizer());
        List<CustomField> customFields = MainActivity.GLOBALS.getDatabase(requireContext()).getCustomFields("");
        CustomAutoCompleteAdapter<String> arrayAdapter = new CustomAutoCompleteAdapter<>(this.requireContext(), this.txtFilterCustomFields);
        for(CustomField customField : customFields) {
            arrayAdapter.add(customField.getTitle());
        }
        this.txtFilterCustomFields.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

        this.spFilterList = view.findViewById(R.id.spFilterList);
        this.filterListAdapter = new CustomSpinnerAdapter<>(this.requireContext());
        this.spFilterList.setAdapter(this.filterListAdapter);
        this.filterListAdapter.notifyDataSetChanged();
        this.reloadFilterListList();

        this.rowName = view.findViewById(R.id.rowName);
        this.rowMedia1 = view.findViewById(R.id.rowMedia1);
        this.rowMedia2 = view.findViewById(R.id.rowMedia2);
        this.rowList = view.findViewById(R.id.rowList);

        this.arrayAdapter = new CustomSpinnerAdapter<>(this.requireContext());
        this.spFilter.setAdapter(this.arrayAdapter);
        this.arrayAdapter.notifyDataSetChanged();
    }

    private void reloadFilterListList() {
        try {
            this.filterListAdapter.clear();
            MediaList emptyMediaList = new MediaList();
            emptyMediaList.setId(0);
            this.filterListAdapter.add(emptyMediaList);
            for(MediaList mediaList : MainActivity.GLOBALS.getDatabase().getMediaLists("", -1, 0)) {
                this.filterListAdapter.add(mediaList);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.requireActivity());
        }
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
        this.txtFilterCustomFields.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                setTempFilter();
            }
        });
        this.spFilterList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setTempFilter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        this.cmdFilterExpand.setOnClickListener(view -> {
            boolean noFilterSelected = this.spFilter.getSelectedItem().toString().equals(this.getString(R.string.filter_no_filter));
            boolean isList = Objects.requireNonNull(this.arrayAdapter.getItem(this.spFilter.getSelectedItemPosition())).isList();
            if (this.rowName.getVisibility() == View.VISIBLE || noFilterSelected || isList) {
                this.lessFilterView();
            } else {
                this.moreFilterView();
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
                boolean noFilterSelected = spFilter.getSelectedItem().toString().equals(getString(R.string.filter_no_filter));
                boolean isList = Objects.requireNonNull(arrayAdapter.getItem(spFilter.getSelectedItemPosition())).isList();
                if (noFilterSelected || isList) {
                    lessFilterView();
                }

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

    private void lessFilterView() {
        Activity activity = this.requireActivity();
        if(this.rowName != null) {
            this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(activity, R.drawable.icon_expand_more));
            this.rowName.setVisibility(View.GONE);
            this.rowMedia1.setVisibility(View.GONE);
            this.rowMedia2.setVisibility(View.GONE);
            this.rowList.setVisibility(View.GONE);
            this.txtFilterSearch.setVisibility(View.GONE);
            this.txtFilterCategories.setVisibility(View.GONE);
            this.txtFilterTags.setVisibility(View.GONE);
            this.txtFilterCustomFields.setVisibility(View.GONE);
        } else {
            int px48 = ConvertHelper.convertDPToPixels(48, this.requireContext());
            this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(activity, R.drawable.icon_expand_more));
            this.filter.getLayoutParams().height = px48;
        }
    }

    private void moreFilterView() {
        Activity activity = this.requireActivity();
        if(this.rowName != null) {
            this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(activity, R.drawable.icon_expand_less));
            this.rowName.setVisibility(View.VISIBLE);
            this.rowMedia1.setVisibility(View.VISIBLE);
            this.rowMedia2.setVisibility(View.VISIBLE);
            this.rowList.setVisibility(View.VISIBLE);
            this.txtFilterSearch.setVisibility(View.VISIBLE);
            this.txtFilterCategories.setVisibility(View.VISIBLE);
            this.txtFilterTags.setVisibility(View.VISIBLE);
            this.txtFilterCustomFields.setVisibility(View.VISIBLE);
        } else {
            this.cmdFilterExpand.setImageDrawable(ConvertHelper.convertResourcesToDrawable(activity, R.drawable.icon_expand_less));
            this.filter.getLayoutParams().height = TableLayout.LayoutParams.WRAP_CONTENT;
        }
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

        try {
            for(MediaList mediaList : MainActivity.GLOBALS.getDatabase().getMediaLists("", MainActivity.GLOBALS.getSettings(requireContext()).getMediaCount(), MainActivity.GLOBALS.getOffset("home"))) {
                MediaFilter listFilter = new MediaFilter();
                listFilter.setTitle("[" + mediaList.getTitle() + "]");
                listFilter.setMediaList(mediaList);
                listFilter.setList(true);
                this.arrayAdapter.add(listFilter);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }

        for(MediaFilter filter : MainActivity.GLOBALS.getDatabase().getFilters("")) {
            this.arrayAdapter.add(filter);
        }
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
        this.txtFilterCustomFields.setText(mediaFilter.getCustomFields());
        MediaList mediaList = mediaFilter.getMediaList();
        if(mediaList != null) {
            for(int position = 0; position <= this.filterListAdapter.getCount() - 1; position++) {
                if(mediaList.getId() == Objects.requireNonNull(this.filterListAdapter.getItem(position)).getId()) {
                    this.spFilterList.setSelection(position);
                    break;
                }
            }
        } else {
            this.spFilterList.setSelection(0);
        }
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
        mediaFilter.setMediaList(null);
        MediaList mediaList = this.filterListAdapter.getItem(this.spFilterList.getSelectedItemPosition());
        if(mediaList != null) {
            if(mediaList.getId() != 0) {
                mediaFilter.setMediaList(mediaList);
            }
        }

        mediaFilter.setCustomFields(txtFilterCustomFields.getText().toString());
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
            String searchString = "";
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    searchString = "(title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%')";
                }
            } else {
                searchString = "";
            }


            this.lvMedia.getAdapter().clear();
            String key = this.returnKey();
            LoadingTask<List<BaseDescriptionObject>> loadingTask = new LoadingTask<>(this.getActivity(), null, mediaFilter, searchString, this.lvMedia, key);
            loadingTask.after((AbstractTask.PostExecuteListener<List<BaseDescriptionObject>>) baseDescriptionObjects -> {
                if(baseDescriptionObjects != null) {
                    for(BaseDescriptionObject baseDescriptionObject : baseDescriptionObjects) {
                        lvMedia.getAdapter().add(baseDescriptionObject);
                    }

                    ControlsHelper.setMediaStatistics(lblEntriesCount, key);
                }
            });
            loadingTask.execute((Void) null);
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private String returnKey() {
        String key = Globals.HOME;

        if(this.search != null) {
            if(!this.search.isEmpty()) {
                key += Globals.SEARCH;
            }
        } else {
            if(!MainActivity.getQuery().isEmpty()) {
                key += Globals.SEARCH;
            }
        }

        if(!this.changePage) {
            key += Globals.RESET;
        } else {
            this.changePage = false;
        }
        return key;
    }

    @Override
    public void setCodes(String codes, String parent) {

    }

    @Override
    public void reload(String search, boolean reload) {
        MediaFilter mediaFilter = null;
        if(this.spFilter != null) {
            if(this.spFilter.getSelectedItem() != null) {
                mediaFilter = this.arrayAdapter.getItem(this.spFilter.getSelectedItemPosition());
            }
        }

        this.search = search;

        if(reload) {
            this.reload(mediaFilter);
        }
    }

    @Override
    public void select() {

    }

    @Override
    public void onResult(ActivityResult result) {
        if(result.getResultCode() == Activity.RESULT_OK) {
            this.reloadFilterListList();
        }
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