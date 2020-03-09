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

package de.domjos.myarchivemobile.helper;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.interfaces.DatabaseObject;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.AbstractPagerAdapter;
import de.domjos.myarchivemobile.fragments.ParentFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ControlsHelper {


    @SuppressWarnings("unchecked")
    public static <T extends BaseMediaObject> BaseDescriptionObject loadItem(Context context, ParentFragment fragment, AbstractPagerAdapter abstractPagerAdapter, BaseDescriptionObject currentObject, SwipeRefreshDeleteList lv, T emptyObject) {
        try {
            long id = -1;
            if(fragment.getArguments() != null) {
                id = fragment.getArguments().containsKey("id") ? fragment.getArguments().getLong("id") : -1;
            }

            if(id != -1) {
                if(id == 0) {
                    fragment.changeMode(true, false);
                    abstractPagerAdapter.setMediaObject(emptyObject);
                    currentObject = null;
                } else {
                    String where = "id=" + id;
                    BaseMediaObject baseMediaObject = null;
                    if(emptyObject instanceof Album) {
                        if(currentObject != null) {
                            baseMediaObject = (Album) currentObject.getObject();
                        } else {
                            baseMediaObject = MainActivity.GLOBALS.getDatabase().getAlbums(where).get(0);
                        }
                    }
                    if(emptyObject instanceof Movie) {
                        if(currentObject != null) {
                            baseMediaObject = (Movie) currentObject.getObject();
                        } else {
                            baseMediaObject = MainActivity.GLOBALS.getDatabase().getMovies(where).get(0);
                        }
                    }
                    if(emptyObject instanceof Game) {
                        if(currentObject != null) {
                            baseMediaObject = (Game) currentObject.getObject();
                        } else {
                            baseMediaObject = MainActivity.GLOBALS.getDatabase().getGames(where).get(0);
                        }
                    }
                    if(emptyObject instanceof Book) {
                        if(currentObject != null) {
                            baseMediaObject = (Book) currentObject.getObject();
                        } else {
                            baseMediaObject = MainActivity.GLOBALS.getDatabase().getBooks(where).get(0);
                        }
                    }
                    if(baseMediaObject != null) {
                        for(int i = 0; i<=lv.getAdapter().getItemCount()-1; i++) {
                            BaseDescriptionObject baseDescriptionObject = lv.getAdapter().getItem(i);
                            if(((BaseMediaObject)baseDescriptionObject.getObject()).getId()==baseMediaObject.getId()) {
                                currentObject = baseDescriptionObject;
                                break;
                            }
                        }
                        //fragment.changeMode(false, true);
                        abstractPagerAdapter.setMediaObject(baseMediaObject);
                        lv.select(currentObject);
                    }
                }
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, context);
        }
        return currentObject;
    }

    public static void scheduleJob(Context context, List<Class<? extends JobService>> classes) {
        for(Class<? extends JobService> serviceClass : classes) {
            ComponentName serviceComponent = new ComponentName(context, serviceClass);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                JobInfo.Builder builder = new JobInfo.Builder(0, serviceComponent);
                builder.setPeriodic(1000 * 60 * 60 * 24);

                JobScheduler jobScheduler;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    jobScheduler = context.getSystemService(JobScheduler.class);
                } else {
                    jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
                }

                if(jobScheduler != null) {
                    jobScheduler.schedule(builder.build());
                }
            }
        }
    }

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, String search) {
        Map<DatabaseObject, String> mp = new LinkedHashMap<>();
        mp.put(new Book(), search);
        mp.put(new Movie(), search);
        mp.put(new Album(), search);
        mp.put(new Game(), search);
        return ControlsHelper.getAllMediaItems(context, mp);
    }

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, MediaFilter mediaFilter) {
        return ControlsHelper.getAllMediaItems(context, mediaFilter, "");
    }

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, MediaFilter mediaFilter, String extendedWhere) {
        String where = "";

        if(mediaFilter.isList()) {
            try {
                List<MediaList> mediaLists = MainActivity.GLOBALS.getDatabase().getMediaLists("id=" + mediaFilter.getMediaList().getId());
                mediaFilter.setMediaList(mediaLists==null ? mediaFilter.getMediaList() : mediaLists.get(0));
            } catch (Exception ignored) {}
            if(mediaFilter.getMediaList() != null) {
                List<Long> bookIds = new LinkedList<>(), movieIds = new LinkedList<>(), musicIds = new LinkedList<>(), gameIds = new LinkedList<>();
                for(BaseMediaObject baseMediaObject : mediaFilter.getMediaList().getBaseMediaObjects()) {
                    if(baseMediaObject instanceof Book) {
                        bookIds.add(baseMediaObject.getId());
                    } else if(baseMediaObject instanceof Movie) {
                        movieIds.add(baseMediaObject.getId());
                    } else if(baseMediaObject instanceof Album) {
                        musicIds.add(baseMediaObject.getId());
                    } else {
                        gameIds.add(baseMediaObject.getId());
                    }
                }

                Map<DatabaseObject, String> mp = new LinkedHashMap<>();
                mp.put(new Book(), "id in (" + TextUtils.join(", ", bookIds) + ")");
                mp.put(new Movie(), "id in (" + TextUtils.join(", ", movieIds) + ")");
                mp.put(new Album(), "id in (" + TextUtils.join(", ", musicIds) + ")");
                mp.put(new Game(), "id in (" + TextUtils.join(", ", gameIds) + ")");
                return ControlsHelper.getAllMediaItems(context, mp);
            }
        } else {
            List<String> categories = Arrays.asList(mediaFilter.getCategories().split("\\|"));
            String categoryWhere = "category like '%" + TextUtils.join("%' or category like '%", categories) + "%'";
            if(!categories.isEmpty() && !categories.get(0).isEmpty()) {
                where = categoryWhere;
            }

            List<String> tags = Arrays.asList(mediaFilter.getTags().split("\\|"));
            String tagsWhere = "tags like '%" + TextUtils.join("%' or tags like '%", tags) + "%'";
            if(!tags.isEmpty() && !tags.get(0).isEmpty()) {
                if(where.isEmpty()) {
                    where = tagsWhere;
                } else {
                    where += " AND " + tagsWhere;
                }
            }

            List<String> customFields = Arrays.asList(mediaFilter.getCustomFields().split("\\|"));
            String customFieldsWhere = "customFields like '%" + TextUtils.join("%' or customFields like '%", customFields) + "%'";
            if(!customFields.isEmpty() && !customFields.get(0).isEmpty()) {
                if(where.isEmpty()) {
                    where = customFieldsWhere;
                } else {
                    where += " AND " + customFieldsWhere;
                }
            }
        }

        Map<DatabaseObject, String> mp = new LinkedHashMap<>();
        String fullWhere = where.trim().isEmpty() ? extendedWhere : where + " AND (" + extendedWhere + ")";
        if(mediaFilter.isBooks()) {
            mp.put(new Book(), fullWhere);
        }
        if(mediaFilter.isMovies()) {
            mp.put(new Movie(), fullWhere);
        }
        if(mediaFilter.isMusic()) {
            mp.put(new Album(), fullWhere);
        }
        if(mediaFilter.isGames()) {
            mp.put(new Game(), fullWhere);
        }
        return ControlsHelper.getAllMediaItems(context, mp);
    }

    private static List<BaseDescriptionObject> getAllMediaItems(Context context, Map<DatabaseObject, String> mp) {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getObjectList(mp)) {
            if(baseMediaObject instanceof Book) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(context.getString(R.string.book));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            if(baseMediaObject instanceof Movie) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(context.getString(R.string.movie));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            if(baseMediaObject instanceof Album) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(context.getString(R.string.album));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            if(baseMediaObject instanceof Game) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                baseDescriptionObject.setCover(baseMediaObject.getCover());
                baseDescriptionObject.setDescription(context.getString(R.string.game));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
        }

        return baseDescriptionObjects;
    }

    private static BaseDescriptionObject setItem(Context context, BaseMediaObject baseMediaObject) {
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        if(baseMediaObject.isLendOut()) {
            baseDescriptionObject.setTitle(baseMediaObject.getTitle() + " (" + context.getString(R.string.library_lendOut) + ")");
        } else {
            baseDescriptionObject.setTitle(baseMediaObject.getTitle());
        }
        return baseDescriptionObject;
    }

    public static BaseDescriptionObject convertMediaToDescriptionObject(BaseMediaObject baseMediaObject, Context context) {
        BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
        baseDescriptionObject.setTitle(baseMediaObject.getTitle());
        baseDescriptionObject.setCover(baseMediaObject.getCover());
        if(baseMediaObject instanceof Book) {
            baseDescriptionObject.setDescription(context.getString(R.string.book));
        }
        if(baseMediaObject instanceof Movie) {
            baseDescriptionObject.setDescription(context.getString(R.string.movie));
        }
        if(baseMediaObject instanceof Album) {
            baseDescriptionObject.setDescription(context.getString(R.string.album));
        }
        if(baseMediaObject instanceof Game) {
            baseDescriptionObject.setDescription(context.getString(R.string.game));
        }
        baseDescriptionObject.setObject(baseMediaObject);
        return baseDescriptionObject;
    }

    public static FilePickerDialog openFilePicker(boolean multi, boolean directory, List<String> extensions, Activity activity) {
        DialogProperties dialogProperties = new DialogProperties();
        if(multi) {
            dialogProperties.selection_mode = DialogConfigs.MULTI_MODE;
        } else {
            dialogProperties.selection_mode = DialogConfigs.SINGLE_MODE;
        }
        if(directory) {
            dialogProperties.selection_type = DialogConfigs.DIR_SELECT;
        } else {
            dialogProperties.selection_type = DialogConfigs.FILE_SELECT;
        }
        dialogProperties.extensions = extensions.toArray(new String[]{});

        return new FilePickerDialog(activity, dialogProperties, R.style.filePickerStyle);
    }

    public static void changeScreenIfEditMode(Map<SwipeRefreshDeleteList, Integer> lists, View view, Activity activity, boolean editMode) {
        int orientation = activity.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_PORTRAIT && view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            int pagerWeight = 10;
            for(Map.Entry<SwipeRefreshDeleteList, Integer> list : lists.entrySet()) {
                pagerWeight -= list.getValue();
                ((LinearLayout.LayoutParams) list.getKey().getLayoutParams()).weight = editMode ? 0 : list.getValue();
                list.getKey().setVisibility(editMode ? GONE : VISIBLE);
            }
            ((LinearLayout.LayoutParams) view.getLayoutParams()).weight = editMode ? 10 : pagerWeight;
        }
    }
}
