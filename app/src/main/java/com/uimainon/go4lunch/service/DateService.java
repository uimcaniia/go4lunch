package com.uimainon.go4lunch.service;

import com.uimainon.go4lunch.models.Preference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateService {

   // private Calendar mCalendar = Calendar.getInstance();
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.FRANCE);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date mDate = sdf.parse(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));

    private int mYear = cal.get(Calendar.YEAR);
    private int mMonth = cal.get(Calendar.MONTH)+1;
    private int mDay = cal.get(Calendar.DAY_OF_MONTH);

    private int mHour = cal.get(Calendar.HOUR_OF_DAY);
    private int mMinute = cal.get(Calendar.MINUTE);
    private int mSeconde = cal.get(Calendar.SECOND);

    private int nbrWeek = cal.get(Calendar.DAY_OF_WEEK);

    public DateService() throws ParseException {
    }

    // private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
   public Calendar giveFrenchCalendar(){return cal;}
    public Date giveDateToday(){return mDate;}
    public int giveYear(){
        return mYear;
    }
    public int giveMonth(){
        return mMonth;
    }
    public int giveDay(){ return mDay;}
    public int giveHour(){return mHour; }
    public int giveMinute(){return mMinute;}
    public int givedayOfWeek(){return nbrWeek;}

    public int giveTimeNow(){
        Calendar calNow = Calendar.getInstance();
        System.out.println(calNow.get(Calendar.HOUR_OF_DAY));
        System.out.println(calNow.get(Calendar.MINUTE));
        return ((calNow.get(Calendar.HOUR_OF_DAY)*60)*60) + (calNow.get(Calendar.MINUTE)*60);
    }

    public String giveTheDayOfTheWeek(int dayToday){
        String week = "";
        switch (nbrWeek) {
            case 1:  week = "SUNDAY";
                break;
            case 2:  week = "MONDAY";
                break;
            case 3:  week = "TUESDAY";
                break;
            case 4:  week = "WEDNESDAY";
                break;
            case 5:  week = "THURSDAY";
                break;
            case 6:  week = "FRIDAY";
                break;
            case 7:  week = "SATURDAY";
                break;
        }
        return week;
    }

    public String giveTheDayOfTheWeekForPreference(int dayToday){
        String week = "";
        switch (nbrWeek) {
            case 1:  week = "sunday";
                break;
            case 2:  week = "monday";
                break;
            case 3:  week = "tuesday";
                break;
            case 4:  week = "wednesday";
                break;
            case 5:  week = "thursday";
                break;
            case 6:  week = "friday";
                break;
            case 7:  week = "saturday";
                break;
        }
        return week;
    }
    public int giveThepreferenceOfDay(Preference preferenceUser, String dayToday){
        int prefOfDay = 0;
        switch (dayToday) {
            case "SUNDAY":  prefOfDay = preferenceUser.getSunday();
                break;
            case "MONDAY":  prefOfDay = preferenceUser.getMonday();
                break;
            case "TUESDAY":  prefOfDay = preferenceUser.getTuesday();
                break;
            case "WEDNESDAY":  prefOfDay = preferenceUser.getWednesday();
                break;
            case "THURSDAY":  prefOfDay = preferenceUser.getThursday();
                break;
            case "FRIDAY":  prefOfDay = preferenceUser.getFriday();
                break;
            case "SATURDAY":  prefOfDay = preferenceUser.getSaturday();
                break;
        }
        return prefOfDay;
    }

    public long giveTheAlarmTimeMilli(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
/*        cal.setTimeInMillis(System.currentTimeMillis());*/
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal.getTimeInMillis();
    }

    /**
     * retourne un format date formater "year-month-day" a partir de 3 int
     * @param year
     * @param month
     * @param day
     * @return date formater pour la comparer
     */
    public Date formatDateToCompare(int year, int month, int day){
        Date date = null;
        try {
            date = sdf.parse(year+"-"+month+"-"+day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }




}
