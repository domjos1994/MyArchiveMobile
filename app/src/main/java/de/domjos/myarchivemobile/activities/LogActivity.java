package de.domjos.myarchivemobile.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.helper.ControlsHelper;

public final class LogActivity extends AbstractActivity {
    private ImageButton cmdSaveLogFile;

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
                if(files != null) {
                    if(files.length != 0) {
                        try {
                            String content = this.getContent();
                            this.writeToFile(files[0], content);
                        } catch (Exception ex) {
                            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
                        }
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


            String encodedHTML = Base64.encodeToString(("<html><body>" + this.getContent() + "</body></html>").getBytes(), Base64.NO_PADDING);
            txtLogContent.loadData(encodedHTML, "text/html", "base64");
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
        }
    }

    public static class Task extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> content = new LinkedList<>();
            try {
                Process process = Runtime.getRuntime().exec("logcat -d");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.add(line);
                }
                bufferedReader.close();
            } catch (Exception ex) {
                this.cancel(true);
            }
            return content;
        }
    }

    private String getContent() throws ExecutionException, InterruptedException {
        Task task = new Task();
        List<String> items = task.execute().get();

        StringBuilder content = new StringBuilder();
        for(String line : items) {
            content.append(line);
            content.append("\n");
        }
        return content.toString();
    }

    private void writeToFile(String folder, String data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(folder + File.separatorChar + "log.txt");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            fileOutputStream.close();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
        }
    }
}
