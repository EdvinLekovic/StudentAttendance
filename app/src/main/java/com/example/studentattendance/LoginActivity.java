package com.example.studentattendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.studentattendance.model.Professor;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private EditText email, password;
    private Button buttonLogin, buttonSignUp;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //Initializing the editText fields for email and password and later using their data
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //Initializing buttons for Login and SignUp
        buttonLogin = findViewById(R.id.login);
        buttonSignUp = findViewById(R.id.signUp);
        //creatingAdminUser
        creatingAdminUsers();

        sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                        "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                    Toast.makeText(getApplicationContext(),
                            "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                    String email = sharedPreferences.getString("email", "");
                    String password = sharedPreferences.getString("password", "");
                    login(email, password);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use account password")
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

//        Button biometricLoginButton = findViewById(R.id.biometric_login);
//        biometricLoginButton.setOnClickListener(view -> {
//            biometricPrompt.authenticate(promptInfo);
//        }); 

        //Event that happen after you click the button Login
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputEmail = email.getText().toString();
                String inputPassword = password.getText().toString();
                if (!inputEmail.isEmpty() && !inputPassword.isEmpty()) {
                    login(inputEmail, inputPassword);
                }
            }
        });

        //Event that happen after you click the button SignUp
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToSignUpForm();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        System.out.println(mAuth.getCurrentUser().getEmail());
        if (loggedIn()) {
            biometricPrompt.authenticate(promptInfo);
        }
    }

    private void creatingAdminUsers() {
        mAuth.createUserWithEmailAndPassword("edvin.lekovikj@gmail.com", "edvinlekovic12")
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.R)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            String uid = mAuth.getCurrentUser().getUid();
                            Professor professor = new Professor(
                                    uid,
                                    "Edvin",
                                    "Lekovic",
                                    "edvin.lekovikj@gmail.com",
                                    List.of("Business and management", "Discrete Mathematics", "Structural programming", "Advance programming", "Web programming"));
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            database.getReference("users").child(uid).setValue(professor);
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, "User with this email already exists", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    @SuppressLint("CommitPrefEdits")
                    SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();
                    Toast.makeText(LoginActivity.this, "Logged succesfully", Toast.LENGTH_LONG).show();

                    addingLoggedUsers();

                    navigateToHomePage();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void addingLoggedUsers() {
        String uid = mAuth.getCurrentUser().getUid();
        firebaseDatabase.getReference("users").child(uid).child("logged").setValue(true);
    }

    public boolean loggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    private void navigateToHomePage() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }

    private void navigateToSignUpForm() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }
}