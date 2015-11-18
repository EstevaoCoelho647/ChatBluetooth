package com.project.estevao.chatbluetooth;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.NotificationCompat;

import junit.framework.Test;

/**
 * Created by C1284520 on 18/11/2015.
 */
abstract class Helper {

    private static int count = 0;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification(Activity context, int notificationId,
                                        String title, String content) {
        long when = System.currentTimeMillis();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, BluetoothChat.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification;

        notification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(content)
                .setNumber(++count)
                .setSmallIcon(R.drawable.ic_button_send)
                .setContentIntent(contentIntent)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults |= Notification.DEFAULT_SOUND;
        nm.notify(0, notification);
        count=0;


    }


}


