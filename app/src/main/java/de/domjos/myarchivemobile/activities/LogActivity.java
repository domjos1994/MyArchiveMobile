package de.domjos.myarchivemobile.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.model.AbstractActivity;
import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivemobile.R;

public final class LogActivity extends AbstractActivity {

    public LogActivity() {
        super(R.layout.log_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_activity);
    }

    @Override
    protected void initActions() { }

    @Override
    protected void initControls() {
        try {
            ListView lvLogs = this.findViewById(R.id.lvLogContent);
            Task task = new Task();
            List<String> items = task.execute().get();
            ArrayAdapter<String> logAdapter = new ArrayAdapter<>(this.getApplicationContext(), android.R.layout.simple_list_item_1, items);
            lvLogs.setAdapter(logAdapter);
            logAdapter.notifyDataSetChanged();

            StringBuilder content = new StringBuilder();
            for(String line : items) {
                content.append(line);
                content.append("\n");
            }
            this.writeToFile(content.toString());
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
            } catch (Exception ex) {
                this.cancel(true);
            }
            return content;
        }
    }

    private void writeToFile(String data) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separatorChar + "log.txt");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            fileOutputStream.close();
        }
        catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, LogActivity.this);
        }
    }
}
