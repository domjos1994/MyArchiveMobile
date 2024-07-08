/*
 * Copyright (C)  2019-2024 Domjos
 * This file is part of UniTrackerMobile <https://unitrackermobile.de/>.
 *
 * UniTrackerMobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * UniTrackerMobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with UniTrackerMobile. If not, see <http://www.gnu.org/licenses/>.
 */

package de.domjos.myarchivelibrary.custom;

import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** @noinspection unchecked, unused */
public abstract class AsyncTaskExecutorService<Params,Progress,Result> {

    private final ExecutorService executor;
    private Handler handler;
    private Looper looper;
    private final CompletableFuture<Result> mResult;
    protected final WeakReference<Activity> refActivity;

    protected AsyncTaskExecutorService(Activity activity) {
        this.mResult = new CompletableFuture<>();
        this.refActivity = new WeakReference<>(activity);
        this.executor = Executors.newSingleThreadExecutor();
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public Handler getHandler() {
        if (handler == null) {
            synchronized(AsyncTaskExecutorService.class) {
                HandlerThread handlerThread = new HandlerThread("BGThread");
                handlerThread.start();
                handler = new Handler(Objects.requireNonNull(handlerThread.getLooper()));
            }
        }
        return handler;
    }

    protected void onPreExecute() {
        // Override this method where ever you want to perform task before background execution get started
    }

    protected abstract Result doInBackground(Params... params);

    protected abstract void onPostExecute(Result result);

    protected void onProgressUpdate(@NotNull Progress... value) {
        // Override this method where ever you want update a progress result
    }

    // used for push progress report to UI
    public void publishProgress(@NotNull Progress... value) {
        getHandler().post(() -> onProgressUpdate(value));
    }

    public Future<Result> execute() {
        return execute((Params) null);
    }

    @SafeVarargs
    public final Future<Result> execute(Params... params) {
        getHandler().post(() -> {
            this.refActivity.get().runOnUiThread(this::onPreExecute);
            try {
                this.mResult.complete(executor.submit(() -> {
                    Result result = doInBackground(params);
                    getHandler().post(() ->
                        this.refActivity.get().runOnUiThread(()->onPostExecute(result))
                    );
                    return result;
                }).get());
            } catch (Exception e) {
                Log.e("Task", e.getLocalizedMessage(), e);
            }
        });
        return this.mResult;
    }

    public void shutDown() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    public boolean isCancelled() {
        return executor == null || executor.isTerminated() || executor.isShutdown();
    }
}
