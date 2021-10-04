package com.example.studentattendance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.studentattendance.enumeration.Type;
import com.example.studentattendance.model.Professor;
import com.example.studentattendance.model.Student;
import com.example.studentattendance.model.Utils;
import com.example.studentattendance.service.StudentService;
import com.example.studentattendance.service.impl.StudentServiceImpl;
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
import java.util.List;
import java.util.Locale;

public class FirstFragment extends Fragment {

    private Spinner spinner;
    private TableLayout tableLayout;
    private StudentServiceImpl studentService;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth firebaseAuth;
    private final int GPS_REQUEST_CODE = 9001;
    private Professor professor;
    private Bundle bundle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bundle = new Bundle();
        spinner = view.findViewById(R.id.subjects_dropdown);
        tableLayout = view.findViewById(R.id.students_table);
        studentService = new StudentServiceImpl();
        firebaseAuth = FirebaseAuth.getInstance();


        view.findViewById(R.id.add_class_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_CalendarFragment, bundle);
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View spinnerView, int position, long id) {
                String subject = (String) parent.getItemAtPosition(position);
                tableLayout.removeAllViews();
                TableRow tableRow = (TableRow) LayoutInflater.from(view.getContext()).inflate(R.layout.student_table_header, null);
                tableLayout.addView(tableRow);
                addingRows(studentService.getStudentsBySubject(subject), professor.getLocation(), subject, view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FirebaseDatabase.getInstance().getReference("users").addValueEventListener(new ValueEventListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    String type = snapshot.child(firebaseAuth.getCurrentUser().getUid())
                            .child("type").getValue(String.class);
                    if (type.equals(Type.PROFESSOR.toString())) {
                        professor = snapshot.child(firebaseAuth.getCurrentUser().getUid())
                                .getValue(Professor.class);

                        String professorJsonString = Utils.getGsonParser().toJson(professor);

                        bundle.putString("professorObject", professorJsonString);
//                        getChildFragmentManager().setFragmentResult("professorObject",savedInstanceState);
                        ArrayAdapter<String> arrayAdapter =
                                new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, professor.getSubjectsTeaching());
                        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                        spinner.setAdapter(arrayAdapter);
                        TableRow tableRow = (TableRow) LayoutInflater.from(view.getContext())
                                .inflate(R.layout.student_table_header, null);
                        tableLayout.addView(tableRow);
                        addingRows(studentService.getStudentsBySubject(professor.getSubjectsTeaching().get(0)), professor.getLocation(), professor.getSubjectsTeaching().get(0), view);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        //checkForPermissions();
    }

    public void addingRows(List<Student> students,
                           com.example.studentattendance.model.Location location,
                           String subject,
                           View view) {
        for (int i = 0; i < students.size(); i++) {
            TableRow tableRow = (TableRow) LayoutInflater.from(view.getContext()).inflate(R.layout.table_row, null);
            ((TextView) tableRow.findViewById(R.id.tableIndexRow)).setText(students.get(i).getIndex());
            ((TextView) tableRow.findViewById(R.id.tableNameRow)).setText(students.get(i).getName());
            ((TextView) tableRow.findViewById(R.id.tableSurnameRow)).setText(students.get(i).getLastName());
            ((TextView) tableRow.findViewById(R.id.tableAttendanceRow))
                    .setText(String.valueOf(students.get(i).getSubjectsListening().get(subject)));
            tableLayout.addView(tableRow);
        }
    }


}