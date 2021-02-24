package com.example.apptrail4.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class OreoAndAboveNotification extends ContextWrapper {

    private static final  String ID = "com.example.apptrail4";
    private static final String NAME = "Yaari";

    private NotificationManager notificationManager;

     public OreoAndAboveNotification (Context base) {
         super(base);

         if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){

          createChannel();

         }


     }

    @TargetApi(Build.VERSION_CODES.O)

    private void createChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(ID,NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getNotificationManager().createNotificationChannel(notificationChannel);

    }
   public NotificationManager getNotificationManager(){

         if (notificationManager == null){

             notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

         }
         return notificationManager;

   }

   @TargetApi(Build.VERSION_CODES.O)
   public Notification.Builder getONotification(String title,
                                              String body ,
                                              PendingIntent pIntent,
                                              Uri sounduri,String icon){

         return new Notification.Builder(getApplicationContext(),ID)
                 .setContentIntent(pIntent).setContentTitle(title)
                         .setContentText(body).setSound(sounduri)
                         .setAutoCancel(true)
                         .setSmallIcon(Integer.parseInt(icon));


   }



}
