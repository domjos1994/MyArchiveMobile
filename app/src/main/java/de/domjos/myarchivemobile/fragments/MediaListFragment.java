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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.LibraryObject;
import de.domjos.myarchivemobile.R;

public class MediaListFragment extends AbstractFragment<List<BaseDescriptionObject>> {
    private SwipeRefreshDeleteList lvMedia, lvLibrary;
    private boolean person;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.media_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.lvMedia = view.findViewById(R.id.lvMedia);
        this.lvLibrary = view.findViewById(R.id.lvLibrary);

        this.lvMedia.setOnClickListener((SwipeRefreshDeleteList.SingleClickListener) listObject -> {
            Intent intent = new Intent();
            intent.putExtra("type", listObject.getDescription());
            intent.putExtra("id", ((BaseMediaObject) listObject.getObject()).getId());
            Activity activity = MediaListFragment.this.getActivity();
            if(activity != null) {
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    public void setPerson(boolean person) {
        this.person = person;
    }

    public void setLibraryObjects(Map<BaseMediaObject, LibraryObject> libraryObjects) {
        this.lvLibrary.getAdapter().clear();
        for(Map.Entry<BaseMediaObject, LibraryObject> libraryObject : libraryObjects.entrySet()) {
            BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
            baseDescriptionObject.setTitle(libraryObject.getKey().getTitle());

            String deadLine = "", broughtBackAt = "";
            if(libraryObject.getValue().getDeadLine() != null) {
                deadLine = ConvertHelper.convertDateToString(libraryObject.getValue().getDeadLine(), this.getString(R.string.sys_date_format));
            }
            if(libraryObject.getValue().getReturned() != null) {
                broughtBackAt = ConvertHelper.convertDateToString(libraryObject.getValue().getDeadLine(), this.getString(R.string.sys_date_format));
            }

            baseDescriptionObject.setDescription(String.format("%s - %s", deadLine, broughtBackAt).trim());
            this.lvLibrary.getAdapter().add(baseDescriptionObject);
        }
    }

    @Override
    public void setMediaObject(List<BaseDescriptionObject> baseMediaObject) {
        if(!this.person) {
            this.lvLibrary.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams)this.lvMedia.getLayoutParams()).weight = 2;
        }

        this.lvMedia.getAdapter().clear();
        for(BaseDescriptionObject baseDescriptionObject : baseMediaObject) {
            this.lvMedia.getAdapter().add(baseDescriptionObject);
        }
    }

    @Override
    public List<BaseDescriptionObject> getMediaObject() {
        return new LinkedList<>();
    }

    @Override
    public void changeMode(boolean editMode) {

    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
