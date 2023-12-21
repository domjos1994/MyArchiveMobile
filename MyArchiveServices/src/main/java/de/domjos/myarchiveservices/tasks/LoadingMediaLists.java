package de.domjos.myarchiveservices.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivedatabase.model.mediaList.MediaList;
import de.domjos.myarchivedbvalidator.Database;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomExtendedStatusTask;

public class LoadingMediaLists extends CustomExtendedStatusTask<Void, MediaList> {

    private final WeakReference<SwipeRefreshDeleteList> lv;
    private final Database database;
    private final String searchString;
    private final int offset;

    public LoadingMediaLists(Activity activity, SwipeRefreshDeleteList lv, String searchString, boolean notification, int icon_notification, Database database, int offset) {
        super(activity, R.string.sys_reload, R.string.sys_reload_summary, notification,
                icon_notification, new ProgressBar(activity), new TextView(activity));

        this.database = database;
        this.offset = offset;
        this.lv = new WeakReference<>(lv);
        this.searchString = searchString;
    }

    @Override
    protected List<MediaList> doInBackground(Void voids) {
        try {
            super.getHandler().post(() -> {
                this.lv.get().getAdapter().clear();
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(this.getContext().getString(R.string.sys_reload));
                baseDescriptionObject.setDescription(this.getContext().getString(R.string.sys_reload_summary));
                this.lv.get().getAdapter().add(baseDescriptionObject);
            });
            return new ArrayList<>();
        } catch (Exception ex) {
            super.printException(ex);
        } finally {
            ((Activity) this.getContext()).runOnUiThread(() -> this.lv.get().getAdapter().clear());
        }
        return new LinkedList<>();
    }
}
