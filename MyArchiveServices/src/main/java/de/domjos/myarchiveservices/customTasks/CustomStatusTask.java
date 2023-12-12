package de.domjos.myarchiveservices.customTasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import de.domjos.customwidgets.model.tasks.TaskStatus;

public abstract class CustomStatusTask<Params, Result> extends CustomAbstractTask<Params, TaskStatus, Result> {
    private final WeakReference<ProgressBar> progressBar;
    private final WeakReference<TextView> status;
    private final boolean showNotifications;
    protected int max;

    public CustomStatusTask(Activity activity, int title, int content, boolean showNotifications, int icon, ProgressBar progressBar, TextView status) {
        super(activity, title, content, showNotifications, icon, true);

        this.progressBar = new WeakReference<>(progressBar);
        this.status = new WeakReference<>(status);
        this.showNotifications = showNotifications;
    }


    protected void onProgressUpdate(TaskStatus... values) {
        final int percentage = (int) (values[0].getStatus() / (this.max / 100.0));
        ((Activity) this.getContext()).runOnUiThread(()->{
            this.progressBar.get().setProgress(percentage);
            this.status.get().setText(values[0].getMessage());
        });

        if(this.showNotifications) {
            if(this.builder != null) {
                this.builder.setProgress(100, percentage, false);
                if(this.manager != null) {
                    this.manager.notify(-1, this.builder.build());
                }
            }
        }
    }
}
