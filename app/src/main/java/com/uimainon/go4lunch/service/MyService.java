package com.uimainon.go4lunch.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Objects;

public class MyService extends Service {
    private String restaurantName;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startAlarm(true,true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.restaurantName =  Objects.requireNonNull(intent.getExtras()).getString("restaurant");
        return START_NOT_STICKY;
    }

    private void startAlarm(boolean isNotification, boolean isRepeat) {
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent;
        PendingIntent pendingIntent;

        DateService mDate = null;
        try {
            mDate = new DateService();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("MyService => restaurantName "+restaurantName);
        //THIS IS WHERE YOU SET NOTIFICATION TIME FOR CASES WHEN THE NOTIFICATION NEEDS TO BE RESCHEDULED

        Calendar calendar= mDate.giveFrenchCalendar();
        calendar.set(Calendar.HOUR_OF_DAY,14);
        calendar.set(Calendar.MINUTE,57);

        myIntent = new Intent(this, Notification.class);
        myIntent.setAction("my.restaurant.string");
        myIntent.putExtra("restaurant", "Connect you !");
        //myIntent.putExtra("restaurant", this.restaurantName);
        pendingIntent = PendingIntent.getBroadcast(this,0,myIntent,0);

        if(!isRepeat)
            manager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+3000,pendingIntent);
        else
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,pendingIntent);
    }
}
