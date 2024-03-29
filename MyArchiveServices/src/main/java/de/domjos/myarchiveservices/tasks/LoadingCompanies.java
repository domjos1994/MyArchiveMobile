package de.domjos.myarchiveservices.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.BaseDescriptionObject;
import de.domjos.customwidgets.widgets.swiperefreshdeletelist.SwipeRefreshDeleteList;
import de.domjos.myarchivedatabase.model.general.company.Company;
import de.domjos.myarchivedbvalidator.Database;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomExtendedStatusTask;

public class LoadingCompanies extends CustomExtendedStatusTask<Void, Company> {

    private final WeakReference<SwipeRefreshDeleteList> lv;
    private final Database database;
    private final String searchString;

    public LoadingCompanies(Activity activity, SwipeRefreshDeleteList lv, String searchString, boolean notification, int icon_notification, Database database) {
        super(activity, R.string.sys_reload, R.string.sys_reload_summary, notification,
                icon_notification, new ProgressBar(activity), new TextView(activity));

        this.database = database;
        this.lv = new WeakReference<>(lv);
        this.searchString = searchString;
    }

    @Override
    protected List<Company> doInBackground(Void voids) {
        try {
            super.getHandler().post(() -> {
                this.lv.get().getAdapter().clear();
                BaseDescriptionObject baseDescriptionObject = new BaseDescriptionObject();
                baseDescriptionObject.setTitle(this.getContext().getString(R.string.sys_reload));
                baseDescriptionObject.setDescription(this.getContext().getString(R.string.sys_reload_summary));
                this.lv.get().getAdapter().add(baseDescriptionObject);
            });
            return this.database.getCompanies(0, "");
        } catch (Exception ex) {
            super.printException(ex);
        } finally {
            ((Activity) this.getContext()).runOnUiThread(() -> this.lv.get().getAdapter().clear());
        }
        return new LinkedList<>();
    }
}
