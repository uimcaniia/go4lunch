package com.uimainon.go4lunch.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateService {

    private Calendar mCalendar = Calendar.getInstance();

    private int mYear = mCalendar.get(Calendar.YEAR);
    private int mMonth = mCalendar.get(Calendar.MONTH)+1;
    private int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

    private int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
    private int mMinute = mCalendar.get(Calendar.MINUTE);

    private int nbrWeek = mCalendar.get(Calendar.DAY_OF_WEEK);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public int giveYear(){
        return mYear;
    }
    public int giveMonth(){
        return mMonth;
    }
    public int giveDay(){
        return mDay;
    }
    public int giveHour(){
        return mHour;
    }
    public int giveMinute(){
        return mMinute;
    }
    public int givedayOfWeek(){return nbrWeek;}

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
    /**
     * renvoie l'heure française en prenant en compte le changement d'heure été et hivers
     * @param year année en cour
     * @param hour heure en cour
     * @param todayToCompareWithSaison date sélectionnée à comparer
     * @return l'heure française suivant todayToCompareWithSaison
     * @throws ParseException
     */
    public int giveTheGoodHourInFrance(int year, int hour, Date todayToCompareWithSaison) throws ParseException {

        Date eteInYear = sdf.parse(year+"-03-31");
        Date hiversInYear = sdf.parse(year+"-10-27");
        Date eteAfterYear = sdf.parse((year+1)+"-03-31");

        int giveGoodHour = hour;

        int changeHourEte =  todayToCompareWithSaison.compareTo(eteInYear);
        int changeHourHiver =  todayToCompareWithSaison.compareTo(hiversInYear);
        int changeHourAfter =  todayToCompareWithSaison.compareTo(eteAfterYear);

        if((changeHourEte == 0)||(changeHourEte > 0)&&(changeHourHiver < 0)){
            giveGoodHour = hour+2;
        }
        if((changeHourHiver == 0)||(changeHourHiver > 0)&&(changeHourAfter < 0)){
            giveGoodHour = hour+1;
        }
        return giveGoodHour;
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
