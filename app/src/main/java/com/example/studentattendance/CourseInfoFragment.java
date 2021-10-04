package com.example.studentattendance;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.studentattendance.model.Course;
import com.example.studentattendance.model.Professor;
import com.example.studentattendance.model.Student;
import com.example.studentattendance.model.Utils;
import com.google.firebase.database.FirebaseDatabase;


public class CourseInfoFragment extends Fragment {


    private Course course;
    private TextView verCodeText;
    private TextView verCode;
    private TextView professorText;
    private TextView professorName;
    private Button deleteCourseButton;
    private TableLayout tableLayout;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        String courseObject = getArguments().getString("courseObject");

        course = Utils.getGsonParser().fromJson(courseObject, Course.class);
        return inflater.inflate(R.layout.fragment_course_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        verCodeText = view.findViewById(R.id.verCodeText);
        verCode = view.findViewById(R.id.show_ver_code);
        professorText = view.findViewById(R.id.prof_name_text);
        professorName = view.findViewById(R.id.show_prof_name);
        tableLayout = view.findViewById(R.id.show_course_students);
        deleteCourseButton = view.findViewById(R.id.delete_course_button);

        deleteCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                String professorJsonString = Utils.getGsonParser().toJson(course.getProfessor());
                bundle.putString("professorObject", professorJsonString);
                FirebaseDatabase.getInstance().getReference("courses").child(course.getVerificationCode()).setValue(null);
                NavHostFragment.findNavController(CourseInfoFragment.this)
                        .navigate(R.id.action_CourseListInfoFragment_to_CourseListProfessorFragment,bundle);
            }
        });

        verCode.setText(course.getVerificationCode());
        String professorFullName = String.format("%s %s",course.getProfessor().getName(),course.getProfessor().getLastName());
        professorName.setText(professorFullName);

        TableRow tableRow = (TableRow) LayoutInflater.from(view.getContext())
                .inflate(R.layout.student_attend_to_course_row_header, null);
        tableLayout.addView(tableRow);
        addAllStudentsInCourse();
    }

    private void addAllStudentsInCourse() {
        TableRow tableRow = (TableRow) LayoutInflater.from(view.getContext()).inflate(R.layout.student_attend_to_course_row, null);
        for (int i = 0;i<course.getStudentList().size();i++) {
            Student student = course.getStudentList().get(i);
            ((TextView) tableRow.findViewById(R.id.tableNameRow)).setText(student.getName());
            ((TextView) tableRow.findViewById(R.id.tableSurnameRow)).setText(student.getLastName());
            ((TextView) tableRow.findViewById(R.id.tableIndexRow)).setText(student.getIndex());
            tableLayout.addView(tableRow);
        }

    }
}