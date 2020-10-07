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
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.SplitPaneLayout;
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
import de.domjos.myarchivemobile.settings.Globals;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ControlsHelper {
    public static boolean fromHome = false;

    @SuppressWarnings("unchecked")
    public static <T extends BaseMediaObject> BaseDescriptionObject loadItem(Context context, ParentFragment fragment, AbstractPagerAdapter abstractPagerAdapter, BaseDescriptionObject currentObject, SwipeRefreshDeleteList lv, T emptyObject, String key) {
        try {
            long id = -1;
            if(fragment.getArguments() != null) {
                id = fragment.getArguments().containsKey("id") ? fragment.getArguments().getLong("id") : -1;
            }

            if(id != -1) {
                /*if(id == 0) {
                    fragment.changeMode(true, false);
                    abstractPagerAdapter.setMediaObject(emptyObject);
                    currentObject = null;
                } else {*/
                    String where = "id=" + id;
                    BaseMediaObject baseMediaObject = null;
                    if(emptyObject instanceof Album) {
                        if(currentObject != null) {
                            baseMediaObject = (Album) currentObject.getObject();
                        } else {
                            List<Album> baseMediaObjects = MainActivity.GLOBALS.getDatabase().getAlbums(where, MainActivity.GLOBALS.getSettings().getMediaCount(), 0);
                            if(!baseMediaObjects.isEmpty()) {
                                baseMediaObject = baseMediaObjects.get(0);
                            } else {
                                baseMediaObject = new Album();
                            }
                        }
                    }
                    if(emptyObject instanceof Movie) {
                        if(currentObject != null) {
                            baseMediaObject = (Movie) currentObject.getObject();
                        } else {
                            List<Movie> baseMediaObjects = MainActivity.GLOBALS.getDatabase().getMovies(where, MainActivity.GLOBALS.getSettings().getMediaCount(), 0);
                            if(!baseMediaObjects.isEmpty()) {
                                baseMediaObject = baseMediaObjects.get(0);
                            } else {
                                baseMediaObject = new Movie();
                            }
                        }
                    }
                    if(emptyObject instanceof Game) {
                        if(currentObject != null) {
                            baseMediaObject = (Game) currentObject.getObject();
                        } else {
                            List<Game> baseMediaObjects = MainActivity.GLOBALS.getDatabase().getGames(where, MainActivity.GLOBALS.getSettings().getMediaCount(), 0);
                            if(!baseMediaObjects.isEmpty()) {
                                baseMediaObject = baseMediaObjects.get(0);
                            } else {
                                baseMediaObject = new Game();
                            }
                        }
                    }
                    if(emptyObject instanceof Book) {
                        if(currentObject != null) {
                            baseMediaObject = (Book) currentObject.getObject();
                        } else {
                            List<Book> baseMediaObjects = MainActivity.GLOBALS.getDatabase().getBooks(where, MainActivity.GLOBALS.getSettings().getMediaCount(), 0);
                            if(!baseMediaObjects.isEmpty()) {
                                baseMediaObject = baseMediaObjects.get(0);
                            } else {
                                baseMediaObject = new Book();
                            }
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
                //}
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, context);
        }
        return currentObject;
    }

    public static void addToolbar(AppCompatActivity activity) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
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

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, String search, String key) {
        Map<DatabaseObject, String> mp = new LinkedHashMap<>();
        mp.put(new Book(), search);
        mp.put(new Movie(), search);
        mp.put(new Album(), search);
        mp.put(new Game(), search);
        return ControlsHelper.getAllMediaItems(context, mp, key);
    }

    public static <T extends BaseMediaObject> T getObject(BaseDescriptionObject baseDescriptionObject, Context context) {
        try {
            baseDescriptionObject.setId(((BaseMediaObject)baseDescriptionObject.getObject()).getId());
            if(baseDescriptionObject.getDescription().trim().equals(context.getString(R.string.book))) {
                return (T) MainActivity.GLOBALS.getDatabase().getBooks("ID=" + ((BaseMediaObject)baseDescriptionObject.getObject()).getId(), 100, 0).get(0);
            }
            if(baseDescriptionObject.getDescription().trim().equals(context.getString(R.string.movie))) {
                return (T) MainActivity.GLOBALS.getDatabase().getMovies("ID=" + ((BaseMediaObject)baseDescriptionObject.getObject()).getId(), 100, 0).get(0);
            }
            if(baseDescriptionObject.getDescription().trim().equals(context.getString(R.string.album))) {
                return (T) MainActivity.GLOBALS.getDatabase().getAlbums("ID=" + ((BaseMediaObject)baseDescriptionObject.getObject()).getId(), 100, 0).get(0);
            }
            if(baseDescriptionObject.getDescription().trim().equals(context.getString(R.string.game))) {
                return (T) MainActivity.GLOBALS.getDatabase().getGames("ID=" + ((BaseMediaObject)baseDescriptionObject.getObject()).getId(), 100, 0).get(0);
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, MediaFilter mediaFilter, String extendedWhere, String key) {
        String where = "";

        if(mediaFilter.isList()) {
            try {
                List<MediaList> mediaLists = MainActivity.GLOBALS.getDatabase().getMediaLists("id=" + mediaFilter.getMediaList().getId(), MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset(key));
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
                return ControlsHelper.getAllMediaItems(context, mp, key);
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
        return ControlsHelper.getAllMediaItems(context, mp, key);
    }

    private static List<BaseDescriptionObject> getAllMediaItems(Context context, Map<DatabaseObject, String> mp, String key) {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getObjectList(mp, MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset(key))) {
            if(baseMediaObject instanceof Book) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                try {
                    if(baseMediaObject.getCover() != null) {
                        baseDescriptionObject.setCover(baseMediaObject.getCover());
                    } else {
                        baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_book));
                    }
                } catch (OutOfMemoryError error) {
                    System.gc();
                }
                baseDescriptionObject.setDescription(context.getString(R.string.book));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            if(baseMediaObject instanceof Movie) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                if(baseMediaObject.getCover() != null) {
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                } else {
                    baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_movie));
                }
                baseDescriptionObject.setDescription(context.getString(R.string.movie));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            if(baseMediaObject instanceof Album) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                if(baseMediaObject.getCover() != null) {
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                } else {
                    baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_music));
                }
                baseDescriptionObject.setDescription(context.getString(R.string.album));
                baseDescriptionObject.setObject(baseMediaObject);
                baseDescriptionObjects.add(baseDescriptionObject);
            }
            if(baseMediaObject instanceof Game) {
                BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                if(baseMediaObject.getCover() != null) {
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                } else {
                    baseDescriptionObject.setCover(ControlsHelper.getBitmapFromVectorDrawable(context, R.drawable.icon_game));
                }
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

        FilePickerDialog filePickerDialog = new FilePickerDialog(activity, dialogProperties, R.style.filePickerStyle);
        LinearLayout linearLayout = filePickerDialog.findViewById(R.id.footer);

        return filePickerDialog;
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

    public static void navViewEditMode(boolean editMode, boolean selected, BottomNavigationView navigationView) {
        MenuItem addItem = navigationView.getMenu().findItem(R.id.cmdAdd);
        MenuItem editItem = navigationView.getMenu().findItem(R.id.cmdEdit);
        if(editMode) {
            addItem.setIcon(R.drawable.icon_cancel);
            addItem.setTitle(R.string.sys_cancel);
            editItem.setIcon(R.drawable.icon_save);
            editItem.setTitle(R.string.sys_save);

            addItem.setVisible(true);
            editItem.setVisible(true);
        } else {
            addItem.setIcon(R.drawable.icon_add);
            addItem.setTitle(R.string.sys_add);
            editItem.setIcon(R.drawable.icon_edit);
            editItem.setTitle(R.string.sys_edit);

            addItem.setVisible(true);
            editItem.setVisible(selected);
        }
    }

    public static void splitPaneEditMode(ViewGroup spl, boolean editMode) {
        if(spl != null) {
            if(spl instanceof SplitPaneLayout) {
                SplitPaneLayout layout = (SplitPaneLayout) spl;
                layout.setSplitterPositionPercent(editMode ? 0.0f : 0.4f);
            }
        }
    }

    public static void setMediaStatistics(TextView txt, String key) {
        int currentPage = MainActivity.GLOBALS.getPage(key);
        int numberOfItems = MainActivity.GLOBALS.getSettings().getMediaCount();
        int max = (currentPage + 1) * numberOfItems;

        txt.setText(String.format("%s - %s", max - numberOfItems, max));
    }

    public static String setThePage(Fragment fragment, String table, String key) {
        if(fragment.getArguments() != null && ControlsHelper.fromHome) {
            long id = fragment.getArguments().getLong("id");
            if( id != 0) {
                key = key.replace(Globals.RESET, "");
                int page = MainActivity.GLOBALS.getDatabase().getPageOfItem(table, id, MainActivity.GLOBALS.getSettings().getMediaCount());
                MainActivity.GLOBALS.setPage(page, key);
                ControlsHelper.fromHome = false;
            } else {
                key = key.replace(Globals.RESET, "");
                int page = 1;
                MainActivity.GLOBALS.setPage(page, key);
                ControlsHelper.fromHome = false;
            }
        }
        return key;
    }

    public static void loadAd(Context context) {
        if(MainActivity.GLOBALS.getSettings().showAd()) {
            InterstitialAd interstitialAd = new InterstitialAd(context);
            interstitialAd.setAdUnitId(context.getString(R.string.ad_mob_key_testing));
            // ToDo replace with interstitialAd.setAdUnitId(context.getString(R.string.ad_mob_key));
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    interstitialAd.show();
                }
            });
        }
    }

    public static void checkNetwork(Activity activity) {
        try {
            de.domjos.myarchivemobile.settings.Settings settings;
            if(MainActivity.GLOBALS.getSettings() != null) {
                settings = MainActivity.GLOBALS.getSettings();
            } else {
                settings =new de.domjos.myarchivemobile.settings.Settings(activity);
            }
            if(!settings.isNoInternet()) {
                if (!MainActivity.GLOBALS.isNetwork()) {
                    View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                    Snackbar snackbar = Snackbar.make(rootView, R.string.api_webservice_no_network_title, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.no_internet_settings, view -> {
                        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                        activity.startActivity(intent);
                    });
                    snackbar.show();
                }
            }
        } catch (Exception ignored) {}
    }

    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, drawableId);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                drawable = (DrawableCompat.wrap(Objects.requireNonNull(drawable))).mutate();
            }

            Bitmap bitmap = Bitmap.createBitmap(Objects.requireNonNull(drawable).getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;
        } catch (Exception ex) {
            return null;
        }
    }
}
