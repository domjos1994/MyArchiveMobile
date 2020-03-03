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
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

import de.domjos.myarchivemobile.R;

public class CustomSpinnerAdapter<T> extends ArrayAdapter<T> {

    public CustomSpinnerAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_spinner_item);
        super.setDropDownViewResource(R.layout.spinner_item);
    }

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull T[] objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        super.setDropDownViewResource(R.layout.spinner_item);
    }

    public CustomSpinnerAdapter(@NonNull Context context, @NonNull List<T> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        super.setDropDownViewResource(R.layout.spinner_item);
    }
}
