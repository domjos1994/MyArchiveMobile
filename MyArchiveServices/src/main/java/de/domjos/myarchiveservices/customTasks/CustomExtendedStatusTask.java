package de.domjos.myarchiveservices.customTasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.model.tasks.ExtendedStatusTask;
import de.domjos.customwidgets.model.tasks.ExtendedTaskStatus;

public abstract class CustomExtendedStatusTask<Params, Result> extends CustomAbstractTask<Params, ExtendedTaskStatus<Result>, List<Result>> {
    private final WeakReference<ProgressBar> progressBar;
    private final WeakReference<TextView> status;
    private ExtendedStatusTask.UpdateProgressListener<Result> updateProgressListener;
    protected int max;

    public CustomExtendedStatusTask(Activity activity, int title, int content, boolean showNotifications, int icon, ProgressBar progressBar, TextView status) {
        super(activity, title, content, showNotifications, icon);

        this.progressBar = new WeakReference<>(progressBar);
        this.status = new WeakReference<>(status);
    }

    @Override
    protected final void onProgressUpdate(ExtendedTaskStatus<Result> values) {
        ((Activity) this.getContext()).runOnUiThread(()->{
            this.progressBar.get().setProgress((int) (values.getStatus() / (this.max / 100.0)));
            this.status.get().setText(values.getMessage());

            if(this.updateProgressListener != null) {
                this.updateProgressListener.onUpdate(values.getObject());
            }
        });
    }

    public void setUpdateProgressListener(ExtendedStatusTask.UpdateProgressListener<Result> updateProgressListener) {
        this.updateProgressListener = updateProgressListener;
    }

    @FunctionalInterface
    public interface UpdateProgressListener<Result> {
        void onUpdate(Result result);
    }
}
