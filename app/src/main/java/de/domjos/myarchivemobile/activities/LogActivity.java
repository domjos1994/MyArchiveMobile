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

package de.domjos.myarchivemobile.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchiveservices.customTasks.CustomAsyncTask;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public final class LogActivity extends AbstractActivity {
    private ImageButton cmdSaveLogFile;
    private ActivityResultLauncher<Intent> emptyCallback;

    public LogActivity() {
        super(R.layout.log_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.log_activity);
    }

    @Override
    protected void initActions() {
        this.cmdSaveLogFile.setOnClickListener(view -> {
            FilePickerDialog dialog = ControlsHelper.openFilePicker(false, true, new LinkedList<>(), LogActivity.this);
            dialog.setDialogSelectionListener(files -> {
                if(files != null && files.length != 0) {
                    try {
                        this.getContent(item -> this.writeToFile(files[0], item));
                    } catch (Exception ex) {
                        MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
                    }
                }
            });
            dialog.show();
        });
    }

    @Override
    protected void initControls() {
        try {
            this.cmdSaveLogFile = this.findViewById(R.id.cmdSaveLogFile);

            WebView txtLogContent = this.findViewById(R.id.txtLogContent);

            this.getContent(item -> {
                String encodedHTML = Base64.encodeToString(("<html><body>" + item + "</body></html>").getBytes(), Base64.NO_PADDING);
                txtLogContent.loadData(encodedHTML, "text/html", "base64");
                ControlsHelper.checkNetwork(this);
            });
            this.initCallBacks();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
        }
    }

    private void initCallBacks() {
        this.emptyCallback = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), (result) -> {});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.menMainLog).setVisible(MainActivity.GLOBALS.getSettings(this.getApplicationContext()).isDebugMode());
        menu.findItem(R.id.menMainScanner).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ControlsHelper.onOptionsItemsSelected(item, this,
                emptyCallback, emptyCallback, emptyCallback, emptyCallback, emptyCallback
        );
        return super.onOptionsItemSelected(item);
    }

    public static class Task extends CustomAsyncTask<Void, Void, List<String>> {
        private final OnFinish onFinish;

        public Task(OnFinish onFinish) {
            super();

            this.onFinish = onFinish;
        }

        @Override
        protected List<String> doInBackground(Void voids) {
            List<String> content = new LinkedList<>();
            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.add(line);
                    }
                }
            } catch (IOException ex) {
                this.shutDown();
            }
            return content;
        }

        @Override
        protected void onPostExecute(List<String> items) {
            super.onPostExecute(items);

            StringBuilder content = new StringBuilder();
            for(String line : items) {
                content.append(line);
                content.append("\n");
            }
            if(items.size() != 0) {
                this.onFinish.onUpdate(items.get(0));
            }
        }

        @FunctionalInterface
        public interface OnFinish {
            void onUpdate(String result);
        }
    }

    private void getContent(Task.OnFinish onFinish) {
        Task task = new Task(onFinish);
        task.execute();
    }

    private void writeToFile(String folder, String data) {
        try {
            try (FileOutputStream fileOutputStream = new FileOutputStream(folder + File.separatorChar + "log.txt");
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream)) {

                outputStreamWriter.write(data);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
        }
    }
}
