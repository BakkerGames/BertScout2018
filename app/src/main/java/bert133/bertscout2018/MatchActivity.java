package bert133.bertscout2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        // get team number from MainActivity
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.TEAM_MESSAGE);

        // set title
        this.setTitle(String.format("Match Scouting - Team %s", message));

        // --- buttons ---

        Button matchMinusButton = (Button) findViewById(R.id.stand_match_minus_btn);
        matchMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = (TextView) findViewById(R.id.stand_match_number);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 1) {
                        tempValue = 1;
                    } else {
                        tempValue--;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        Button matchPlusButton = (Button) findViewById(R.id.stand_match_plus_btn);
        matchPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = (TextView) findViewById(R.id.stand_match_number);
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

        Button matchMinus10Button = (Button) findViewById(R.id.stand_match_minus10_btn);
        matchMinus10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = (TextView) findViewById(R.id.stand_match_number);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue <= 10) {
                        tempValue = 1;
                    } else if (tempValue % 10 != 0) {
                        tempValue = tempValue - (tempValue % 10);
                    } else {
                        tempValue = tempValue - 10;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        Button matchPlus10Button = (Button) findViewById(R.id.stand_match_plus10_btn);
        matchPlus10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView matchText = (TextView) findViewById(R.id.stand_match_number);
                matchText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(matchText.getText().toString());
                    if (tempValue < 10) {
                        tempValue = 10;
                    } else if (tempValue >= 990) {
                        tempValue = 999;
                    } else if (tempValue % 10 != 0) {
                        tempValue = tempValue + 10 - (tempValue % 10);
                    } else {
                        tempValue = tempValue + 10;
                    }
                } catch (Exception e) {
                    tempValue = 1;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        Button matchGoButton = (Button) findViewById(R.id.stand_match_go_btn);
        matchGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // hide button layout
                RelativeLayout layoutButtons = (RelativeLayout) findViewById(R.id.stand_match_button_layout);
                layoutButtons.setVisibility(View.INVISIBLE);
                RelativeLayout layoutInfo = (RelativeLayout) findViewById(R.id.stand_match_info_layout);
                layoutInfo.setVisibility(View.VISIBLE);
            }
        });

    }
}
