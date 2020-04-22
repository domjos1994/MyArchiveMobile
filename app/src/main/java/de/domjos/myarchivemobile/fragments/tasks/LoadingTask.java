package de.domjos.myarchivemobile.fragments.tasks;

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.myarchivelibrary.model.media.MediaFilter;
import de.domjos.myarchivelibrary.model.media.books.Book;
import de.domjos.myarchivelibrary.model.media.games.Game;
import de.domjos.myarchivelibrary.model.media.movies.Movie;
import de.domjos.myarchivelibrary.model.media.music.Album;
import de.domjos.myarchivelibrary.tasks.AbstractTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public class LoadingTask<T> extends AbstractTask<Void, Void, List> {
    private T test;
    private MediaFilter mediaFilter;
    private String searchString;
    private WeakReference<SwipeRefreshDeleteList> lv;

    public LoadingTask(Activity activity, T test, MediaFilter mediaFilter, String searchString, SwipeRefreshDeleteList lv) {
        super(activity, activity.getString(R.string.sys_reload), R.string.sys_reload_summary, MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);

        this.lv = new WeakReference<>(lv);
        this.test = test;
        this.mediaFilter = mediaFilter;
        this.searchString = searchString;
    }

    @Override
    protected void before() {

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
                    baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getContext(), this.searchString);
                } else {
                    if(mediaFilter.getTitle().trim().equals(this.getContext().getString(R.string.filter_no_filter))) {
                        baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getContext(), this.searchString);
                    } else {
                        baseDescriptionObjects = ControlsHelper.getAllMediaItems(this.getContext(), this.mediaFilter, this.searchString);
                    }
                }
                return baseDescriptionObjects;
            } else {
                if(this.test instanceof Book) {
                    return MainActivity.GLOBALS.getDatabase().getBooks(this.searchString);
                }
                if(this.test instanceof Movie) {
                    return MainActivity.GLOBALS.getDatabase().getMovies(this.searchString);
                }
                if(this.test instanceof Album) {
                    return MainActivity.GLOBALS.getDatabase().getAlbums(this.searchString);
                }
                if(this.test instanceof Game) {
                    return MainActivity.GLOBALS.getDatabase().getGames(this.searchString);
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
