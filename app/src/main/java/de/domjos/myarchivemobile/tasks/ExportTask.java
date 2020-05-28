package de.domjos.myarchivemobile.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.model.tasks.StatusTask;
import de.domjos.customwidgets.model.tasks.TaskStatus;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchivelibrary.services.TextService;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.activities.MainActivity;
import de.domjos.myarchivemobile.helper.PDFWriterHelper;

public class ExportTask extends StatusTask<Void, Void> {
    private WeakReference<TextView> lblState;
    private int max;
    private String path;
    private List<BaseMediaObject> baseMediaObjects;

    public ExportTask(Activity activity, String path, ProgressBar pbProgress, TextView lblState, TextView lblMessage, List<BaseMediaObject> baseMediaObjects) {
        super(activity, R.string.api_task_export, R.string.api_task_export_content, MainActivity.GLOBALS.getSettings().isNotifications(), R.drawable.not_export, pbProgress, lblMessage);

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
        try {
            int i = 0;
            if(this.path.toLowerCase().endsWith("pdf")) {
                PDFWriterHelper pdfWriterHelper = new PDFWriterHelper(this.path, this.getContext());
                for(BaseMediaObject baseMediaObject : this.baseMediaObjects) {
                    pdfWriterHelper.addRow(baseMediaObject);
                    publishProgress(new TaskStatus(++i, "Import Row " + baseMediaObject.getTitle()));
                }
                pdfWriterHelper.close();
            } else {
                TextService textService = new TextService(this.path);
                textService.openWriter();
                boolean header = false;
                for(BaseMediaObject baseMediaObject : this.baseMediaObjects) {
                    if(!header) {
                        header = true;
                        textService.writeHeader(baseMediaObject);
                    }
                    textService.writeLine(baseMediaObject);
                    publishProgress(new TaskStatus(++i, "Import Row " + baseMediaObject.getTitle()));
                }
                textService.closeWriter();
            }
        } catch (Exception ex) {
            super.printException(ex);
        }
        return null;
    }
}
