package com.example.phakamisa;

import androidx.annotation.NonNull;

import android.Manifest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phakamisa.donations.DonationMainActivity;
import com.example.phakamisa.post.Post;
import com.example.phakamisa.profile.UserProfileActivity;
import com.example.phakamisa.volunteers.VolunteersActivity;

public class DashboardActivity extends AppCompatActivity {
    //CardView card_profile;


    private CardView card_volunteer, card_report, card_profile, card_donate;


    //emergency calls
    private static final int REQUEST_CALL = 1;

    private TextView policeTextView, ambulancePhone, callCPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        card_donate = findViewById(R.id.card_donate);
        card_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardActivity.this, DonationMainActivity.class));
            }
        });


        card_profile = findViewById(R.id.card_profile);

        card_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DashboardActivity.this, UserProfileActivity.class));
            }
        });

        card_report = findViewById(R.id.card_reporting);
        card_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(DashboardActivity.this, Post.class));
            }
        });

        card_volunteer = findViewById(R.id.card_volunteer);
        card_volunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(DashboardActivity.this, VolunteersActivity.class));

            }
        });

        policeTextView = findViewById(R.id.policePhone);
        policeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCall();
            }
        });

        ambulancePhone = findViewById(R.id.ambulancePhone);
        ambulancePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCallA();
            }
        });

        callCPhone = findViewById(R.id.callCentre);
        callCPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeCallC();
            }
        });

    }

    private void makeCall() {
        String number = policeTextView.getText().toString();
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                startCall(number);
            }
        }
    }

    private void startCall(String number) {
        String dial = "tel: " + number;
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(dial));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    private void makeCallA() {
        String numberA = ambulancePhone.getText().toString();
        if (numberA.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                startCall(numberA);
            }
        }
    }

    private void startCallA(String numberA) {
        String dial = "tel: " + numberA;
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(dial));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    private void makeCallC() {
        String numberC = callCPhone.getText().toString();
        if (numberC.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                startCall(numberC);
            }
        }
    }

    private void startCallC(String numberC) {
        String dial = "tel: " + numberC;
        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(dial));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeCall();
                makeCallA();
                makeCallC();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

