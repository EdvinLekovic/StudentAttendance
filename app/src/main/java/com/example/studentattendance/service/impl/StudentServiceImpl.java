package com.example.studentattendance.service.impl;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.studentattendance.enumeration.Type;
import com.example.studentattendance.model.Professor;
import com.example.studentattendance.model.Student;
import com.example.studentattendance.service.StudentService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StudentServiceImpl implements StudentService {

    HashMap<String, List<Student>> studentsBySubjects;
    private FirebaseDatabase firebaseDatabase;

    public StudentServiceImpl() {
        this.studentsBySubjects = new HashMap<>();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        addingStudents();
    }

    private void addingStudents() {
        firebaseDatabase.getReference("users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    String type = item.child("type").getValue(String.class);
                    if (type!=null&&type.equals(Type.STUDENT.toString())) {
                        Student student = item.getValue(Student.class);
                        addingStudentsToMap(student);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addingStudentsToMap(Student student) {
        List<String> subjects = new ArrayList<>(student.getSubjectsListening().keySet());
        for (int i = 0; i < subjects.size(); i++) {
            if (studentsBySubjects.isEmpty() ||
                    !studentsBySubjects.containsKey(subjects.get(i))) {
                List<Student> students = new ArrayList<>();
                students.add(student);
                studentsBySubjects.put(subjects.get(i), students);
            } else {
                studentsBySubjects.get(subjects.get(i)).add(student);
            }
        }
    }

    public List<Student> getStudentsBySubject(String subject) {
        if (studentsBySubjects.containsKey(subject)) {
            return studentsBySubjects.get(subject);
        }
        return new ArrayList<>();
    }

    public int getNumberOfStudentsBySubject(String subject) {
        if (studentsBySubjects.containsKey(subject)) {
            return studentsBySubjects.get(subject).size();
        }
        return 0;
    }
}
