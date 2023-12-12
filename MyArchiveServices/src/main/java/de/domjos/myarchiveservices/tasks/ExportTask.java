/*
 * This file is part of the MyArchiveMobile distribution (https://github.com/domjos1994/MyArchiveMobile).
 * Copyright (c) 2020 Dominic Joas.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchiveservices.tasks;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import de.domjos.customwidgets.model.tasks.TaskStatus;
import de.domjos.myarchivelibrary.model.media.BaseMediaObject;
import de.domjos.myarchiveservices.services.TextService;
import de.domjos.myarchiveservices.R;
import de.domjos.myarchiveservices.customTasks.CustomStatusTask;
import de.domjos.myarchiveservices.pdfs.PDFWriterHelper;

public class ExportTask extends CustomStatusTask<Void, Void> {
    private final WeakReference<TextView> lblState;
    private final int max;
    private final String path;
    private final List<BaseMediaObject> baseMediaObjects;
    private final int icon_notification;

    public ExportTask(Activity activity, String path, ProgressBar pbProgress, TextView lblState, TextView lblMessage, List<BaseMediaObject> baseMediaObjects, boolean notification, int icon_notification) {
        super(activity, R.string.api_task_export, R.string.api_task_export_content, notification, icon_notification, pbProgress, lblMessage);

        this.path = path;
        this.icon_notification = icon_notification;
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
    protected Void doInBackground(Void unused) {
        try {
            int i = 0;
            if(this.path.toLowerCase().endsWith("pdf")) {
                PDFWriterHelper pdfWriterHelper = new PDFWriterHelper(this.path, this.getContext(), this.icon_notification);
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
