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

import androidx.activity.result.ActivityResult;
import androidx.fragment.app.Fragment;

import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivemobile.adapter.AbstractPagerAdapter;

/** @noinspection rawtypes*/
public abstract class AbstractFragment<T> extends Fragment {
    AbstractPagerAdapter abstractPagerAdapter;

    public abstract void setMediaObject(T baseMediaObject);
    public abstract T getMediaObject();

    public void setAbstractPagerAdapter(AbstractPagerAdapter abstractPagerAdapter) {
        this.abstractPagerAdapter = abstractPagerAdapter;
    }

    public abstract void changeMode(boolean editMode);

    public abstract Validator initValidation(Validator validator);

    public void onResult(ActivityResult result) {

    }
}
