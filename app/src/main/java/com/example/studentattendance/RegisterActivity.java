package com.example.studentattendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studentattendance.model.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RegisterActivity extends AppCompatActivity {

    private EditText studentEmail, studentPassword, studentName, studentLastName, studentIndex;
    private Button chooseSubjects, buttonSignUp;
    private FirebaseAuth mAuth;
    private List<String> subjectsListening;
    private String[] subjects;
    private boolean[] checked;
    private TextView accountAlready;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        studentName = findViewById(R.id.StudentName);
        studentLastName = findViewById(R.id.StudentLastName);
        studentIndex = findViewById(R.id.StudentIndex);
        studentEmail = findViewById(R.id.StudentEmail);
        studentPassword = findViewById(R.id.StudentPassword);
        buttonSignUp = findViewById(R.id.StudentSignUp);
        chooseSubjects = findViewById(R.id.ChooseSubjects);

        subjectsListening = new ArrayList<>();
        subjects = getResources().getStringArray(R.array.student_courses);
        checked = new boolean[subjects.length];

        chooseSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                builder.setTitle("Classes you are enrolled in");
                builder.setMultiChoiceItems(subjects, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int position, boolean isChecked) {
                        if (isChecked) {
                            subjectsListening.add(subjects[position]);
                        } else {
                            subjectsListening.remove(subjects[position]);
                        }
                    }
                });

                builder.setCancelable(false);

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = studentName.getText().toString();
                String lastname = studentLastName.getText().toString();
                String index = studentIndex.getText().toString();
                String email = studentEmail.getText().toString();
                String password = studentPassword.getText().toString();
                if (!name.isEmpty() && !lastname.isEmpty() && !index.isEmpty() &&
                        !email.isEmpty() && !password.isEmpty() && !subjectsListening.isEmpty()) {
                    register(name, lastname, index, email, password, subjectsListening);
                }
            }
        });

        accountAlready = findViewById(R.id.accountAlready);
        accountAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLoginPage();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void navigateToLoginPage() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Map<String,Integer>  createMapSubjectAttendance(List<String> subjectsListening){
       return subjectsListening.stream().collect(Collectors.toMap(s -> s,s -> 0));
    }

    private void register(String name, String lastname, String index,
                          String email, String password, List<String> subjectsListening) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                System.out.println(task.getException());
                if (task.isSuccessful()) {
                    //creating student
                    Student student = new Student(mAuth.getCurrentUser().getUid(),index,name,lastname,email,createMapSubjectAttendance(subjectsListening));
                    myRef.child(mAuth.getCurrentUser().getUid()).setValue(student);
                    Toast.makeText(RegisterActivity.this, "SignUp successful", Toast.LENGTH_LONG).show();
                    navigateToLoginPage();
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this, "User with this email already exists", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
}

