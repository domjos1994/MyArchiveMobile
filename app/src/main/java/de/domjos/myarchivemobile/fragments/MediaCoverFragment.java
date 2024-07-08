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

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import de.domjos.customwidgets.utils.ConvertHelper;
import de.domjos.customwidgets.utils.Validator;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.helper.LifecycleObserver;

public class MediaCoverFragment<T> extends AbstractFragment<T> {
    private ImageButton cmdMediaCoverPhoto, cmdMediaCoverGallery;
    private ImageView ivMediaCover;
    private LifecycleObserver lifecycleObserver;

    private T object;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.lifecycleObserver = new LifecycleObserver(
                this.requireActivity().getActivityResultRegistry(),
                this.requireContext(),(bmp) -> {
            if(bmp != null) {
                this.ivMediaCover.setImageBitmap(bmp);
            }
        }, (bmp) -> {
            if(bmp != null) {
                this.ivMediaCover.setImageBitmap(bmp);
            } else {
                this.lifecycleObserver.startGallery();
            }
        });
        this.getLifecycle().addObserver(this.lifecycleObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_fragment_media_cover, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.cmdMediaCoverPhoto = view.findViewById(R.id.cmdMediaCoverPhoto);
        this.cmdMediaCoverGallery = view.findViewById(R.id.cmdMediaCoverGallery);
        this.ivMediaCover = view.findViewById(R.id.ivMediaCover);

        this.cmdMediaCoverGallery.setOnClickListener(view1 -> this.lifecycleObserver.startGallery());
        this.cmdMediaCoverPhoto.setOnClickListener(view1 -> this.lifecycleObserver.startCamera());

        this.changeMode(false);
    }

    @Override
    public void setMediaObject(T object) {
        this.object = object;

        if(this.ivMediaCover != null) {
            if(this.object instanceof BaseMediaObject baseMediaObject) {
                if(baseMediaObject.getCover() != null) {
                    this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(baseMediaObject.getCover(), 0, baseMediaObject.getCover().length));
                } else {
                    this.ivMediaCover.setImageBitmap(null);
                }
            }
            if(this.object instanceof Person person) {
                if(person.getImage() != null) {
                    this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(person.getImage(), 0, person.getImage().length));
                } else {
                    this.ivMediaCover.setImageBitmap(null);
                }
            }
            if(this.object instanceof Company company) {
                if(company.getCover() != null) {
                    this.ivMediaCover.setImageBitmap(BitmapFactory.decodeByteArray(company.getCover(), 0, company.getCover().length));
                } else {
                    this.ivMediaCover.setImageBitmap(null);
                }
            }
        }
    }

    @Override
    public T getMediaObject() {
        if(this.object instanceof BaseMediaObject && this.ivMediaCover.getDrawable()!=null) {
            try {
                ((BaseMediaObject) this.object).setCover(ConvertHelper.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }
        if(this.object instanceof Person && this.ivMediaCover.getDrawable() != null) {
            try {
                ((Person) this.object).setImage(ConvertHelper.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }
        if(this.object instanceof Company && this.ivMediaCover.getDrawable() != null) {
            try {
                ((Company) this.object).setCover(ConvertHelper.convertDrawableToByteArray(this.ivMediaCover.getDrawable()));
            } catch (Exception ignored) {}
        }

        return this.object;
    }

    @Override
    public void changeMode(boolean editMode) {
        this.cmdMediaCoverPhoto.setEnabled(editMode);
        this.cmdMediaCoverGallery.setEnabled(editMode);
        this.ivMediaCover.setEnabled(editMode);
    }

    @Override
    public Validator initValidation(Validator validator) {
        return validator;
    }
}
