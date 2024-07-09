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

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.LinkedList;
import java.util.List;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.fragments.MediaCoverFragment;
import de.domjos.myarchivemobile.fragments.MediaListFragment;
import de.domjos.myarchivemobile.fragments.PersonFragment;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class PersonPagerAdapter extends AbstractStateAdapter<Person> {
    private final PersonFragment personFragment;
    private final MediaCoverFragment<Person> mediaCoverFragment;
    private final MediaListFragment mediaListFragment;
    private final Context context;

    public PersonPagerAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity, context);

        this.context = context;

        this.personFragment = new PersonFragment();
        this.mediaCoverFragment = new MediaCoverFragment<>();
        this.mediaListFragment = new MediaListFragment();
        this.mediaListFragment.setPerson(true);

        this.personFragment.setAbstractStateAdapter(this);
        this.mediaCoverFragment.setAbstractStateAdapter(this);
        this.mediaListFragment.setAbstractStateAdapter(this);
    }

    @Override
    public void changeMode(boolean editMode) {
        this.personFragment.changeMode(editMode);
        this.mediaCoverFragment.changeMode(editMode);
        this.mediaListFragment.changeMode(editMode);
    }

    @Override
    public void setMediaObject(Person mediaObject) {
        try {
            this.personFragment.setMediaObject(mediaObject);
            this.mediaCoverFragment.setMediaObject(mediaObject);

            List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getObjects(mediaObject.getTable(), mediaObject.getId(), MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("persons"))) {
                baseDescriptionObjects.add(ControlsHelper.convertMediaToDescriptionObject(baseMediaObject, this.context));
            }
            this.mediaListFragment.setMediaObject(baseDescriptionObjects);

            this.mediaListFragment.setLibraryObjects(MainActivity.GLOBALS.getDatabase().getLendOutObjects(mediaObject, MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset("persons")));
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
    }

    @Override
    public Person getMediaObject() {
        Person person = this.personFragment.getMediaObject();
        person.setImage(this.mediaCoverFragment.getMediaObject().getImage());
        return person;
    }

    @Override
    public void onResult(ActivityResult result) {
        this.personFragment.onResult(result);
        this.mediaCoverFragment.onResult(result);
        this.mediaListFragment.onResult(result);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> this.personFragment;
            case 1 -> this.mediaCoverFragment;
            case 2 -> this.mediaListFragment;
            default -> new Fragment();
        };
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public Validator initValidator() {
        Validator validator = new Validator(super.context, R.mipmap.ic_launcher_round);
        validator = this.personFragment.initValidation(validator);
        validator = this.mediaCoverFragment.initValidation(validator);
        return this.mediaListFragment.initValidation(validator);
    }
}
