package de.domjos.myarchivemobile.helper;

import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

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
import de.domjos.myarchivemobile.services.LibraryService;

public class ControlsHelper {

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

    public static void scheduleJob(Context context) {
        ComponentName serviceComponent = new ComponentName(context, LibraryService.class);
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
