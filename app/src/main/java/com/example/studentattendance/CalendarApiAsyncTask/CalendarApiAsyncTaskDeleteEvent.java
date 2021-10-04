//package com.example.studentattendance.CalendarApiAsyncTask;
//
//import android.os.AsyncTask;
//
//import com.example.studentattendance.CalendarFragment;
//
//import java.io.IOException;
//
//public class CalendarApiAsyncTaskDeleteEvent extends AsyncTask<Void,Void,Void> {
//
//    private CalendarFragment calendarFragment;
//    private String eventId;
//
//    public CalendarApiAsyncTaskDeleteEvent(CalendarFragment calendarFragment, String eventId) {
//        this.calendarFragment = calendarFragment;
//        this.eventId = eventId;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        try {
//            calendarFragment.mService.events().delete("primary",eventId);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
