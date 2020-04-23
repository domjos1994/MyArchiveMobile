package de.domjos.myarchivemobile.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.model.tasks.StatusTask;
import de.domjos.customwidgets.model.tasks.TaskStatus;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.PDFWriterHelper;

public class ExportTask extends StatusTask<Void, Void> {
    private WeakReference<TextView> lblState;
    private int max;
    private String path;
    private List<BaseMediaObject> baseMediaObjects;

    public ExportTask(Activity activity, String path, ProgressBar pbProgress, TextView lblState, TextView lblMessage, List<BaseMediaObject> baseMediaObjects) {
        super(activity, R.string.settings_general_database_export, R.string.settings_general_database_export, MainActivity.GLOBALS.getSettings().isNotifications(), R.mipmap.ic_launcher_round, pbProgress, lblMessage);

        this.path = path;
        this.baseMediaObjects = baseMediaObjects;
        this.max = baseMediaObjects.size();
        this.lblState = new WeakReference<>(lblState);
    }

    @Override
    protected final void onProgressUpdate(TaskStatus... values) {
        super.onProgressUpdate(values);
        this.lblState.get().setText(String.format("%s / %s", this.max, values[0].getStatus()));
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
