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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import androidx.annotation.NonNull;

import de.domjos.myarchivemobile.R;

public class CustomAutoCompleteAdapter<T> extends ArrayAdapter<T> {

    public CustomAutoCompleteAdapter(@NonNull Context context, AutoCompleteTextView txt) {
        super(context, android.R.layout.simple_expandable_list_item_1);
        txt.setDropDownBackgroundResource(R.color.windowBackground);
    }

    public CustomAutoCompleteAdapter(@NonNull Context context, MultiAutoCompleteTextView txt) {
        super(context, android.R.layout.simple_expandable_list_item_1);
        txt.setDropDownBackgroundResource(R.color.windowBackground);
    }
}
