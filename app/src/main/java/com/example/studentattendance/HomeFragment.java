package com.example.studentattendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentattendance.enumeration.Type;
import com.example.studentattendance.model.Course;
import com.example.studentattendance.model.Professor;
import com.example.studentattendance.model.Student;
import com.example.studentattendance.model.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {

    private EditText enteredCode;
    private Button verificationButton;
    private Button showStudents;
    private Button showStudentAttendance;
    private Button showCourseList;
    private Button showMap;
    private FirebaseAuth firebaseAuth;
    private View view;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Bundle bundle;
    private Student student;
    private Course course;
    private List<Course> courseList;
    private Map<String, Long> studentAttendanceMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        bundle = new Bundle();
        firebaseAuth = FirebaseAuth.getInstance();
        showMap = view.findViewById(R.id.show_map);
        showMap.setVisibility(View.INVISIBLE);
        //Professor code
        showStudents = view.findViewById(R.id.showStudents);
        showCourseList = view.findViewById(R.id.course_list_prof);
        //Don't show visibility until you verify what type of user is logged in
        showStudents.setVisibility(View.INVISIBLE);
        showCourseList.setVisibility(View.INVISIBLE);
        //Student code
        showStudentAttendance = view.findViewById(R.id.showAttendance);
        enteredCode = view.findViewById(R.id.enteredCode);
        verificationButton = view.findViewById(R.id.verificationButton);
        //Don't show visibility until you verify what type of user is logged in
        showStudentAttendance.setVisibility(View.INVISIBLE);
        enteredCode.setVisibility(View.INVISIBLE);
        verificationButton.setVisibility(View.INVISIBLE);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());
        courseList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference("courses").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String enterCode = enteredCode.getText().toString();
                for (DataSnapshot item : snapshot.getChildren()) {
                    Course course = item.getValue(Course.class);
                    courseList.add(course);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        studentAttendanceMap = new HashMap<>();
        checkWhatTypeUserIsLoggedIn();


        checkMyPermission();


        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapActivity.class));
            }
        });


        showStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_HomeFragment_to_FirstFragment);
            }
        });

        showStudentAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_HomeFragment_to_StudentSubjectAttendanceFragment);
            }
        });

        showCourseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_HomeFragment_to_Course_List_Professor_Fragment, bundle);
            }
        });


        verificationButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                if (checkEligible(courseList)) {
                    //add student to the course student list
                    if (course.getStudentList().stream().noneMatch(s -> s.getIndex().equals(student.getIndex()))) {
                        addStudentAttendance();
                    }

                    addStudentInTheCourseAttendanceList();
                    //student is eligible to listen add the attendance only if he wasn't in the course student list

                } else {
                    Toast.makeText(getContext(), "Not eligible to listen the course", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void addStudentAttendance() {

        if (studentAttendanceMap.containsKey(course.getSubject())) {

            long prevAttendances = studentAttendanceMap.get(course.getSubject());
            //Adding +1 to previous attendances
            studentAttendanceMap.put(course.getSubject(), prevAttendances + 1);
            changeStudentAttendanceMap();

            Toast.makeText(getContext(), "You are attending to the course now", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "You are not rolled to this course", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeStudentAttendanceMap() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid).child("subjectsListening").setValue(studentAttendanceMap);
    }

    private void addStudentInTheCourseAttendanceList() {

        List<Student> studentList = new ArrayList<>();
        studentList = loadStudentListInCourse(studentList);

        studentList.add(student);

        //Adding modified studentList
        FirebaseDatabase.getInstance().getReference("courses")
                .child(course.getVerificationCode())
                .child("studentList")
                .setValue(studentList);
    }

    private List<Student> loadStudentListInCourse(List<Student> studentList) {
        FirebaseDatabase.getInstance().getReference("courses")
                .child(course.getVerificationCode()).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Student> students = (List<Student>) snapshot.child("studentList").getValue();
                if (students != null) {
                    studentList.addAll(students);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return studentList;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean checkEligible(List<Course> courses) {
        String verCode = enteredCode.getText().toString();
        Date timeNow = new Date();
        //If there is verification code with entered,
        // student is not on the course student list and is on time between start and end time of the course
        Optional<Course> courseOptional =
                courses.stream()
                        .filter(c -> c.getVerificationCode().equals(verCode) &&
                                c.getDateStart().getTime() <= timeNow.getTime() && timeNow.getTime() <= c.getDateEnd().getTime())
                        .findAny();
        if (courseOptional.isPresent()) {
            //found course equal to verification code and is between start and end date
            //Get the professor location and student location to see if he is close to the classroom
            course = courseOptional.get();
            com.example.studentattendance.model.Location professorLocation = courseOptional.get().getProfessor().getLocation();
            double studentDistance =
                    Haversine.distance(student.getLocation().getLatitude(),
                            student.getLocation().getLongitude(),
                            professorLocation.getLatitude(),
                            professorLocation.getLongitude());

            return studentDistance <= 100;
        }
        return false;
    }


    private void loadStudentAttendanceMap() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<String, Long> map = (Map<String, Long>) snapshot.child("subjectsListening").getValue();
                    studentAttendanceMap.putAll(map);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void checkWhatTypeUserIsLoggedIn() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String type = snapshot.child("type").getValue(String.class);
                        showMap.setVisibility(View.VISIBLE);
                        if (type.equals(Type.STUDENT.toString())) {
                            Student st = snapshot.getValue(Student.class);
                            student = st;
                            showStudentAttendance.setVisibility(View.VISIBLE);
                            enteredCode.setVisibility(View.VISIBLE);
                            verificationButton.setVisibility(View.VISIBLE);
                            loadStudentAttendanceMap();
                        } else {
                            Professor professor = snapshot.getValue(Professor.class);

                            String professorJsonString = Utils.getGsonParser().toJson(professor);
                            bundle.putString("professorObject", professorJsonString);
                            showCourseList.setVisibility(View.VISIBLE);
                            showStudents.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void checkMyPermission() {
        Dexter.withContext(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Toast.makeText(getActivity(), "Permission granted", Toast.LENGTH_SHORT).show();
                        if (isGPSEnabled()) {
                            getLocation();
                        }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), "");
                        intent.setData(uri);
                        startActivity(intent);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                   PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isGPSEnabled() {
        LocationManager locationManager =
                (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean providerEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (providerEnabled) {
            return true;
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("GPS permissions")
                    .setMessage("GPS is required for this app to work. Please enable GPS")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getActivity(), "GPS provided", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
        return false;
    }


    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                        List<Address> addresses =
                                geocoder.getFromLocation(location.getLatitude(),
                                        location.getLongitude(), 5);
                        double longitude = addresses.get(0).getLongitude();
                        double latitude = addresses.get(0).getLatitude();
                        String addressLine = addresses.get(0).getAddressLine(0);
                        String locality = addresses.get(0).getLocality();
                        com.example.studentattendance.model.Location loc =
                                new com.example.studentattendance.model
                                        .Location(longitude, latitude, addressLine, locality);
                        System.out.println(firebaseAuth.getCurrentUser());
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(firebaseAuth.getCurrentUser().getUid())
                                .child("location").setValue(loc);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}