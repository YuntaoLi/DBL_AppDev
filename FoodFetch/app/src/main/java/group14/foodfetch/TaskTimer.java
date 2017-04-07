package group14.foodfetch;

import android.os.AsyncTask;

/**
 * Created by s143969 on 4/7/2017.
 * The AsyncTask check will be used for the login
 */

public class TaskTimer implements Runnable{
    private AsyncTask task;

    public TaskTimer(AsyncTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING ){
            task.cancel(true);
        }
    }
}
