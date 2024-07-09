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

package de.domjos.myarchivemobile.fragments;

import androidx.activity.result.ActivityResult;
import androidx.fragment.app.Fragment;

public abstract class ParentFragment extends Fragment {

    public abstract void setCodes(String codes, String label);

    public abstract void reload(String search, boolean reload);

    public void changeMode(boolean editMode, boolean selected) {

    }

    protected void onResult(ActivityResult result) {

    }

    public abstract void select();
}
