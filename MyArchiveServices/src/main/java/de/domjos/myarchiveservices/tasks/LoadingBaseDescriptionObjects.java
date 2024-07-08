package de.domjos.myarchiveservices.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivedatabase.model.filter.Filter;
import de.domjos.myarchivedatabase.model.general.category.Category;
import de.domjos.myarchivedatabase.model.general.tag.Tag;
import de.domjos.myarchivedatabase.model.media.album.Album;
import de.domjos.myarchivedatabase.model.media.book.Book;
import de.domjos.myarchivedatabase.model.media.game.Game;
import de.domjos.myarchivedatabase.model.media.movie.Movie;
import de.domjos.myarchivedbvalidator.Database;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomExtendedStatusTask;
import de.domjos.myarchiveservices.helper.ControlsHelper;

public class LoadingBaseDescriptionObjects<T> extends CustomExtendedStatusTask<Void, BaseDescriptionObject> {
    private final T test;
    private final Filter mediaFilter;
    private final String searchString;
    private final WeakReference<SwipeRefreshDeleteList> lv;
    private final String key;
    private final Database database;
    private final int mediaCount;
    private final int offset;
    private final String orderBy;

    public LoadingBaseDescriptionObjects(
            Activity activity, T test, Filter mediaFilter, String searchString,
            SwipeRefreshDeleteList lv, String key, boolean notification, int icon_notification,
            Database database, int mediaCount, int offset, String orderBy) {
        super(activity, R.string.sys_reload, R.string.sys_reload_summary, notification,
                icon_notification, new ProgressBar(activity), new TextView(activity));

        this.key = key;
        this.test = test;
        this.mediaCount = mediaCount;
        this.orderBy = orderBy;
        this.offset = offset;
        this.database = database;
        this.lv = new WeakReference<>(lv);
        this.mediaFilter = mediaFilter;
        this.searchString = searchString;
    }

    @Override
    protected List<BaseDescriptionObject> doInBackground(Void voids) {
        try {
            super.getHandler().post(() -> {
                this.lv.get().getAdapter().clear();
                de.domjos.customwidgets.model.BaseDescriptionObject baseDescriptionObject = new de.domjos.customwidgets.model.BaseDescriptionObject();
                baseDescriptionObject.setTitle(this.getContext().getString(R.string.sys_reload));
                baseDescriptionObject.setDescription(this.getContext().getString(R.string.sys_reload_summary));
                this.lv.get().getAdapter().add(baseDescriptionObject);
            });

            if(this.test == null) {
                List<BaseDescriptionObject> baseDescriptionObjects;
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
                    Filter mediaFilter = new Filter();
                    mediaFilter.setBooks(true);
                    mediaFilter.setMovies(false);
                    mediaFilter.setGames(false);
                    mediaFilter.setAlbums(false);

                    return ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Movie) {
                    Filter mediaFilter = new Filter();
                    mediaFilter.setBooks(false);
                    mediaFilter.setMovies(true);
                    mediaFilter.setGames(false);
                    mediaFilter.setAlbums(false);
                    return ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Album) {
                    Filter mediaFilter = new Filter();
                    mediaFilter.setBooks(false);
                    mediaFilter.setMovies(false);
                    mediaFilter.setGames(false);
                    mediaFilter.setAlbums(true);
                    return ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof Game) {
                    Filter mediaFilter = new Filter();
                    mediaFilter.setBooks(false);
                    mediaFilter.setMovies(false);
                    mediaFilter.setGames(true);
                    mediaFilter.setAlbums(false);
                    return ControlsHelper.getAllMediaItems(
                            this.getContext(), mediaFilter, this.searchString, this.key,
                            this.database, this.mediaCount, this.offset, this.orderBy
                    );
                }
                if(this.test instanceof BaseDescriptionObject) {
                    if(this.key.equals(this.getContext().getString(R.string.media_general_tags).toLowerCase())) {
                        List<Tag> data = this.database.getTags(0, "");
                        List<BaseDescriptionObject> objects = new LinkedList<>();
                        data.forEach(item -> {
                            BaseDescriptionObject obj = new BaseDescriptionObject();
                            obj.setId(item.getId());
                            obj.setTitle(item.getTitle());
                            obj.setObject(item);
                            objects.add(obj);
                        });
                        return objects;
                    } else {
                        List<Category> data = new LinkedList<>();
                        List<BaseDescriptionObject> objects = new LinkedList<>();
                        data.forEach(item -> {
                            BaseDescriptionObject obj = new BaseDescriptionObject();
                            obj.setId(item.getId());
                            obj.setTitle(item.getTitle());
                            obj.setObject(item);
                            objects.add(obj);
                        });
                        return objects;
                    }

                }
            }
        } catch (Exception ex) {
            super.printException(ex);
        } finally {
            ((Activity) this.getContext()).runOnUiThread(() -> this.lv.get().getAdapter().clear());
        }
        return new LinkedList<>();
    }
}
