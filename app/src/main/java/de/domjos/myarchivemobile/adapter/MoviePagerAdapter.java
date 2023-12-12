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

/** @noinspection rawtypes*/
public class MoviePagerAdapter extends AbstractPagerAdapter<Movie> {
    private AbstractFragment<BaseMediaObject> mediaCoverFragment;
    private MediaGeneralFragment mediaGeneralFragment;
    private AbstractFragment<BaseMediaObject> mediaMovieFragment;
    private AbstractFragment<BaseMediaObject> mediaPlayerFragment;
    private AbstractFragment<BaseMediaObject> mediaPersonsCompaniesFragment;
    private AbstractFragment<BaseMediaObject> mediaRatingFragment;
    private AbstractFragment<BaseMediaObject> mediaCustomFieldFragment;
    private final Runnable runnable;
    private boolean first = true;

    public MoviePagerAdapter(@NonNull FragmentManager fm, Context context, Runnable runnable) {
        super(fm, context);

        this.runnable = runnable;

        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaMovieFragment = new MediaMovieFragment();
        this.mediaPlayerFragment = new MediaPlayerFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();
        this.mediaRatingFragment = new MediaRatingFragment();
        this.mediaCustomFieldFragment = new MediaCustomFieldFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaMovieFragment.setAbstractPagerAdapter(this);
        this.mediaPlayerFragment.setAbstractPagerAdapter(this);
        this.mediaPersonsCompaniesFragment.setAbstractPagerAdapter(this);
        this.mediaRatingFragment.setAbstractPagerAdapter(this);
        this.mediaCustomFieldFragment.setAbstractPagerAdapter(this);
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
    public Fragment getItem(int position) {
        return switch (position) {
            case 0 -> this.mediaGeneralFragment;
            case 1 -> this.mediaCoverFragment;
            case 2 -> this.mediaPersonsCompaniesFragment;
            case 3 -> this.mediaMovieFragment;
            case 4 -> this.mediaPlayerFragment;
            case 5 -> this.mediaRatingFragment;
            case 6 -> this.mediaCustomFieldFragment;
            default -> new Fragment();
        };
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0 -> {
                this.mediaGeneralFragment = (MediaGeneralFragment) createdFragment;
                return this.mediaGeneralFragment;
            }
            case 1 -> {
                this.mediaCoverFragment = (MediaCoverFragment<BaseMediaObject>) createdFragment;
                return this.mediaCoverFragment;
            }
            case 2 -> {
                this.mediaPersonsCompaniesFragment = (MediaPersonsCompaniesFragment) createdFragment;
                return this.mediaPersonsCompaniesFragment;
            }
            case 3 -> {
                this.mediaMovieFragment = (MediaMovieFragment) createdFragment;
                return this.mediaMovieFragment;
            }
            case 4 -> {
                this.mediaPlayerFragment = (MediaPlayerFragment) createdFragment;
                return this.mediaPlayerFragment;
            }
            case 5 -> {
                this.mediaRatingFragment = (MediaRatingFragment) createdFragment;
                return this.mediaRatingFragment;
            }
            case 6 -> {
                this.mediaCustomFieldFragment = (MediaCustomFieldFragment) createdFragment;
                return this.mediaCustomFieldFragment;
            }
            default -> {
                return new Fragment();
            }
        }
    }

    @Override
    public void changeMode(boolean editMode) {
        this.mediaGeneralFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaMovieFragment.changeMode(editMode);
        this.mediaPlayerFragment.changeMode(editMode);
        this.mediaPersonsCompaniesFragment.changeMode(editMode);
        this.mediaRatingFragment.changeMode(editMode);
        this.mediaCustomFieldFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Movie movie) {
        this.mediaGeneralFragment.setMediaObject(movie);
        this.mediaCoverFragment.setMediaObject(movie);
        this.mediaPersonsCompaniesFragment.setMediaObject(movie);
        this.mediaMovieFragment.setMediaObject(movie);
        this.mediaPlayerFragment.setMediaObject(movie);
        this.mediaRatingFragment.setMediaObject(movie);
        this.mediaCustomFieldFragment.setMediaObject(movie);
    }

    @Override
    public Movie getMediaObject() {
        BaseMediaObject baseMediaObject = this.mediaGeneralFragment.getMediaObject();
        baseMediaObject.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        BaseMediaObject tmp = this.mediaPersonsCompaniesFragment.getMediaObject();
        baseMediaObject.setCompanies(tmp.getCompanies());
        baseMediaObject.setPersons(tmp.getPersons());
        baseMediaObject.setRatingOwn(this.mediaRatingFragment.getMediaObject().getRatingOwn());
        baseMediaObject.setRatingWeb(this.mediaRatingFragment.getMediaObject().getRatingWeb());
        baseMediaObject.setRatingNote(this.mediaRatingFragment.getMediaObject().getRatingNote());
        baseMediaObject.setCustomFieldValues(this.mediaCustomFieldFragment.getMediaObject().getCustomFieldValues());
        Movie movie = (Movie) baseMediaObject;
        Movie tmpMovie = (Movie) this.mediaMovieFragment.getMediaObject();
        movie.setType(tmpMovie.getType());
        movie.setLength(tmpMovie.getLength());
        movie.setPath(tmpMovie.getPath());
        movie.setLastSeen(tmpMovie.getLastSeen());
        return movie;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.mediaGeneralFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaCoverFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaPersonsCompaniesFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaMovieFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaPlayerFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaRatingFragment.onActivityResult(requestCode, resultCode, data);
        this.mediaCustomFieldFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return " ";
    }

    @Override
    public int getCount() {
        return 7;
    }

    @Override
    public Validator initValidator() {
        Validator validator = new Validator(super.context, R.mipmap.ic_launcher_round);
        validator = this.mediaGeneralFragment.initValidation(validator);
        validator = this.mediaCoverFragment.initValidation(validator);
        validator = this.mediaPersonsCompaniesFragment.initValidation(validator);
        validator = this.mediaRatingFragment.initValidation(validator);
        validator = this.mediaCustomFieldFragment.initValidation(validator);
        validator = this.mediaPlayerFragment.initValidation(validator);
        return this.mediaMovieFragment.initValidation(validator);
    }
}
