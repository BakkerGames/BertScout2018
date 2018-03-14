package bert133.bertscout2018;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    boolean loadingValues = false;

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
        final TextView pickNumberText = findViewById(R.id.team_pick_number_text);
        final Button teamPickNumberMinusButton = findViewById(R.id.team_pick_number_minus_btn);
        final Button teamPickNumberPlusButton = findViewById(R.id.team_pick_number_plus_btn);
        final ToggleButton teamPickedButton = findViewById(R.id.team_picked_checkBox);
        final ListView showMatchesListView = findViewById(R.id.team_show_matches_list);
        final EditText teamComments = findViewById(R.id.team_comments_text);

        //set match adapter
        matchesList = new ArrayList<>();
        matchesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, matchesList);
        showMatchesListView.setAdapter(matchesAdapter);
        showMatchesListView.setClickable(true);

        showMatchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String matchLine = showMatchesListView.getItemAtPosition(position).toString();
                if (!matchLine.startsWith("Match ")) {
                    return;
                }
                String[] matchLineArray = matchLine.split(" ");
                String teamMatchMessage = String.format("%d %s", currTeamNumber, matchLineArray[1]);

                try {
                    // jump to match info here
                    Intent intent = new Intent(TeamActivity.this, MatchActivity.class);
                    intent.putExtra(MainActivity.TEAM_MESSAGE, teamMatchMessage);
                    startActivity(intent);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        // set current information

        JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
        try {
            loadingValues = true;
            ratingStars.setRating(currTeam.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_RATING));
            teamPickedButton.setChecked(currTeam.getBoolean(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED));
            pickNumberText.setText(String.format("%d", currTeam.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER)));
            teamComments.setText(currTeam.getString(DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT));
            loadingValues = false;
        } catch (Exception ex) {
            loadingValues = false;
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
                String tempCycleTimeString = tempCycleTime > 0 ? "*****".substring(0, tempCycleTime) : "0";
                int tempRating = m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_RATING);
                String tempRatingString = tempRating > 0 ? "*****".substring(0, tempRating) : "0";
                boolean tempParked = m.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_PARKED);
                boolean tempClimbed = m.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_CLIMBED);
                String tempEndgame = tempClimbed ? "CLIMB" : (tempParked ? "PARK" : "-");
                String tempComment = m.getString(DBContract.TableMatchInfo.COLNAME_MATCH_COMMENT);
                String tempAutoString = "";
                if (m.getBoolean(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_BASELINE)) {
                    tempAutoString += "B";
                }
                if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH) == 1) {
                    if (tempAutoString != "") {
                        tempAutoString += ",";
                    }
                    tempAutoString += "Sw";
                } else if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH) > 1) {
                    if (tempAutoString != "") {
                        tempAutoString += ",";
                    }
                    tempAutoString += "Sw=" + m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SWITCH);
                }
                if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE) == 1) {
                    if (tempAutoString != "") {
                        tempAutoString += ",";
                    }
                    tempAutoString += "Sc";
                } else if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE) > 1) {
                    if (tempAutoString != "") {
                        tempAutoString += ",";
                    }
                    tempAutoString += "Sc=" + m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_AUTO_SCALE);
                }
                String tempTeleString = "";
                if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH) > 0) {
                    tempTeleString += "Sw=" + m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SWITCH);
                }
                if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE) > 0) {
                    if (tempTeleString != "") {
                        tempTeleString += ",";
                    }
                    tempTeleString += "Sc=" + m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_SCALE);
                }
                if (m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE) > 0) {
                    if (tempTeleString != "") {
                        tempTeleString += ",";
                    }
                    tempTeleString += "X=" + m.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TELE_EXCHANGE);
                }
                if (tempComment != "") {
                    tempComment = "\r\n" + tempComment;
                }
                String lineInfo = String.format("Match %d -- Auto %s -- Tele %s -- End %s -- Cycle %s -- Rating %s%s"
                        , tempMatchNum
                        , tempAutoString
                        , tempTeleString
                        , tempEndgame
                        , tempCycleTimeString
                        , tempRatingString
                        , tempComment
                );
                matchesList.add(lineInfo);
//                if (tempComment != null && tempComment != "") {
//                    matchesList.add(tempComment);
//                }
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
                if (loadingValues) {
                    return;
                }
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
                pickNumberText.requestFocus();
                int tempValue;
                try {
                    tempValue = Integer.parseInt(pickNumberText.getText().toString());
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
                pickNumberText.setText(Integer.toString(tempValue));
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
                if (loadingValues) {
                    return;
                }
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
                try {
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED, buttonView.isChecked());
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        // comments

        teamComments.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (loadingValues) {
                    return;
                }
                JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
                try {
                    String tempValue = teamComments.getText().toString();
                    if (currTeam.getString(DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT) != tempValue) {
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT, tempValue);
                        mDBHelper.updateTeamInfo(currTeam);
                    }
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

    }

}
