package com.uimainon.go4lunch.service;

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
    public int giveDay(){
        return mDay;
    }
    public int giveHour(){
        return mHour;
    }
    public int giveMinute(){return mMinute;}
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

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"), Locale.FRANCE);
        SimpleDateFormat dfm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date mDate = dfm.parse(cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH)+" "+cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND));

        return cal.get(Calendar.HOUR_OF_DAY);
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
