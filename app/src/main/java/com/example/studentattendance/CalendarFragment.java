package com.example.studentattendance;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.studentattendance.model.Course;
import com.example.studentattendance.model.Professor;
import com.example.studentattendance.model.Student;
import com.example.studentattendance.model.Utils;
import com.example.studentattendance.service.impl.StudentServiceImpl;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class CalendarFragment extends Fragment {

    private Spinner spinner;
    private TextView add_class_txt;
    private EditText subject;
    private EditText classRoom;
    private EditText date;
    private EditText startTime, endTime;
    private Button setDate, setStartTime, setEndTime;
    private Button add_class_btn;
    FirebaseAuth firebaseAuth;
    private Professor professor;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String selectedSubject;
    private static final int REQ_CODE = 1200;

    Date data = new Date();
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("courses");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String professorJsonObject = getArguments().getString("professorObject");

        professor = Utils.getGsonParser().fromJson(professorJsonObject, Professor.class);
        return inflater.inflate(R.layout.fragment_calendar, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        add_class_txt = view.findViewById(R.id.add_class_txt);
        subject = view.findViewById(R.id.classLesson);
        classRoom = view.findViewById(R.id.classroom);
        date = view.findViewById(R.id.date);
        startTime = view.findViewById(R.id.startTime);
        endTime = view.findViewById(R.id.endTime);
        setDate = view.findViewById(R.id.setDate);
        setStartTime = view.findViewById(R.id.setStartTime);
        setEndTime = view.findViewById(R.id.setEndTime);
        add_class_btn = view.findViewById(R.id.add_class_btn);
        spinner = view.findViewById(R.id.spinner_subjects);
        selectedSubject = professor.getSubjectsTeaching().get(0);

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, professor.getSubjectsTeaching());
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View spinnerView, int position, long id) {
                selectedSubject = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                @SuppressLint("DefaultLocale")
                                String dateSet = String.format("%02d-%02d-%d", dayOfMonth, (monthOfYear + 1), year);
                                date.setText(dateSet);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String time = hourOfDay + ":" + minute;
                                startTime.setText(time);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String time = hourOfDay + ":" + minute;
                                endTime.setText(time);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        add_class_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                if (!classRoom.getText().toString().isEmpty() &&
                        !date.getText().toString().isEmpty() &&
                        !startTime.getText().toString().isEmpty() &&
                        !endTime.getText().toString().isEmpty()) {
                    String classroom = classRoom.getText().toString();

                    String enteredDate = date.getText().toString();
                    String enteredStartTime = startTime.getText().toString();
                    String enteredEndTime = endTime.getText().toString();
                    Date startDate = null;
                    Date endDate = null;
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    try {
                        startDate = formatter.parse(enteredDate + " " + enteredStartTime);
                        endDate = formatter.parse(enteredDate + " " + enteredEndTime);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //String studentVerificationCode = generateStudentVerificationCode();
                    String verCode = createCourse(selectedSubject, classroom, startDate, endDate);
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.CALENDAR_ID, verCode);
                    intent.putExtra(CalendarContract.Events.TITLE, selectedSubject);
                    intent.putExtra(CalendarContract.Events.DESCRIPTION, classroom);
                    intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startDate.getTime());
                    intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endDate.getTime());
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "Please fill the field", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private String createCourse(String subjectClass, String classroom, Date startDate, Date endDate) {
        final String[] verify = new String[1];
        StudentServiceImpl studentService = new StudentServiceImpl();
        Task<DataSnapshot> task = FirebaseDatabase.getInstance().getReference("courses").get();
        if (!task.isSuccessful()) {
            verify[0] = generateStudentVerificationCode();
        }
        List<String> verificationCodes = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("courses").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    verificationCodes.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        String studentVerificationCode = generateUniqueCode(verificationCodes, generateStudentVerificationCode());
        verify[0] = studentVerificationCode;
        Course course = new Course(studentVerificationCode,
                professor,
                subjectClass,
                classroom,
                startDate,
                endDate);

        databaseReference.child(studentVerificationCode).setValue(course);
        return verify[0];
    }

    private String generateStudentVerificationCode() {
        //generate random code for student verification
        Random rnd = new Random();
        return String.valueOf(rnd.nextInt(999999));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String generateUniqueCode(List<String> verCodes, String verCode) {
        if (verCodes.stream().anyMatch(verCode::equals)) {
            String newVerCode = generateStudentVerificationCode();
            verCode = generateUniqueCode(verCodes, newVerCode);
        }
        return verCode;
    }


}