package bert133.bertscout2018;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TeamActivity extends AppCompatActivity {

    String message;
    int currTeamNumber;

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);
    private ArrayAdapter<String> matchesAdapter;
    private ArrayList<String> matchesList;

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

        // set objects here
        final RatingBar ratingStars = findViewById(R.id.team_rating_value);
        final TextView matchText = findViewById(R.id.team_pick_number_text);
        final Button teamPickNumberMinusButton = findViewById(R.id.team_pick_number_minus_btn);
        final Button teamPickNumberPlusButton = findViewById(R.id.team_pick_number_plus_btn);
        final ToggleButton teamPickedButton = findViewById(R.id.team_picked_checkBox);
        final ListView showMatchesListView = findViewById(R.id.team_show_matches_list);

        //set chat adapter
        matchesList = new ArrayList<>();
        matchesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchesList);
        showMatchesListView.setAdapter(matchesAdapter);
        showMatchesListView.setClickable(true);

        showMatchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = showMatchesListView.getItemAtPosition(position);
                Toast.makeText(getBaseContext(), o.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // set current information

        JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
        try {
            ratingStars.setRating(currTeam.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_RATING));
            teamPickedButton.setChecked(currTeam.getBoolean(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED));
            matchText.setText(String.format("%d", currTeam.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER)));
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        // show all matches
        JSONArray matchList = mDBHelper.getMatchInfoByTeam(currTeamNumber);
        try {
            for (int i = 0; i < matchList.length(); i++) {
                JSONObject m = (JSONObject) matchList.get(i);
                int tempMatchNum = m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER);
                int tempCycleTime = m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_CYCLE_TIME);
                String tempCycleTimeString = "*****".substring(0, tempCycleTime);
                int tempPenalties = m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_PENALTIES);
                int tempRating = m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_RATING);
                String tempRatingString = "*****".substring(0, tempRating);
                boolean tempClimbed = m.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED);
                String tempClimbedString = tempClimbed ? "Yes" : "No";
                String lineInfo = String.format("Match %d -- Cycle %s -- Penalties %d -- Rating %s -- Climbed %s"
                        , tempMatchNum
                        , tempCycleTimeString
                        , tempPenalties
                        , tempRatingString
                        , tempClimbedString);
                matchesList.add(lineInfo);
                matchesAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }


        // rating

        ratingStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
                try {
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_RATING, (int) rating);
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        // pick number

        teamPickNumberMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                // get the team record from database
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
                try {
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER, tempValue);
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

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
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
                try {
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER, tempValue);
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                matchText.setText(Integer.toString(tempValue));
            }
        });

        // picked

        teamPickedButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                // get the team record from database
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);

                // update the picked flag
                try {
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED, buttonView.isChecked());
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }
}
