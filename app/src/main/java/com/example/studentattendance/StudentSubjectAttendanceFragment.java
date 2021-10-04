package com.example.studentattendance;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.studentattendance.enumeration.Type;
import com.example.studentattendance.model.Student;
import com.example.studentattendance.service.StudentService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;


public class StudentSubjectAttendanceFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private TableLayout tableLayout;
    private Student student;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_subject_attendance, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        tableLayout = view.findViewById(R.id.tableForAttendance);

        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String type = snapshot.child(firebaseAuth.getCurrentUser().getUid())
                        .child("type").getValue(String.class);

                if (type.equals(Type.STUDENT.toString())) {
                    student = snapshot.child(firebaseAuth.getCurrentUser().getUid()).getValue(Student.class);
                    addRows(student, view);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addRows(Student student, View view) {
        TableRow tableRow1 = (TableRow) LayoutInflater.from(view.getContext()).inflate(R.layout.student_subject_attendance, null);
        tableLayout.addView(tableRow1);
        System.out.println(tableLayout);
        for (Map.Entry<String, Integer> subjectAttendance : student.getSubjectsListening().entrySet()) {
            TableRow tableRow = (TableRow) LayoutInflater.from(view.getContext()).inflate(R.layout.student_subject_att_row, null);
            ((TextView) tableRow.findViewById(R.id.subject_name_view)).setText(subjectAttendance.getKey());
            ((TextView) tableRow.findViewById(R.id.subject_attendance_view)).setText(String.valueOf(subjectAttendance.getValue()));
            tableLayout.addView(tableRow);
            System.out.println(tableLayout);
        }
    }


}