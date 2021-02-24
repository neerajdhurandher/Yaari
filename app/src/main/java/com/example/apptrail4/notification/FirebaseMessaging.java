package com.example.apptrail4.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.apptrail4.Personal_Chat_Activity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savedCurrentUser = sp.getString("Current_UserID","None");

        String send = remoteMessage.getData().get("send");
        String user = remoteMessage.getData().get("user");

        FirebaseUser currentuser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentuser != null && send.equals(currentuser.getUid())){

//            if (!savedCurrentUser.equals(user)){

//                sendNormalNotification(remoteMessage);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

                    sendOAndAboveNotification(remoteMessage);
                }
                else {
                    sendNormalNotification(remoteMessage);
                }

//            }

        }



    }

    private void sendNormalNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification =  remoteMessage.getNotification();
        assert user != null;
        int i = Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent gotoChat = new Intent(this, Personal_Chat_Activity.class);
        Bundle bundle =  new Bundle();
        bundle.putString("samnevaleuserkiUid",user);
        gotoChat.putExtras(bundle);
        gotoChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this,i,gotoChat,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultsounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        assert icon != null;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentText(body)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(defaultsounduri)
                .setContentIntent(pIntent);


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
         int j = 0;
         if (i > 0){
             j = i;
         }
         notificationManager.notify(j,builder.build());


    }

    private void sendOAndAboveNotification(RemoteMessage remoteMessage) {


        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int i = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent gotoChat = new Intent(this, Personal_Chat_Activity.class);
        Bundle bundle = new Bundle();
        bundle.putString("samnevaleuserkiUid", user);
        gotoChat.putExtras(bundle);
        gotoChat.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(this, i, gotoChat, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultsounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoAndAboveNotification notification1 = new OreoAndAboveNotification(this);
        Notification.Builder builder = notification1.getONotification(title, body, pIntent, defaultsounduri, icon);


        int j = 0;
        if (i > 0) {
            j = i;
        }
        notification1.getNotificationManager().notify(j, builder.build());

    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser!= null){

            updateToken(s);
        }
    }

    private void updateToken(String tokenRefresh) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference token_ref = FirebaseDatabase.getInstance().getReference("Tokens");

        Token token = new Token(tokenRefresh);

        token_ref.child(currentUser.getUid()).setValue(token);


    }
}
