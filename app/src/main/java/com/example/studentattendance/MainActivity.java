package com.example.studentattendance;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {


    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        firebaseAuth = FirebaseAuth.getInstance();

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (loggedIn()) {
                    getLocation();
                }
            }
        }, 0, 60000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!loggedIn()) {
            navigateToLogInPage();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {

                    try {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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

    public boolean loggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void navigateToLogInPage() {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference("users").child(mAuth.getCurrentUser().getUid()).child("logged").setValue(false);
            FirebaseAuth.getInstance().signOut();
//            getSharedPreferences("data",MODE_PRIVATE).edit().putBoolean("isLogged",false).apply();
            navigateToLogInPage();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}