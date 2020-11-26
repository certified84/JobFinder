package com.certified.jobfinder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MyWorker extends Worker {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private NotificationManager mNotifyManager;
    private static final int BUDGET_NOTIFICATION_ID = 0;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        displayNotification();
        return Result.success();
    }

    public void displayNotification() {
        NotificationManager manager = (NotificationManager) getApplicationContext()
                .getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(getApplicationContext(), BusinessActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), BUDGET_NOTIFICATION_ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String message = "A new job matches your profile";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), PRIMARY_CHANNEL_ID);
        builder.setDefaults(Notification.DEFAULT_ALL).setSmallIcon(R.drawable.ic_notifications).setContentTitle("New job alert")
                .setColor(getApplicationContext().getResources().getColor(R.color.primaryGreen))
                .setContentText(message).setTicker("Budget").setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH).setContentIntent(pendingIntent);
        manager.notify(BUDGET_NOTIFICATION_ID, builder.build());
    }
}
