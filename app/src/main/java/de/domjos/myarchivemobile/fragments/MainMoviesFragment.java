package de.domjos.myarchivemobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.Converter;
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

public class MainMoviesFragment extends ParentFragment {
    private SwipeRefreshDeleteList lvMovies;
    private MoviePagerAdapter moviePagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private String search;

    private BaseDescriptionObject currentObject = null;
    private Validator validator;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment_movies, container, false);
        this.initControls(root);

        this.lvMovies.reload(new SwipeRefreshDeleteList.ReloadListener() {
            @Override
            public void onReload() {
                MainMoviesFragment.this.reload();
            }
        });
        this.lvMovies.deleteItem(new SwipeRefreshDeleteList.DeleteListener() {
            @Override
            public void onDelete(BaseDescriptionObject listObject) {
                Movie movie = (Movie) listObject.getObject();
                MainActivity.GLOBALS.getDatabase().deleteItem(movie);
                changeMode(false, false);
                moviePagerAdapter.setMediaObject(new Movie());
            }
        });
        this.lvMovies.click(new SwipeRefreshDeleteList.ClickListener() {
            @Override
            public void onClick(BaseDescriptionObject listObject) {
                currentObject = listObject;
                moviePagerAdapter.setMediaObject((Movie) currentObject.getObject());
                changeMode(false, true);
            }
        });

        this.bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.cmdAdd:
                    this.changeMode(true, false);
                    this.moviePagerAdapter.setMediaObject(new Movie());
                    this.currentObject = null;
                    break;
                case R.id.cmdEdit:
                    if(this.currentObject != null) {
                        this.changeMode(true, true);
                        this.moviePagerAdapter.setMediaObject((Movie) this.currentObject.getObject());
                    }
                    break;
                case R.id.cmdCancel:
                    this.changeMode(false, false);
                    currentObject = null;
                    this.reload();
                    break;
                case R.id.cmdSave:
                    if(this.validator.getState()) {
                        Movie movie = this.moviePagerAdapter.getMediaObject();
                        if(this.validator.checkDuplicatedEntry(movie.getTitle(), this.lvMovies.getAdapter().getList())) {
                            if(this.currentObject!=null) {
                                movie.setId(((Movie) this.currentObject.getObject()).getId());
                            }
                            MainActivity.GLOBALS.getDatabase().insertOrUpdateMovie(movie);
                            this.changeMode(false, false);
                            this.currentObject = null;
                            this.reload();
                        }
                    } else {
                        MessageHelper.printMessage(this.validator.getResult(), R.mipmap.ic_launcher_round, this.getActivity());
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
        this.bottomNavigationView.getMenu().findItem(R.id.cmdAdd).setVisible(!editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(!editMode && selected);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(editMode);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(editMode);
        this.lvMovies.setLayoutParams(editMode ? MainActivity.CLOSE_LIST : MainActivity.OPEN_LIST);
        this.viewPager.setLayoutParams(editMode ? MainActivity.OPEN_PAGER : MainActivity.CLOSE_PAGER);

        this.moviePagerAdapter.changeMode(editMode);
    }

    private void initControls(View view) {
        this.lvMovies = view.findViewById(R.id.lvMediaMovies);
        this.bottomNavigationView = view.findViewById(R.id.navigationView);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        this.viewPager = view.findViewById(R.id.viewPager);
        this.viewPager.setOffscreenPageLimit(4);
        tabLayout.setupWithViewPager(this.viewPager);

        this.moviePagerAdapter = new MoviePagerAdapter(Objects.requireNonNull(this.getFragmentManager()), this.getContext(),() -> currentObject = ControlsHelper.loadItem(this.getActivity(), this, moviePagerAdapter, currentObject, lvMovies, new Movie()));
        this.validator = this.moviePagerAdapter.initValidator();
        this.viewPager.setAdapter(this.moviePagerAdapter);

        for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            this.viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        this.viewPager.setCurrentItem(0);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.ic_general_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.ic_image_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.ic_person_black_24dp);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.ic_local_movies_black_24dp);

        this.bottomNavigationView.getMenu().findItem(R.id.cmdEdit).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdCancel).setVisible(false);
        this.bottomNavigationView.getMenu().findItem(R.id.cmdSave).setVisible(false);
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

            this.lvMovies.getAdapter().clear();
            for(Movie movie : MainActivity.GLOBALS.getDatabase().getMovies(this.search)) {
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(movie.getTitle());
                baseDescriptionObject.setDescription(Converter.convertDateToString(movie.getReleaseDate(), this.getString(R.string.sys_date_format)));
                baseDescriptionObject.setCover(movie.getCover());
                baseDescriptionObject.setObject(movie);
                this.lvMovies.getAdapter().add(baseDescriptionObject);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.getActivity());
        }
    }

    @Override
    public void setCodes(String codes, String parent) {
        try {
            if(parent.equals(this.getString(R.string.main_navigation_media_movies))) {
                String[] code = codes.split("\n");
                EANDataMovieTask eanDataService = new EANDataMovieTask(this.getActivity(), MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, MainActivity.GLOBALS.getSettings().getEANDataKey());
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
        long id = Objects.requireNonNull(this.getArguments()).getLong("id");
        if(id != 0) {
            for(int i = 0; i<=this.lvMovies.getAdapter().getItemCount()-1; i++) {
                BaseDescriptionObject baseDescriptionObject = this.lvMovies.getAdapter().getItem(i);
                BaseMediaObject baseMediaObject = (BaseMediaObject) baseDescriptionObject.getObject();
                if(baseMediaObject.getId() == id) {
                    currentObject = baseDescriptionObject;
                    moviePagerAdapter.setMediaObject((Movie) currentObject.getObject());
                    changeMode(false, true);
                    return;
                }
            }
        }
    }
}