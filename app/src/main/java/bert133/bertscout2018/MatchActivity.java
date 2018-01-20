package com.example.chime.bertscout2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // get team number from MainActivity
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TEAM_MESSAGE);
        TextView textView = findViewById(R.id.textViewMessage);
        textView.setText(message);
    }
}
