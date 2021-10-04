package com.example.studentattendance;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.studentattendance.adapter.CoursesListAdapter;
import com.example.studentattendance.model.Course;
import com.example.studentattendance.model.Professor;
import com.example.studentattendance.model.Utils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CoursesListProfessorFragment extends Fragment {


    private RecyclerView recyclerView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference("courses");
    private Professor professor;
    private Button back_home_button;
    private final int MY_CAL_REQ_READ = 1001;
    private final int MY_CAL_REQ_WRITE = 1002;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments() != null) {
            String professorJsonObject = getArguments().getString("professorObject");

            professor = Utils.getGsonParser().fromJson(professorJsonObject, Professor.class);
        }
        return inflater.inflate(R.layout.fragment_courses_list_professor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CoursesListAdapter coursesListAdapter = new CoursesListAdapter();
        back_home_button = view.findViewById(R.id.back_to_home);
        back_home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(CoursesListProfessorFragment.this)
                        .navigate(R.id.action_CourseListProfessorFragment_to_HomeFragment);
            }
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.course_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(coursesListAdapter);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ_READ);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CALENDAR}, MY_CAL_REQ_WRITE);
        }

        databaseReference.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Course> courseList = new ArrayList<>();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Course course = item.getValue(Course.class);
                    courseList.add(course);
                }
                List<Course> professorCourses = filterProfessorCourses(courseList);
                coursesListAdapter.updateData(professorCourses);
//                recyclerView.setAdapter(coursesListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        coursesListAdapter.setCourseClickListener(new CoursesListAdapter.CourseClickListener() {
            @Override
            public void onCourseClicked(int position) {
                Bundle bundle = new Bundle();
                String courseJsonString = Utils.getGsonParser().toJson(coursesListAdapter.getCourse(position));
                bundle.putString("courseObject", courseJsonString);
                NavHostFragment.findNavController(CoursesListProfessorFragment.this)
                        .navigate(R.id.action_CourseListProfessorFragment_to_CourseListInfoFragment, bundle);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<Course> filterProfessorCourses(List<Course> courses) {
        return courses.stream()
                .filter(c -> c.getProfessor().getuId().equals(professor.getuId()))
                .collect(Collectors.toList());
    }
}