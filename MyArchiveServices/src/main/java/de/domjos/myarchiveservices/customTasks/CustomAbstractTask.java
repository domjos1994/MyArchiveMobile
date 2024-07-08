package de.domjos.myarchiveservices.customTasks;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import java.lang.ref.WeakReference;

import de.domjos.customwidgets.model.tasks.Receiver;
import de.domjos.customwidgets.utils.MessageHelper;

public abstract class CustomAbstractTask<Params, Progress, Result> extends CustomAsyncTask<Params, Progress, Result> {
    private final WeakReference<Context> weakReference;
    private final int icon;
    private int id = -1;
    private final String title, content;
    private final boolean showNotifications;
    private PostExecuteListener<Result> postExecuteListener;
    private PreExecuteListener preExecuteListener;
    private final boolean progress;
    private final CustomCurrentTask<Params, Progress, Result> CURRENT_TASK = new CustomCurrentTask<>();
    protected NotificationCompat.Builder builder;
    protected final NotificationManager manager;

    public CustomAbstractTask(Activity activity, int title, int content, boolean showNotifications, int icon) {
        super();

        this.manager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        this.CURRENT_TASK.setAbstractTask(this);
        this.weakReference = new WeakReference<>(activity);
        this.icon = icon;
        this.title = activity.getString(title);
        this.content = activity.getString(content);
        this.showNotifications = showNotifications;
        this.progress = false;
    }

    CustomAbstractTask(Activity activity, int title, int content, boolean showNotifications, int icon, boolean progress) {
        super();

        this.manager = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        this.CURRENT_TASK.setAbstractTask(this);
        this.weakReference = new WeakReference<>(activity);
        this.icon = icon;
        this.title = activity.getString(title);
        this.content = activity.getString(content);
        this.showNotifications = showNotifications;
        this.progress = progress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Intent intent = new Intent(this.getContext(), Receiver.class);
        intent.putExtra("id", this.id);
        if (this.showNotifications) {
            if(this.progress) {
                this.builder = MessageHelper.returnProgressNotification((Activity) this.getContext(), this.title, this.content, this.icon, this.id, intent, 100, 0);
            } else {
                MessageHelper.startProgressNotification((Activity) this.getContext(), this.title, this.content, this.icon, this.id, intent);
            }
        }

        if(this.preExecuteListener != null) {
            this.preExecuteListener.onPreExecute();
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);

        if (this.showNotifications) {
            MessageHelper.stopNotification(this.getContext(), this.id);
        }
        if (this.postExecuteListener != null) {
            this.postExecuteListener.onPostExecute(result);
        }
    }

    public void printMessage(String message) {
        ((Activity) this.getContext()).runOnUiThread(() -> MessageHelper.printMessage(message, this.icon, this.getContext()));
    }

    public void printException(Exception ex) {
        ((Activity) this.getContext()).runOnUiThread(() -> MessageHelper.printException(ex, this.icon, this.getContext()));
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void before(PreExecuteListener preExecuteListener) {
        this.preExecuteListener = preExecuteListener;
    }

    public void after(PostExecuteListener<Result> postExecuteListener) {
        this.postExecuteListener = postExecuteListener;
    }

    protected Context getContext() {
        return this.weakReference.get();
    }

    @FunctionalInterface
    public interface PostExecuteListener<Result> {
        void onPostExecute(Result result);
    }

    @FunctionalInterface
    public interface PreExecuteListener {
        void onPreExecute();
    }
}
