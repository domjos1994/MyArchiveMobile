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

package de.domjos.myarchivemobile.adapter;

import android.content.Context;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.fragments.AbstractFragment;
import de.domjos.myarchivemobile.fragments.MediaAlbumFragment;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaCustomFieldFragment;
import de.domjos.myarchivemobile.fragments.MediaGeneralFragment;
import de.domjos.myarchivemobile.fragments.MediaPersonsCompaniesFragment;
import de.domjos.myarchivemobile.fragments.MediaRatingFragment;

public class AlbumPagerAdapter extends AbstractStateAdapter<Album> {
    private final AbstractFragment<BaseMediaObject> mediaCoverFragment;
    private final AbstractFragment<BaseMediaObject> mediaGeneralFragment;
    private final AbstractFragment<BaseMediaObject> mediaAlbumFragment;
    private final AbstractFragment<BaseMediaObject> mediaPersonsCompaniesFragment;
    private final AbstractFragment<BaseMediaObject> mediaRatingFragment;
    private final AbstractFragment<BaseMediaObject> mediaCustomFieldFragment;
    private final Runnable runnable;
    private boolean first = true;

    public AlbumPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context, Runnable runnable) {
        super(fragmentActivity, context);

        this.runnable = runnable;

        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaGeneralFragment = new MediaGeneralFragment();
        this.mediaAlbumFragment = new MediaAlbumFragment();
        this.mediaPersonsCompaniesFragment = new MediaPersonsCompaniesFragment();
        this.mediaRatingFragment = new MediaRatingFragment();
        this.mediaCustomFieldFragment = new MediaCustomFieldFragment();

        this.mediaCoverFragment.setAbstractStateAdapter(this);
        this.mediaGeneralFragment.setAbstractStateAdapter(this);
        this.mediaAlbumFragment.setAbstractStateAdapter(this);
        this.mediaPersonsCompaniesFragment.setAbstractStateAdapter(this);
        this.mediaRatingFragment.setAbstractStateAdapter(this);
        this.mediaCustomFieldFragment.setAbstractStateAdapter(this);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(this.first) {
            this.runnable.run();
            this.first = false;
        }

        return switch (position) {
            case 0 -> this.mediaGeneralFragment;
            case 1 -> this.mediaCoverFragment;
            case 2 -> this.mediaPersonsCompaniesFragment;
            case 3 -> this.mediaAlbumFragment;
            case 4 -> this.mediaRatingFragment;
            case 5 -> this.mediaCustomFieldFragment;
            default -> new Fragment();
        };
    }

    @Override
    public void changeMode(boolean editMode) {
        this.mediaGeneralFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaAlbumFragment.changeMode(editMode);
        this.mediaPersonsCompaniesFragment.changeMode(editMode);
        this.mediaRatingFragment.changeMode(editMode);
        this.mediaCustomFieldFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Album album) {
        this.mediaGeneralFragment.setMediaObject(album);
        this.mediaCoverFragment.setMediaObject(album);
        this.mediaPersonsCompaniesFragment.setMediaObject(album);
        this.mediaAlbumFragment.setMediaObject(album);
        this.mediaRatingFragment.setMediaObject(album);
        this.mediaCustomFieldFragment.setMediaObject(album);
    }

    @Override
    public Album getMediaObject() {
        BaseMediaObject baseMediaObject = this.mediaGeneralFragment.getMediaObject();
        baseMediaObject.setCover(this.mediaCoverFragment.getMediaObject().getCover());
        BaseMediaObject tmp = this.mediaPersonsCompaniesFragment.getMediaObject();
        baseMediaObject.setCompanies(tmp.getCompanies());
        baseMediaObject.setPersons(tmp.getPersons());
        baseMediaObject.setRatingOwn(this.mediaRatingFragment.getMediaObject().getRatingOwn());
        baseMediaObject.setRatingWeb(this.mediaRatingFragment.getMediaObject().getRatingWeb());
        baseMediaObject.setRatingNote(this.mediaRatingFragment.getMediaObject().getRatingNote());
        baseMediaObject.setCustomFieldValues(this.mediaCustomFieldFragment.getMediaObject().getCustomFieldValues());
        Album album = (Album) baseMediaObject;
        Album tmpAlbum = (Album) this.mediaAlbumFragment.getMediaObject();
        album.setType(tmpAlbum.getType());
        album.setNumberOfDisks(tmpAlbum.getNumberOfDisks());
        album.setLastHeard(tmpAlbum.getLastHeard());
        return album;
    }

    @Override
    public void onResult(ActivityResult result) {
        this.mediaGeneralFragment.onResult(result);
        this.mediaCoverFragment.onResult(result);
        this.mediaPersonsCompaniesFragment.onResult(result);
        this.mediaAlbumFragment.onResult(result);
        this.mediaRatingFragment.onResult(result);
        this.mediaCustomFieldFragment.onResult(result);
    }

    @Override
    public int getItemCount() {
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
        return this.mediaAlbumFragment.initValidation(validator);
    }
}
