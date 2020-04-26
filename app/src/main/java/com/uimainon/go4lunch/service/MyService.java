package com.uimainon.go4lunch.service;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.text.ParseException;
import java.util.Calendar;

//There are three parts relevant here: (i) the creation of the service and the initialisation of a counter
// (which is used to display if the service is alive or not) (ii) onStartCommand that will start the timer which will print the value
// of the counter every second (note: it returns START_STICKY - that is used to tell Android to try not to kill the service when resources are scarce:
// note Android can ignore this)  (iii) onDestroy which will restart the service when killed. Ignore the Timer for now. Let's see the latter:
public class MyService extends Service {
    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "your_package_name.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);
    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Starting timer... "+ bi.getIntExtra("hourPref",0));
    }

    @Override
    public void onDestroy() {
        cdt.cancel();
        System.out.println("Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // String userID = intent.getStringExtra("UserID");
        int hourPref = intent.getIntExtra("hourPref",12);
        int minutePref = intent.getIntExtra("minutePref",0);
        int mondayPref = intent.getIntExtra("mondayPref",1);
        int tuesdayPref = intent.getIntExtra("tuesdayPref",1);
        int wednesdayPref = intent.getIntExtra("wednesdayPref",1);
        int thursdayPref = intent.getIntExtra("thursdayPref",1);
        int fridayPref = intent.getIntExtra("fridayPref",1);
        int saturdayPref = intent.getIntExtra("saturdayPref",0);
        int sundayPref = intent.getIntExtra("sundayPref",0);
        String message = intent.getStringExtra("messageNotification");

        DateService mDate = null;
        try {
            mDate = new DateService();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int hourNow = mDate.giveHour();
        int minuteNow = mDate.giveMinute();
        int miliSecond = (hourNow*60*60)+(minuteNow*60)*1000;

        String week= mDate.giveTheDayOfTheWeek(mDate.givedayOfWeek());
       // System.out.println(week +"      "+this.sundayPref);
       if((week.equals("SUNDAY"))&&(sundayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
       }
        if((week.equals("MONDAY"))&&(mondayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
        }
        if((week.equals("TUESDAY"))&&(tuesdayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
        }
        if((week.equals("WEDNESDAY"))&&(wednesdayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
        }
        if((week.equals("THURSDAY"))&&(thursdayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
        }
        if((week.equals("FRIDAY"))&&(fridayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
        }
        if((week.equals("SATURDAY"))&&(saturdayPref == 1)){
            startAlarmNotification(miliSecond, hourPref, minutePref, message);
        }
        return super.onStartCommand(intent, flags, startId);
        //return START_STICKY;
    }

    private void startAlarmNotification(int miliNow, int hourPref, int minutePref, String message) {
        //System.out.println("C'est partit !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        int miliNotif = (hourPref*60*60)+(minutePref*60)*1000;
        if(miliNow < miliNotif){
            cdt = new CountDownTimer((miliNotif-miliNow), 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                   // System.out.println("Countdown seconds remaining: " + millisUntilFinished / 1000);
                    bi.putExtra("countdown", millisUntilFinished);
                    sendBroadcast(bi);
                }

                @Override
                public void onFinish() {
                    System.out.println("Timer finished");
                    try {
                        startAlarm(message);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            };
            cdt.start();
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    private void startAlarm(String message) throws ParseException {
        //  create a method for scheduling the notification. This is where you set the time it:
        // SET TIME HERE
        StringBuffer passToNotification = new StringBuffer();
        DateService mDate = new DateService();
        Calendar calendar= mDate.giveFrenchCalendar();
        calendar.set(Calendar.HOUR_OF_DAY,13);
        calendar.set(Calendar.MINUTE,40);
        System.out.println("send that => "+message);
        Data data = new Data.Builder()
                .putString(NotificationWorker.TASK_DESC, String.valueOf(message))
                .build();

        final OneTimeWorkRequest simpleRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(data)
                //.setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueue(simpleRequest);
    }



}
