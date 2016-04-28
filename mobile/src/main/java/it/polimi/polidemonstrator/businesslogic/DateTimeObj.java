package it.polimi.polidemonstrator.businesslogic;

/**
 * Created by saeed on 4/1/2016.
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

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
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

    public static String get7daysBeforeFromCurrentDateTime(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-7);
        String dateTime=dfDate.format(c.getTime());
        return dateTime;
    }

    public static String getFirstDayOfMonth(){

        SimpleDateFormat dfDate  = new SimpleDateFormat("yyyy/MM/dd 00:00");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
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

    public static ArrayList<Long> getDateTimeMiliRange(String startDateTime, String endDateTime, TimeIntervals timeInterval){
       // startDateTime="2016/04/18 00:00";
       // endDateTime="2016/04/18 23:45";
        int interval=timeInterval.getTimeInterval();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");


        try {
            Date startDate = dateFormat.parse(startDateTime);
            Date endDate=dateFormat.parse(endDateTime);

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
    //here define Enumerations
   public enum TimeIntervals{

        OneDay (76400000),
        HalfaDay(43200000),
        OneHour(3600000),
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
        ThisYear (3);

        public int getMeasurementTimeWindow(){return  measurementTimeWindow;}
        private int measurementTimeWindow;
        MeasurementTimeWindow(int i) {this.measurementTimeWindow=i;}

    }

}
