package de.domjos.myarchivemobile.helper;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.domjos.customwidgets.model.objects.BaseDescriptionObject;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.adapter.AbstractPagerAdapter;
import de.domjos.myarchivemobile.fragments.ParentFragment;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ControlsHelper {

    public static boolean hasNetwork(Context context){
        boolean have_WIFI= false;
        boolean have_MobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = Objects.requireNonNull(connectivityManager).getAllNetworkInfo();
        for(NetworkInfo info:networkInfos){
            if (info.getTypeName().equalsIgnoreCase("WIFI"))if (info.isConnected())have_WIFI=true;
            if (info.getTypeName().equalsIgnoreCase("MOBILE DATA"))if (info.isConnected())have_MobileData=true;
        }
        return have_WIFI||have_MobileData;
    }

    public static <T extends BaseMediaObject> BaseDescriptionObject loadItem(Context context, ParentFragment fragment, AbstractPagerAdapter abstractPagerAdapter, BaseDescriptionObject currentObject, SwipeRefreshDeleteList lv, T emptyObject) {
        try {
            long id = -1;
            if(fragment.getArguments() != null) {
                if(fragment.getArguments().containsKey("id")) {
                    id = fragment.getArguments().getLong("id");
                }
            }

            if(id != -1) {
                if(id == 0) {
                    fragment.changeMode(true, false);
                    abstractPagerAdapter.setMediaObject(emptyObject);
                    currentObject = null;
                } else {
                    BaseMediaObject baseMediaObject = null;
                    if(emptyObject instanceof Album) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getAlbums("id=" + id).get(0);
                    }
                    if(emptyObject instanceof Movie) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getMovies("id=" + id).get(0);
                    }
                    if(emptyObject instanceof Game) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getGames("id=" + id).get(0);
                    }
                    if(emptyObject instanceof Book) {
                        baseMediaObject = MainActivity.GLOBALS.getDatabase().getBooks("id=" + id).get(0);
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

    public static List<BaseDescriptionObject> getAllMediaItems(Context context, String search) throws Exception {
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
