//package com.example.studentattendance.CalendarApiAsyncTask;
//
//import android.os.AsyncTask;
//import android.os.Build;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RequiresApi;
//
//import com.example.studentattendance.CalendarActivity;
//import com.example.studentattendance.CalendarFragment;
//import com.example.studentattendance.model.Course;
//import com.example.studentattendance.model.Professor;
//import com.example.studentattendance.model.Student;
//import com.example.studentattendance.service.impl.StudentServiceImpl;
//import com.google.android.gms.tasks.Task;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventAttendee;
//import com.google.api.services.calendar.model.EventDateTime;
//import com.google.api.services.calendar.model.EventReminder;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//import java.util.Random;
//
//public class CalendarApiAsyncTaskCreateEvent extends AsyncTask<Void, Void, Void> {
//
//    private CalendarActivity calendarActivity;
//    private CalendarFragment calendarFragment;
//    private Professor professor;
//    private String subject;
//    private String classRoom;
//    private Date startDate;
//    private Date endDate;
//    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("lessons");
//
//    public CalendarApiAsyncTaskCreateEvent(CalendarFragment calendarFragment,
//                                           Professor professor,
//                                           String subject,
//                                           String classRoom,
//                                           Date startDate,
//                                           Date endDate) {
//        this.calendarFragment = calendarFragment;
//        this.subject = subject;
//        this.classRoom = classRoom;
//        this.startDate = startDate;
//        this.endDate = endDate;
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    @Override
//    protected Void doInBackground(Void... voids) {
//        String verifyCode = createCourse();
//        Event event = new Event().setId(verifyCode).setSummary(subject).setDescription(classRoom);
//        DateTime startDateTime = new DateTime(startDate);
//        DateTime endDateTime = new DateTime(endDate);
//        EventDateTime start = new EventDateTime().setDateTime(startDateTime);
//        EventDateTime end = new EventDateTime().setDateTime(endDateTime);
//
//        event.setStart(start);
//        event.setEnd(end);
//        StudentServiceImpl studentService = new StudentServiceImpl();
//        List<Student> studentsListening = studentService.getStudentsBySubject(subject);
//        List<EventAttendee> subjectListening = new ArrayList<>();
//
//        //Adding students to the event
//        studentsListening.forEach(student -> {
//            EventAttendee attendee = new EventAttendee();
//            attendee.setEmail(student.getEmail());
//            attendee.setId(student.getIndex());
//            subjectListening.add(attendee);
//        });
//
//        //example to test with
//        EventAttendee attendee = new EventAttendee();
//        attendee.setId("181818").setEmail("golmanforever@gmail.com");
//        subjectListening.add(attendee);
//
//        event.setAttendees(subjectListening);
//
//        EventReminder[] reminderOverrides = new EventReminder[] {
//                new EventReminder().setMethod("email").setMinutes(24 * 60),
//                new EventReminder().setMethod("popup").setMinutes(10),
//        };
//        Event.Reminders reminders = new Event.Reminders()
//                .setUseDefault(false)
//                .setOverrides(Arrays.asList(reminderOverrides));
//        event.setReminders(reminders);
//
//        try {
//            System.out.println(calendarFragment.mService);
//            event = calendarFragment.mService.events().insert("primary",event).execute();
//            Toast.makeText(calendarFragment.getContext(), event.getHtmlLink(), Toast.LENGTH_SHORT).show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private String createCourse(){
//        final String[] verify = new String[1];
//        StudentServiceImpl studentService = new StudentServiceImpl();
//        Task<DataSnapshot> task = FirebaseDatabase.getInstance().getReference("lessons").get();
//        if(!task.isSuccessful()){
//            verify[0] = generateStudentVerificationCode();
//        }
//        FirebaseDatabase.getInstance().getReference("lessons").addValueEventListener(new ValueEventListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                String studentVerificationCode = generateStudentVerificationCode();
//                List<String> verificationCodes = new ArrayList<>();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    verificationCodes.add(dataSnapshot.getKey());
//                }
//                studentVerificationCode = generateUniqueCode(verificationCodes,studentVerificationCode);
//                verify[0] = studentVerificationCode;
//                Course course = new Course(studentVerificationCode,
//                        professor,
//                        subject,
//                        classRoom,
//                        studentService.getStudentsBySubject(subject),
//                        startDate,
//                        endDate);
//
//                databaseReference.child(studentVerificationCode).setValue(course);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        return verify[0];
//    }
//
//    private String generateStudentVerificationCode() {
//        //generate random code for student verification
//        Random rnd = new Random();
//        return String.valueOf(rnd.nextInt(999999));
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    private String generateUniqueCode(List<String> verCodes, String verCode){
//        if(verCodes.stream().anyMatch(verCode::equals)){
//            String newVerCode =  generateStudentVerificationCode();
//            verCode = generateUniqueCode(verCodes,newVerCode);
//        }
//        return verCode;
//    }
//}
