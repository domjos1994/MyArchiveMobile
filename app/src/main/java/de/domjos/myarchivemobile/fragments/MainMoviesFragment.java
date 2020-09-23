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

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.model.tasks.AbstractTask;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.tasks.EANDataMovieTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.MoviePagerAdapter;
import de.domjos.myarchivemobile.helper.ControlsHelper;
import de.domjos.myarchivemobile.settings.Globals;
import de.domjos.myarchivemobile.tasks.LoadingTask;

public class MainMoviesFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvMovies;
    private MoviePagerAdapter moviePagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private TextView txtStatistics;
    private String search;
    private ViewGroup spl;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;
    private boolean changePage;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_movies, container, false);
        this.initControls(root);

        this.lvMovies.setOnReloadListener(MainMoviesFragment.this::reload);
        this.lvMovies.setOnDeleteListener(listObject -> {
            Movie movie = (Movie) listObject.getObject();
            MainActivity.GLOBALS.getDatabase().deleteItem(movie);
            this.changeMode(false, false);
            this.moviePagerAdapter.setMediaObject(new Movie());
        });
        this.lvMovies.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            this.currentObject = listObject;
            this.currentObject.setObject(ControlsHelper.getObject(listObject, this.requireContext()));
            this.moviePagerAdapter.setMediaObject((Movie) this.currentObject.getObject());
            this.changeMode(false, true);
        });
        this.lvMovies.addButtonClick(R.drawable.icon_movie, this.getString(R.string.movie_last_seen), list -> {
            for(BaseDescriptionObject baseDescriptionObject : list) {
                Movie movie = (Movie) baseDescriptionObject.getObject();
                movie.setLastSeen(new Date());
                MainActivity.GLOBALS.getDatabase().insertOrUpdateMovie(movie);
            }
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdNext:
                    this.changePage = true;
                    MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.MOVIES) + 1, Globals.MOVIES);
                    this.reload();
                    break;
                case R.id.cmdPrevious:
                    this.changePage = true;
                    MainActivity.GLOBALS.setPage(MainActivity.GLOBALS.getPage(Globals.MOVIES) - 1, Globals.MOVIES);
                    this.reload();
                    break;
                case R.id.cmdAdd:
                    if(menuItem.getTitle().equals(this.getString(R.string.sys_add))) {
                        this.changeMode(true, false);
                        this.moviePagerAdapter.setMediaObject(new Movie());
                        this.currentObject = null;
                    } else {
                        this.changeMode(false, false);
                        this.moviePagerAdapter.setMediaObject(new Movie());
                        currentObject = null;
                        this.reload();
                    }
                    break;
                case R.id.cmdEdit:
                    if(menuItem.getTitle().equals(this.getString(R.string.sys_edit))) {
                        if(this.currentObject != null) {
                            this.changeMode(true, true);
                            this.moviePagerAdapter.setMediaObject((Movie) this.currentObject.getObject());
                        }
                    } else {
                        if(this.validator.getState()) {
                            Movie movie = this.moviePagerAdapter.getMediaObject();
                            if(this.currentObject!=null) {
                                movie.setId(((Movie) this.currentObject.getObject()).getId());
                            }
                            if(this.validator.checkDuplicatedEntry(movie.getTitle(), movie.getId(), this.lvMovies.getAdapter().getList())) {
                                MainActivity.GLOBALS.getDatabase().insertOrUpdateMovie(movie);
                                this.changeMode(false, false);
                                this.moviePagerAdapter.setMediaObject(new Movie());
                                this.currentObject = null;
                                this.reload();
                            }
                        } else {
                            MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, this.getActivity());
                        }
                    }
                    break;
            }
            return true;
        });

        this.reload();
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.moviePagerAdapter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void changeMode(boolean editMode, boolean selected) {
        this.validator.clear();
        ControlsHelper.navViewEditMode(editMode, selected, this.bottomNavigationView);
        ControlsHelper.splitPaneEditMode(this.spl, editMode);
        this.moviePagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvMovies = view.findViewById(R.id.lvMediaMovies);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);

        this.txtStatistics = view.findViewById(R.id.lblNumber);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(6);
        tabLayout.setupWithViewPager(viewPager);

        this.spl = view.findViewById(R.id.spl);

        this.moviePagerAdapter = new MoviePagerAdapter(Objects.requireNonNull(this.requireActivity().getSupportFragmentManager()), this.getContext(),() -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, moviePagerAdapter, currentObject, lvMovies, new Movie(), Globals.MOVIES));
        this.validator = this.moviePagerAdapter.initValidator();
        viewPager.setAdapter(this.moviePagerAdapter);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.icon_general);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.icon_image);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.icon_person);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.icon_movie);
        Objects.requireNonNull(tabLayout.getTabAt(4)).setIcon(R.drawable.icon_video);
        Objects.requireNonNull(tabLayout.getTabAt(5)).setIcon(R.drawable.icon_stars);
        Objects.requireNonNull(tabLayout.getTabAt(6)).setIcon(R.drawable.icon_field);
    }

    private void reload() {
        try {
            String searchQuery = "";
            if(this.search != null) {
                if(!this.search.isEmpty()) {
                    searchQuery = "title like '%" + this.search + "%' or originalTitle like '%" + this.search + "%'";
                }
            } else {
                if(!MainActivity.getQuery().isEmpty()) {
                    searchQuery = "title like '%" + MainActivity.getQuery() + "%' or originalTitle like '%" + MainActivity.getQuery() + "%'";
                } else {
                    searchQuery = "";
                }
            }

            this.lvMovies.getAdapter().clear();
            String key = this.returnKey();
            key = ControlsHelper.setThePage(this, "movies", key);
            LoadingTask<Movie> loadingTask = new LoadingTask<>(this.getActivity(), new Movie(), null, searchQuery, this.lvMovies, key);
            String finalKey = key;
            loadingTask.after((AbstractTask.PostExecuteListener<List<BaseDescriptionObject>>) movies -> {
                for(BaseDescriptionObject baseDescriptionObject : movies) {
                    lvMovies.getAdapter().add(baseDescriptionObject);
                }
                this.select();
                ControlsHelper.setMediaStatistics(this.txtStatistics, finalKey);
            });
            loadingTask.execute();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    private String returnKey() {
        String key = Globals.MOVIES;

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
        try {
            if(parent.equals(this.getString(R.string.main_navigation_media_movies))) {
                String[] code = codes.split("\n");
                EANDataMovieTask eanDataService = new EANDataMovieTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.icon_notification, MainActivity.GLOBALS.getSettings().getEANDataKey());
                List<Movie> movies = eanDataService.execute(code).get();
                for(Movie movie : movies) {
                    MainActivity.GLOBALS.getDatabase().insertOrUpdateMovie(movie);
                }
                this.reload();
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
        if(this.getArguments() != null) {
            long id = this.getArguments().getLong("id");
            if(id != 0) {
                for(int i = 0; i<=this.lvMovies.getAdapter().getItemCount()-1; i++) {
                    BaseDescriptionObject baseDescriptionObject = this.lvMovies.getAdapter().getItem(i);
                    BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                    if(baseMediaObject.getId() == id) {
                        currentObject = baseDescriptionObject;
                        this.currentObject.setObject(ControlsHelper.getObject(baseDescriptionObject, this.requireContext()));
                        moviePagerAdapter.setMediaObject((Movie) currentObject.getObject());
                        lvMovies.select(currentObject);
                        changeMode(false, true);
                        return;
                    }
                }
            }
        }
    }
}