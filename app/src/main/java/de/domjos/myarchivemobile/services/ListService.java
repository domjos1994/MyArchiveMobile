package de.domjos.myarchivemobile.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import de.domjos.customwidgets.utils.MessageHelper;
import de.domjos.myarchivelibrary.database.Database;
import de.domjos.myarchivelibrary.model.media.MediaList;
import de.domjos.myarchivemobile.R;
import de.domjos.myarchivemobile.settings.Settings;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ListService extends JobService {
    private Database database;
    private Context context;
    private List<Integer> notifications;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        this.context = this.getApplicationContext();
        try {
            Settings settings = new Settings(this.context);
            String pwd = settings.getSetting(Settings.DB_PASSWORD, "", true);
            this.database = new Database(this.context, pwd);
            this.notifications = new LinkedList<>();
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }

        try {
            for(MediaList mediaList : this.database.getMediaLists("")) {
                this.checkListObject(mediaList);
            }
        } catch (Exception ex) {
            MessageHelper.printException(ex, R.mipmap.ic_launcher_round, this.context);
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        for(Integer id : this.notifications) {
            MessageHelper.stopNotification(this.getApplicationContext(), id);
        }
        this.database.close();
        return true;
    }

    private void checkListObject(MediaList mediaList) {
        String message = this.context.getString(R.string.lists_msg);
        boolean hasDeadLine = false;
        Calendar deadLineDate = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.setTime(new Date());
        if(mediaList.getDeadLine() != null) {
            deadLineDate.setTime(mediaList.getDeadLine());
            message = String.format(message, mediaList.getTitle());
            hasDeadLine = true;
        }
        if(!hasDeadLine) {
            return;
        }
        if(deadLineDate.get(Calendar.YEAR)==today.get(Calendar.YEAR) && deadLineDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
            this.notifications.add(MessageHelper.showNotification(this.context, message, message, R.drawable.ic_local_library_black_24dp));
        }
    }
}
