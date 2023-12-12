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

package de.domjos.myarchiveservices.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomExtendedStatusTask;
import de.domjos.myarchiveservices.helper.ControlsHelper;

/** @noinspection rawtypes, unchecked */
public class LoadingTask<T> extends CustomExtendedStatusTask<Void, T> {
    private final T test;
    private final MediaFilter mediaFilter;
    private final String searchString;
    private final WeakReference<SwipeRefreshDeleteList> lv;
    private final String key;
    private final Database database;
    private final int mediaCount;
    private final int offset;
    private final String orderBy;

    public LoadingTask(
            Activity activity, T test, MediaFilter mediaFilter, String searchString,
            SwipeRefreshDeleteList lv, String key, boolean notification, int icon_notification,
            Database database, int mediaCount, int offset, String orderBy) {
        super(activity, R.string.sys_reload, R.string.sys_reload_summary, notification,
                icon_notification, new ProgressBar(activity), new TextView(activity));

        this.key = key;
        this.mediaCount = mediaCount;
        this.orderBy = orderBy;
        this.offset = offset;
        this.database = database;
        this.lv = new WeakReference<>(lv);
        this.test = test;
        this.mediaFilter = mediaFilter;
        this.searchString = searchString;
    }

    @Override
    protected List<T> doInBackground(Void voids) {
        try {
            ((Activity) this.getContext()).runOnUiThread(() -> {
                this.lv.get().getAdapter().clear();
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(this.getContext().getString(R.string.sys_reload));
                baseDescriptionObject.setDescription(this.getContext().getString(R.string.sys_reload_summary));
                this.lv.get().getAdapter().add(baseDescriptionObject);
            });
            if(this.test == null) {
                List baseDescriptionObjects;
                if(mediaFilter==null) {
                    baseDescriptionObjects = ControlsHelper.getAllMediaItems(
                        this.getContext(), this.searchString, this.database,
                        this.mediaCount, this.offset, this.orderBy
                    );
                } else {
                    if(mediaFilter.getTitle().trim().equals(this.getContext().getString(R.string.filter_no_filter))) {
                        baseDescriptionObjects = ControlsHelper.getAllMediaItems(
                            this.getContext(), this.searchString, this.database,
                            this.mediaCount, this.offset, this.orderBy
                        );
                    } else {
                        baseDescriptionObjects = ControlsHelper.getAllMediaItems(
                            this.getContext(), this.mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                        );
                    }
                }
                return baseDescriptionObjects;
            } else {
                if(this.test instanceof Book) {
                    MediaFilter mediaFilter = new MediaFilter();
                    mediaFilter.setBooks(true);
                    mediaFilter.setMovies(false);
                    mediaFilter.setGames(false);
                    mediaFilter.setMusic(false);
                    return (List<T>) ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Movie) {
                    MediaFilter mediaFilter = new MediaFilter();
                    mediaFilter.setBooks(false);
                    mediaFilter.setMovies(true);
                    mediaFilter.setGames(false);
                    mediaFilter.setMusic(false);
                    return (List<T>) ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Album) {
                    MediaFilter mediaFilter = new MediaFilter();
                    mediaFilter.setBooks(false);
                    mediaFilter.setMovies(false);
                    mediaFilter.setGames(false);
                    mediaFilter.setMusic(true);
                    return (List<T>) ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Game) {
                    MediaFilter mediaFilter = new MediaFilter();
                    mediaFilter.setBooks(false);
                    mediaFilter.setMovies(false);
                    mediaFilter.setGames(true);
                    mediaFilter.setMusic(false);
                    return (List<T>) ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Person) {
                    return (List<T>) this.database.getPersons(this.searchString);
                }
                if(this.test instanceof Company) {
                    return (List<T>) this.database.getCompanies(this.searchString);
                }
                if(this.test instanceof MediaList) {
                    return (List<T>) this.database.getMediaLists(this.searchString, -1, this.offset);
                }
                if(this.test instanceof CustomField) {
                    return (List<T>) this.database.getCustomFields(this.searchString);
                }
                if(this.test instanceof de.domjos.myarchivelibrary.model.base.BaseDescriptionObject) {
                    if(this.key.equals(this.getContext().getString(R.string.media_general_tags).toLowerCase())) {
                        return (List<T>) this.database.getBaseObjects("tags", "", 0, this.searchString);
                    } else {
                        return (List<T>) this.database.getBaseObjects("categories", "", 0, this.searchString);
                    }
                }
            }
        } catch (Exception ex) {
            super.printException(ex);
        } finally {
            ((Activity) this.getContext()).runOnUiThread(() -> this.lv.get().getAdapter().clear());
        }

        return null;
    }
}
