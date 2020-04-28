package de.domjos.myarchivemobile.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.model.tasks.ExtendedStatusTask;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.general.Company;
import de.domjos.myarchivelibrary.model.general.Person;
import de.domjos.myarchivelibrary.model.media.CustomField;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class LoadingTask<T> extends ExtendedStatusTask<Void, T> {
    private T test;
    private MediaFilter mediaFilter;
    private String searchString;
    private WeakReference<SwipeRefreshDeleteList> lv;
    private String key;

    public LoadingTask(Activity activity, T test, MediaFilter mediaFilter, String searchString, SwipeRefreshDeleteList lv, String key) {
        super(activity, R.string.sys_reload, R.string.sys_reload_summary, MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, new ProgressBar(activity), new TextView(activity));

        this.key = key;
        this.lv = new WeakReference<>(lv);
        this.test = test;
        this.mediaFilter = mediaFilter;
        this.searchString = searchString;
    }

    @Override
    protected List doInBackground(Void... voids) {
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
                    baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getContext(), this.searchString, this.key);
                } else {
                    if(mediaFilter.getTitle().trim().equals(this.getContext().getString(R.string.filter_no_filter))) {
                        baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getContext(), this.searchString, this.key);
                    } else {
                        baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getContext(), this.mediaFilter, this.searchString, key);
                    }
                }
                return baseDescriptionObjects;
            } else {
                if(this.test instanceof Book) {
                    return MainActivity.GLOBALS.getDatabase().getBooks(this.searchString, MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset(key));
                }
                if(this.test instanceof Movie) {
                    return MainActivity.GLOBALS.getDatabase().getMovies(this.searchString, MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset(key));
                }
                if(this.test instanceof Album) {
                    return MainActivity.GLOBALS.getDatabase().getAlbums(this.searchString, MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset(key));
                }
                if(this.test instanceof Game) {
                    return MainActivity.GLOBALS.getDatabase().getGames(this.searchString, MainActivity.GLOBALS.getSettings().getMediaCount(), MainActivity.GLOBALS.getOffset(key));
                }
                if(this.test instanceof Person) {
                    return MainActivity.GLOBALS.getDatabase().getPersons("", 0);
                }
                if(this.test instanceof Company) {
                    return MainActivity.GLOBALS.getDatabase().getCompanies("", 0);
                }
                if(this.test instanceof MediaList) {
                    return MainActivity.GLOBALS.getDatabase().getMediaLists("", -1, MainActivity.GLOBALS.getOffset(key));
                }
                if(this.test instanceof CustomField) {
                    return MainActivity.GLOBALS.getDatabase().getCustomFields("");
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
