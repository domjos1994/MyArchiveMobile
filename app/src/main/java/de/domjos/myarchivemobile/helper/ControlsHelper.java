package de.domjos.myarchivemobile.helper;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.viewpager.widget.ViewPager;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.AbstractPagerAdapter;
import de.domjos.myarchivemobile.fragments.ParentFragment;

public class ControlsHelper {

    public static void initTabs(TabLayout tabLayout, ViewPager viewPager) {
        /*for(int i = 0; i<=tabLayout.getTabCount()-1; i++) {
            tabLayout.setScrollPosition(i, 0f, true);
            viewPager.setCurrentItem(i);
        }
        tabLayout.setScrollPosition(0, 0f, true);
        viewPager.setCurrentItem(0);*/
    }

    @SuppressWarnings("deprecation")
    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) {
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else {
                    return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
                }
            } else {
                return false;
            }
        }
        return true;
    }


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
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getAlbums(where).get(0);
                    }
                    if(emptyObject instanceof Movie) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getMovies(where).get(0);
                    }
                    if(emptyObject instanceof Game) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getGames(where).get(0);
                    }
                    if(emptyObject instanceof Book) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getBooks(where).get(0);
                    }
                    if(baseMediaObject != null) {
                        for(int i = 0; i<=lv.getAdapter().getItemCount()-1; i++) {
                            BaseDescriptionObject baseDescriptionObject = lv.getAdapter().getItem(i);
                            if(((BaseMediaObject)baseDescriptionObject.getObject()).getId()==baseMediaObject.getId()) {
                                currentObject = baseDescriptionObject;
                                break;
                            }
                        }
                        fragment.changeMode(false, true);
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

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, String search) throws ParseException {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getBooks(search)) {
            BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.book));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getMovies(search)) {
            BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.movie));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getAlbums(search)) {
            BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.album));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getGames(search)) {
            BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
            baseDescriptionObject.setCover(baseMediaObject.getCover());
            baseDescriptionObject.setDescription(context.getString(R.string.game));
            baseDescriptionObject.setObject(baseMediaObject);
            baseDescriptionObjects.add(baseDescriptionObject);
        }
        return baseDescriptionObjects;
    }

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, MediaFilter mediaFilter) throws ParseException {
        List<BaseDescriptionObject> baseDescriptionObjects = new LinkedList<>();
        StringBuilder where = new StringBuilder();
        if(!mediaFilter.getSearch().trim().isEmpty()) {
            String search = mediaFilter.getSearch();
            where.append("title like '").append(search.replace("|", "' or title like '").replace("&", "' and title like '")).append("'");
        }
        List<String> categories = new LinkedList<>();
        if(mediaFilter.getCategories().contains("|")) {
            for(String category : mediaFilter.getCategories().split("\\|")) {
                if(!category.trim().isEmpty()) {
                    categories.add(category);
                }
            }
        }
        List<List<String>> tags = new LinkedList<>();
        for(String orTags : mediaFilter.getTags().split("\\|")) {
            if(!orTags.trim().isEmpty()) {
                List<String> tmpTags = new LinkedList<>();
                for(String andTags : orTags.split("&")) {
                    if(!andTags.trim().isEmpty()) {
                        tmpTags.add(andTags.trim());
                    }
                }
                tags.add(tmpTags);
            }
        }

        if(mediaFilter.isBooks()) {
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getBooks(where.toString())) {
                if(isValidItemForFilter(baseMediaObject, categories, tags)) {
                    BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                    baseDescriptionObject.setDescription(context.getString(R.string.book));
                    baseDescriptionObject.setObject(baseMediaObject);
                    baseDescriptionObjects.add(baseDescriptionObject);
                }
            }
        }
        if(mediaFilter.isMovies()) {
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getMovies(where.toString())) {
                if(isValidItemForFilter(baseMediaObject, categories, tags)) {
                    BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                    baseDescriptionObject.setDescription(context.getString(R.string.movie));
                    baseDescriptionObject.setObject(baseMediaObject);
                    baseDescriptionObjects.add(baseDescriptionObject);
                }
            }
        }
        if(mediaFilter.isMusic()) {
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getAlbums(where.toString())) {
                if(isValidItemForFilter(baseMediaObject, categories, tags)) {
                    BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                    baseDescriptionObject.setDescription(context.getString(R.string.album));
                    baseDescriptionObject.setObject(baseMediaObject);
                    baseDescriptionObjects.add(baseDescriptionObject);
                }
            }
        }
        if(mediaFilter.isGames()) {
            for(BaseMediaObject baseMediaObject : MainActivity.GLOBALS.getDatabase().getGames(where.toString())) {
                if(isValidItemForFilter(baseMediaObject, categories, tags)) {
                    BaseDescriptionObject baseDescriptionObject = ControlsHelper.setItem(context, baseMediaObject);
                    baseDescriptionObject.setCover(baseMediaObject.getCover());
                    baseDescriptionObject.setDescription(context.getString(R.string.game));
                    baseDescriptionObject.setObject(baseMediaObject);
                    baseDescriptionObjects.add(baseDescriptionObject);
                }
            }
        }

        return baseDescriptionObjects;
    }


    private static boolean isValidItemForFilter(BaseMediaObject baseMediaObject, List<String> categories, List<List<String>> tags) {
        if(!categories.isEmpty()) {
            if(baseMediaObject.getCategory() == null) {
                return false;
            } else {
                boolean contains = false;
                for(String category : categories) {
                    if(baseMediaObject.getCategory().getTitle().trim().equals(category)) {
                        contains = true;
                        break;
                    }
                }
                if(!contains) {
                    return false;
                }
            }
        }

        if(!tags.isEmpty()) {
            if(baseMediaObject.getTags().isEmpty()) {
                return false;
            } else {
                List<String> titles = new LinkedList<>();
                for(de.domjos.myarchivelibrary.model.base.BaseDescriptionObject baseDescriptionObject : baseMediaObject.getTags()) {
                    titles.add(baseDescriptionObject.getTitle().trim());
                }

                boolean contains = false;
                for(List<String> andTags : tags) {
                    boolean andContains = true;
                    for(String tag : andTags) {
                        if(!titles.contains(tag)) {
                            andContains = false;
                            break;
                        }
                    }
                    if(andContains) {
                        contains = true;
                        break;
                    }
                }

                return contains;
            }
        }
        return true;
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
        dialogProperties.root = new File(DialogConfigs.DEFAULT_DIR);
        dialogProperties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        dialogProperties.offset = new File(DialogConfigs.DEFAULT_DIR);
        dialogProperties.extensions = extensions.toArray(new String[]{});
        return new FilePickerDialog(activity, dialogProperties);
    }
}
