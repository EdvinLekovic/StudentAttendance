package com.example.studentattendance.service;

import com.example.studentattendance.model.Student;

import java.util.List;

public interface StudentService {
    List<Student> getStudentsBySubject(String subject);
    int getNumberOfStudentsBySubject(String subject);
}
