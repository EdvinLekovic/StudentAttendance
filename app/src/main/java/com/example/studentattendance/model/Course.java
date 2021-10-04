package com.example.studentattendance.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course {

    //Id
    private String verificationCode;
    private Professor professor;
    private String subject;
    private String classRoom;
    private List<Student> studentList;
    private Date dateStart;
    private Date dateEnd;

    public Course(){
        studentList = new ArrayList<>();
    }

    public Course(String verificationCode,
                  Professor professor,
                  String subject,
                  String classRoom,
                  Date dateStart,
                  Date dateEnd) {
        this.verificationCode = verificationCode;
        this.professor = professor;
        this.subject = subject;
        this.classRoom = classRoom;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.studentList = new ArrayList<>();
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Student> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<Student> studentList) {
        this.studentList = studentList;
    }

    public String getClassRoom() {
        return classRoom;
    }

    public void setClassRoom(String classRoom) {
        this.classRoom = classRoom;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }
}
