package com.uimainon.go4lunch.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.uimainon.go4lunch.R;
import com.uimainon.go4lunch.controllers.activities.ProfileActivity;

import java.util.Objects;

public class Notification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String restaurantName = Objects.requireNonNull(intent.getExtras()).getString("restaurant");
        System.out.println("receive !!"+ restaurantName);

        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent myIntent  = new Intent(context, ProfileActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, myIntent , PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
    /*    inboxStyle.setBigContentTitle("It's lunch time!")

        inboxStyle.addLine(restaurantName);*/

        String channelId = String.valueOf(R.string.default_notification_channel_id);
        long when = System.currentTimeMillis();
        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, channelId)
                .setWhen(when)
                .setSmallIcon(R.drawable.ic_restaurant_menu_24px)
                .setContentTitle("It's time to lunch")
                 .setContentText(restaurantName)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                 .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(restaurantName))
                .setContentInfo("Info");

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Go4Lunch";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }
        String NOTIFICATION_TAG = "FIREBASEGO4LUNCH";
        int NOTIFICATION_ID = 007;
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());



    }


}