package de.domjos.myarchivemobile.fragments.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.tasks.AbstractTask;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.PDFWriterHelper;

public class ExportTask extends AbstractTask<Void, TaskStatus, Void> {
    private WeakReference<ProgressBar> pbProgress;
    private WeakReference<TextView> lblState, lblMessage;
    private int max;
    private String path;
    private List<BaseMediaObject> baseMediaObjects;

    public ExportTask(Activity activity, String path, ProgressBar pbProgress, TextView lblState, TextView lblMessage, List<BaseMediaObject> baseMediaObjects) {
        super(activity, activity.getString(R.string.settings_general_database_export), R.string.settings_general_database_export, MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round);

        this.path = path;
        this.baseMediaObjects = baseMediaObjects;
        this.max = baseMediaObjects.size();
        this.pbProgress = new WeakReference<>(pbProgress);
        this.lblState = new WeakReference<>(lblState);
        this.lblMessage = new WeakReference<>(lblMessage);
    }

    @Override
    protected final void onProgressUpdate(TaskStatus... values) {
        this.pbProgress.get().setProgress((int) (values[0].status / (this.max / 100.0)));
        this.lblMessage.get().setText(values[0].message);
        this.lblState.get().setText(String.format("%s / %s", this.max, values[0].status));
    }

    @Override
    protected void before() {

    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(this.path.toLowerCase().endsWith("pdf")) {
            int i = 0;
            PDFWriterHelper pdfWriterHelper = new PDFWriterHelper(this.path, this.getContext());
            for(BaseMediaObject baseMediaObject : this.baseMediaObjects) {
                pdfWriterHelper.addRow(baseMediaObject);
                publishProgress(new TaskStatus(++i, "Import Row " + baseMediaObject.getTitle()));
            }
            pdfWriterHelper.close();
        } else {

        }
        return null;
    }
}
