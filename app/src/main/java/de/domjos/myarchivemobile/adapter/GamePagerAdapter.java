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
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.*;

/** @noinspection rawtypes*/
public class GamePagerAdapter extends AbstractPagerAdapter<Game> {
    private AbstractFragment<BaseMediaObject> mediaCoverFragment;
    private AbstractFragment<BaseMediaObject> mediaGeneralFragment;
    private AbstractFragment<BaseMediaObject> mediaGameFragment;
    private AbstractFragment<BaseMediaObject> mediaPersonsCompaniesFragment;
    private AbstractFragment<BaseMediaObject> mediaRatingFragment;
    private AbstractFragment<BaseMediaObject> mediaCustomFieldFragment;
    private final Runnable runnable;
    private boolean first = true;

    public GamePagerAdapter(@NonNull FragmentManager fm, Context context, Runnable runnable) {
        super(fm, context);

        this.runnable = runnable;

        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaGameFragment = new MediaGameFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();
        this.mediaRatingFragment = new MediaRatingFragment();
        this.mediaCustomFieldFragment = new MediaCustomFieldFragment();

        this.mediaCoverFragment.setAbstractPagerAdapter(this);
        this.mediaGeneralFragment.setAbstractPagerAdapter(this);
        this.mediaGameFragment.setAbstractPagerAdapter(this);
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
            case 3 -> this.mediaGameFragment;
            case 4 -> this.mediaRatingFragment;
            case 5 -> this.mediaCustomFieldFragment;
            default -> new Fragment();
        };
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
        return switch (position) {
            case 0 -> {
                this.mediaGeneralFragment = (MediaGeneralFragment) createdFragment;
                yield this.mediaGeneralFragment;
            }
            case 1 -> {
                this.mediaCoverFragment = (MediaCoverFragment) createdFragment;
                yield this.mediaCoverFragment;
            }
            case 2 -> {
                this.mediaPersonsCompaniesFragment = (MediaPersonsCompaniesFragment) createdFragment;
                yield this.mediaPersonsCompaniesFragment;
            }
            case 3 -> {
                this.mediaGameFragment = (MediaGameFragment) createdFragment;
                yield this.mediaGameFragment;
            }
            case 4 -> {
                this.mediaRatingFragment = (MediaRatingFragment) createdFragment;
                yield this.mediaRatingFragment;
            }
            case 5 -> {
                this.mediaCustomFieldFragment = (MediaCustomFieldFragment) createdFragment;
                yield this.mediaCustomFieldFragment;
            }
            default -> new Fragment();
        };
    }

    @Override
    public void changeMode(boolean editMode) {
        this.mediaGeneralFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaGameFragment.changeMode(editMode);
        this.mediaPersonsCompaniesFragment.changeMode(editMode);
        this.mediaRatingFragment.changeMode(editMode);
        this.mediaCustomFieldFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Game game) {
        this.mediaGeneralFragment.setMediaObject(game);
        this.mediaCoverFragment.setMediaObject(game);
        this.mediaPersonsCompaniesFragment.setMediaObject(game);
        this.mediaGameFragment.setMediaObject(game);
        this.mediaRatingFragment.setMediaObject(game);
        this.mediaCustomFieldFragment.setMediaObject(game);
    }

    @Override
    public Game getMediaObject() {
        BaseMediaObject baseMediaObject = this.mediaGeneralFragment.getMediaObject();
        baseMediaObject.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        BaseMediaObject tmp = this.mediaPersonsCompaniesFragment.getMediaObject();
        baseMediaObject.setCompanies(tmp.getCompanies());
        baseMediaObject.setPersons(tmp.getPersons());
        baseMediaObject.setRatingOwn(this.mediaRatingFragment.getMediaObject().getRatingOwn());
        baseMediaObject.setRatingWeb(this.mediaRatingFragment.getMediaObject().getRatingWeb());
        baseMediaObject.setRatingNote(this.mediaRatingFragment.getMediaObject().getRatingNote());
        baseMediaObject.setCustomFieldValues(this.mediaCustomFieldFragment.getMediaObject().getCustomFieldValues());
        Game game = (Game) baseMediaObject;
        Game tmpGame = (Game) this.mediaGameFragment.getMediaObject();
        game.setType(tmpGame.getType());
        game.setLength(tmpGame.getLength());
        game.setLastPlayed(tmpGame.getLastPlayed());
        return game;
    }

    @Override
    public void onResult(ActivityResult result) {
        this.mediaGeneralFragment.onResult(result);
        this.mediaCoverFragment.onResult(result);
        this.mediaPersonsCompaniesFragment.onResult(result);
        this.mediaGameFragment.onResult(result);
        this.mediaRatingFragment.onResult(result);
        this.mediaCustomFieldFragment.onResult(result);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return " ";
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public Validator initValidator() {
        Validator validator = new Validator(super.context, R.mipmap.ic_launcher_round);
        validator = this.mediaGeneralFragment.initValidation(validator);
        validator = this.mediaCoverFragment.initValidation(validator);
        validator = this.mediaPersonsCompaniesFragment.initValidation(validator);
        validator = this.mediaRatingFragment.initValidation(validator);
        validator = this.mediaCustomFieldFragment.initValidation(validator);
        return this.mediaGameFragment.initValidation(validator);
    }
}
