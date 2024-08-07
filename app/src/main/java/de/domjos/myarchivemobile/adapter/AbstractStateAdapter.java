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
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import de.domjos.customwidgets.utils.Validator;

public abstract class AbstractStateAdapter<T> extends FragmentStateAdapter {
    protected Context context;

    public AbstractStateAdapter(@NonNull FragmentActivity fragmentActivity, Context context) {
        super(fragmentActivity);

        this.context = context;
    }

    public abstract void changeMode(boolean editMode);

    public abstract void setMediaObject(T mediaObject);
    public abstract T getMediaObject();

    public abstract void onResult(ActivityResult result);

    public abstract Validator initValidator();
}
