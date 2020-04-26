package com.uimainon.go4lunch.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Notification.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, MyService.class));;
    }
/*
    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";
    private String restaurantName="pas de restaurant choisit";

    @Override
    public void onReceive(Context context, Intent intent) {

       restaurantName = Objects.requireNonNull(intent.getExtras()).getString("restaurant");
        System.out.println("restaurantName !!"+restaurantName);

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent myIntent  = new Intent(context, ProfileActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent , PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("It's lunch time!");
        inboxStyle.addLine(restaurantName);

        String channelId = String.valueOf(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                //.setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Go4Lunch")
                 .setContentText("It's lunch time!" +restaurantName)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                 .setStyle(inboxStyle)
                //.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentInfo("Info");

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Go4Lunch";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
      */
/*  context.unregisterReceiver(this);*//*

    }
*/

}