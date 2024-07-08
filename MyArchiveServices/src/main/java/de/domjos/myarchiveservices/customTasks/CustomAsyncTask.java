package de.domjos.myarchiveservices.customTasks;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class CustomAsyncTask<Params, Progress, Result > {
    protected Context context;

    private final ExecutorService executor;
    private Handler handler;

    protected CustomAsyncTask() {
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            return thread;
        });
    }

    public ExecutorService getExecutor() {
        return this.executor;
    }

    public Handler getHandler() {
        if (this.handler == null) {
            synchronized(CustomAsyncTask.class) {
                this.handler = new Handler(Looper.getMainLooper());
            }
        }
        return this.handler;
    }

    protected void onPreExecute() {
        // Override this method whereever you want to perform task before background execution get started
    }

    protected abstract Result doInBackground(Params params);

    protected void onPostExecute(Result result) {
    }

    protected void onProgressUpdate(@NotNull Progress value) {
        // Override this method whereever you want update a progress result
    }

    // used for push progress resport to UI
    public void publishProgress(@NotNull Progress value) {
        getHandler().post(() -> onProgressUpdate(value));
    }

    public void execute() {
        execute(null);
    }

    public void execute(Params params) {
        getHandler().post(this::onPreExecute);
        this.executor.execute(() -> {
            Result result = doInBackground(params);
            getHandler().post(() -> onPostExecute(result));
        });
    }

    public void shutDown() {
        if (this.executor != null) {
            this.executor.shutdownNow();
        }
    }

    public boolean isCancelled() {
        return this.executor == null || this.executor.isTerminated() || this.executor.isShutdown();
    }
}
