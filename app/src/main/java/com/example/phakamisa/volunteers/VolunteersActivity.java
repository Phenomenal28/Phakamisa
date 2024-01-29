package com.example.phakamisa.volunteers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.phakamisa.R;

public class VolunteersActivity extends AppCompatActivity {

    private CardView card_requestV;

    private CardView card_receiveV;
     private  CardView card_historyV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteers);

        card_requestV = findViewById(R.id.card_volunteer);
        card_requestV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VolunteersActivity.this, RequestVActivity.class));
            }
        });

        card_receiveV = findViewById(R.id.card_receiveV);
        card_receiveV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VolunteersActivity.this, ReceiveVActivity.class));
            }
        });

        card_historyV = findViewById(R.id.volunteer_history);
        card_historyV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(VolunteersActivity.this, VolunteerUserActivity.class));
            }
        });
    }
}