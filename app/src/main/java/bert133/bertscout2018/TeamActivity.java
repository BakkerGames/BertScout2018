package bert133.bertscout2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TeamActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        // get team number from MainActivity
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TEAM_MESSAGE);

        // set title
        this.setTitle(String.format("Team Information - Team %s", message));

        Button teamPickNumberMinusButton = (Button) findViewById(R.id.team_pick_number_minus_btn);
        teamPickNumberMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = (TextView) findViewById(R.id.team_pick_number_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 0) {
                        tempValue = 0;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        Button teamPickNumberPlusButton = (Button) findViewById(R.id.team_pick_number_plus_btn);
        teamPickNumberPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = (TextView) findViewById(R.id.team_pick_number_text);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue >= 999) {
                        tempValue = 999;
                    } else {
                        tempValue++;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

    }
}
