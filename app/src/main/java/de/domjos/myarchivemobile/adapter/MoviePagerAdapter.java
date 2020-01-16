package de.domjos.myarchivemobile.adapter;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivemobile.fragments.AbstractFragment;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaGeneralFragment;
import de.domjos.myarchivemobile.fragments.MediaMovieFragment;
import de.domjos.myarchivemobile.fragments.MediaPersonsCompaniesFragment;

public class MoviePagerAdapter extends AbstractPagerAdapter<Movie> {
    private AbstractFragment mediaCoverFragment;
    private AbstractFragment mediaGeneralFragment;
    private AbstractFragment mediaMovieFragment;
    private AbstractFragment mediaPersonsCompaniesFragment;

    public MoviePagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, context);

        this.mediaCoverFragment = new MediaCoverFragment();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaMovieFragment = new MediaMovieFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaMovieFragment.setAbstractPagerAdapter(this);
        this.mediaPersonsCompaniesFragment.setAbstractPagerAdapter(this);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return this.mediaGeneralFragment;
            case 1:
                return this.mediaCoverFragment;
            case 2:
                return this.mediaPersonsCompaniesFragment;
            case 3:
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
}
