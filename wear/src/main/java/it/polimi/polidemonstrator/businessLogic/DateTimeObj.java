package it.polimi.polidemonstrator.businessLogic;

/**
 * Created by saeed on 5/20/2016.
 */

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class DateTimeObj {

    public DateTimeObj() {
        // TODO Auto-generated constructor stub
    }
    public static String getCurrentDate(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd");
        Calendar c = Calendar.getInstance();
        String date=dfDate.format(c.getTime());
        return date;
    }

    public static String getCurrentYear(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy");
        Calendar c = Calendar.getInstance();
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }

    public static String getCurrentMonth(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("MM");
        Calendar c = Calendar.getInstance();
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }


    public static String getCurrentDateBeginningMidnight(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
        Calendar c = Calendar.getInstance();
        String date=dfDate.format(c.getTime());
        return date;
    }

    public static String getCurrentDateEndingMidnight(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 23:45");
        Calendar c = Calendar.getInstance();
        String date=dfDate.format(c.getTime());
        return date;
    }

    public static String getCurrentDateTime(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Calendar c = Calendar.getInstance();
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }

    public static long getCurrentDateTimeInMili(){
        long dateTimeMili=System.currentTimeMillis();
        return dateTimeMili;
    }




    public static String get7daysBeforeCurrentDateTime(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -6);
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }

    public static String getFirstDayOfCurrentMonth(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }

    public static String getFirstDayOfCurrentYear(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }


    public static Integer getYear(long timeStamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int mYear = calendar.get(Calendar.YEAR);
        return  mYear;
    }
    public static Integer getMonth(long timeStamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int mMonth = calendar.get(Calendar.MONTH);
        return  mMonth;
    }

    public static Integer getDayofWeek(long timeStamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int mDay = calendar.get(Calendar.DAY_OF_WEEK);
        return  mDay;
    }

    public static String getTime(long timeStamp)
    {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        df.setTimeZone(TimeZone.getDefault());
        String mTime=df.format(timeStamp);
        return  mTime;
    }


    public static String getCurrentTimeHour()
    {
        SimpleDateFormat df = new SimpleDateFormat("HH");
        Calendar c = Calendar.getInstance();
        String date=df.format(c.getTime());
        return date;
    }

    public static String getCurrentTimeMin()
    {
        SimpleDateFormat df = new SimpleDateFormat("mm");
        Calendar c = Calendar.getInstance();
        String date=df.format(c.getTime());
        return date;
    }

    public static String[] getTimeRangeForTwoHours( ){
        String currentHour=getCurrentTimeHour();
        int currentHourvalue=Integer.valueOf(currentHour);
        int start=currentHourvalue-1;
        if(start<0){
            start=0;
        }

        String startHour= String.valueOf(start);
        String endHour=String.valueOf(currentHourvalue+1);
        if(startHour.length() == 1){
            startHour="0"+startHour;
        }
        if(endHour.length() == 1){
            endHour="0"+endHour;
        }

        String[] result =new String[]{startHour,endHour};

        return result;
    }



    public static String getMonthDayTime(long timeStamp)
    {
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, HH:mm");
        df.setTimeZone(TimeZone.getDefault());
        String mTime=df.format(timeStamp);
        return  mTime;
    }
    public static Integer getMinute(long timeStamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        int mMinute = calendar.get(Calendar.MINUTE);
        return  mMinute;
    }

    public static ArrayList<Long> getDateTimeMiliRange(MeasurementTimeWindow measurementTimeWindow, TimeIntervals timeInterval, String selectedDate){

        int interval=timeInterval.getTimeInterval();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Date startDate;
            Date endDate;
            if (measurementTimeWindow== MeasurementTimeWindow.Custom){
                startDate=dateFormat.parse(selectedDate +" 00:00");
                endDate=dateFormat.parse(selectedDate+" 23:45");

            }else {
                startDate=dateFormat.parse(getChartStartDateTime(measurementTimeWindow));
                endDate = dateFormat.parse(getCurrentDateTime());
            }

            long startDateTimeMili= startDate.getTime();
            long endDateTimeMili=endDate.getTime();

            ArrayList<Long> arrayListTimespans=new ArrayList<Long>();
            for (long i=startDateTimeMili;i<endDateTimeMili;i=i+interval){
                arrayListTimespans.add(i);
            }
            return arrayListTimespans;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static String getChartStartDateTime(MeasurementTimeWindow measurementTimeWindow) {
        String startChartDateTime=null;
        switch (measurementTimeWindow){
            case Today:
                startChartDateTime=getCurrentDateBeginningMidnight();
                break;
            case Last7days:
                startChartDateTime=get7daysBeforeCurrentDateTime();
                break;
            case ThisMonth:
                startChartDateTime=getFirstDayOfCurrentMonth();
                break;
            case ThisYear:
                startChartDateTime=getFirstDayOfCurrentYear();
                break;

        }
        return startChartDateTime;
    }



    //here define Enumerations
    public enum TimeIntervals{

        OneDay (76400000),
        HalfaDay(43200000),
        QuarterDay(21600000),
        OneEighthDay(10800000),
        OneHour(3600000),
        HalfanHour(1800000),
        FifteenMins(900000);

        public int getTimeInterval() {
            return timeInterval;
        }

        private int timeInterval;
        TimeIntervals(int i) {
            this.timeInterval=i;
        }
    }
    //this will be used in generationg API Urls
    public enum MeasurementTimeWindow{
        Today (0),
        Last7days (1),
        ThisMonth (2),
        ThisYear (3),
        Custom (4);

        public int getMeasurementTimeWindow(){return  measurementTimeWindow;}
        private int measurementTimeWindow;
        MeasurementTimeWindow(int i) {this.measurementTimeWindow=i;}

    }

}

