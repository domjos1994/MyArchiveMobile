package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.*;

public class MoviePagerAdapter extends AbstractPagerAdapter<Movie> {
    private AbstractFragment<BaseMediaObject> mediaCoverFragment;
    private AbstractFragment<BaseMediaObject> mediaGeneralFragment;
    private AbstractFragment<BaseMediaObject> mediaMovieFragment;
    private AbstractFragment<BaseMediaObject> mediaPersonsCompaniesFragment;
    private Runnable runnable;
    private boolean first = true;

    public MoviePagerAdapter(@NonNull FragmentManager fm, Context context, Runnable runnable) {
        super(fm, context);

        this.runnable = runnable;

        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaMovieFragment = new MediaMovieFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaMovieFragment.setAbstractPagerAdapter(this);
        this.mediaPersonsCompaniesFragment.setAbstractPagerAdapter(this);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        super.finishUpdate(container);

        if(this.first) {
            this.runnable.run();
            this.first = false;
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                this.mediaGeneralFragment = (MediaGeneralFragment) super.getFragment(position, this.mediaGeneralFragment);
                return this.mediaGeneralFragment;
            case 1:
                this.mediaCoverFragment = (MediaCoverFragment) super.getFragment(position, this.mediaCoverFragment);
                return this.mediaCoverFragment;
            case 2:
                this.mediaPersonsCompaniesFragment = (MediaPersonsCompaniesFragment) super.getFragment(position, this.mediaPersonsCompaniesFragment);
                return this.mediaPersonsCompaniesFragment;
            case 3:
                this.mediaMovieFragment = (MediaMovieFragment) super.getFragment(position, this.mediaMovieFragment);
                return this.mediaMovieFragment;
            default:
                return new Fragment();
        }
    }

    @Override
    public void changeMode(boolean editMode) {
        this.mediaGeneralFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaMovieFragment.changeMode(editMode);
        this.mediaPersonsCompaniesFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Movie movie) {
        this.mediaGeneralFragment.setMediaObject(movie);
        this.mediaCoverFragment.setMediaObject(movie);
        this.mediaPersonsCompaniesFragment.setMediaObject(movie);
        this.mediaMovieFragment.setMediaObject(movie);
    }

    @Override
    public Movie getMediaObject() {
        BaseMediaObject baseMediaObject = this.mediaGeneralFragment.getMediaObject();
        baseMediaObject.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        BaseMediaObject tmp = this.mediaPersonsCompaniesFragment.getMediaObject();
        baseMediaObject.setCompanies(tmp.getCompanies());
        baseMediaObject.setPersons(tmp.getPersons());
        Movie movie = (Movie) baseMediaObject;
        Movie tmpMovie = (Movie) this.mediaMovieFragment.getMediaObject();
        movie.setType(tmpMovie.getType());
        movie.setLength(tmpMovie.getLength());
        movie.setPath(movie.getPath());
        return movie;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mediaGeneralFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaCoverFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaPersonsCompaniesFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaMovieFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return " ";
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Validator initValidator() {
        Validator validator = new Validator(super.context, R.mipmap.ic_launcher_round);
        validator = this.mediaGeneralFragment.initValidation(validator);
        validator = this.mediaCoverFragment.initValidation(validator);
        validator = this.mediaPersonsCompaniesFragment.initValidation(validator);
        return this.mediaMovieFragment.initValidation(validator);
    }
}
