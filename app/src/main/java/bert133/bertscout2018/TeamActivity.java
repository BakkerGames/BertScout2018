package bert133.bertscout2018;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

public class TeamActivity extends AppCompatActivity {

    String message;
    int currTeamNumber;

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        // get team number from MainActivity
        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.TEAM_MESSAGE);
        currTeamNumber = Integer.parseInt(message);

        // set title
        this.setTitle(String.format("Team Information - Team %s", message));

        // set current information

        JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);


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


        ToggleButton teamPickedButton = (ToggleButton) findViewById(R.id.team_picked_checkBox);
        try
        {
            teamPickedButton.setChecked(currTeam.getBoolean(DBContract.TableTeamInfo.COLNAME_PICKED));
        }catch(Exception ex){
        }

        teamPickedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(getApplicationContext(),
//                        String.valueOf(buttonView.isChecked()), Toast.LENGTH_SHORT).show();

                // get the team record from database
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);

                //Toast.makeText(context, currTeam.toString(), Toast.LENGTH_LONG).show(); // todo for testing

                // update the picked flag
                try {
                    if (buttonView.isChecked()) {
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_PICKED, 1);
                    } else {
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_PICKED, 0);
                    }
                } catch (JSONException ex) {
                }
                //Toast.makeText(context, currTeam.toString(), Toast.LENGTH_LONG).show(); // todo for testing

                // save the team record
                try {
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(getApplicationContext(), "Error saving TeamInfo", Toast.LENGTH_LONG).show();
                    return;
                }

            }
        });

    }
}
