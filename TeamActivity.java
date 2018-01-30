package bert133.bertscout2018;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.UUID;

public class TeamActivity extends AppCompatActivity {

    String message;
    int currTeamNumber;

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);

    ArrayList<String> teamCommentArray= new ArrayList<String>();

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
        final RatingBar ratingStars = (RatingBar) findViewById(R.id.team_rating_value);
        final TextView matchText = (TextView) findViewById(R.id.team_pick_number_text);
        final Button teamPickNumberMinusButton = (Button) findViewById(R.id.team_pick_number_minus_btn);
        final Button teamPickNumberPlusButton = (Button) findViewById(R.id.team_pick_number_plus_btn);
        final ToggleButton teamPickedButton = (ToggleButton) findViewById(R.id.team_picked_checkBox);
        final Button saveCommentButton = (Button) findViewById(R.id.save_comment_button);

        // set current information

        JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
        try {
            ratingStars.setRating(currTeam.getInt(DBContract.TableTeamInfo.COLNAME_RATING));
            teamPickedButton.setChecked(currTeam.getBoolean(DBContract.TableTeamInfo.COLNAME_PICKED));
            matchText.setText(String.format("%d", currTeam.getInt(DBContract.TableTeamInfo.COLNAME_PICK_NUMBER)));
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
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_RATING, (int)rating);
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
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_PICK_NUMBER, tempValue);
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
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_PICK_NUMBER, tempValue);
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
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_PICKED, buttonView.isChecked());
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
            }
        });

        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* JSONObject currTeam = mDBHelper.getTeamInfo(currTeamNumber);
                try {
                    currTeam.put(DBContract.TableTeamInfo.COLNAME_RATING, (int)rating);
                    mDBHelper.updateTeamInfo(currTeam);
                } catch (Exception ex) {
                    Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }*/
               JSONObject teamComment = new JSONObject();
               TextView commentText = (TextView) findViewById(R.id.team_comment_text);
               try {
               teamComment.put("team",currTeamNumber);
               teamComment.put("uuid",UUID.randomUUID().toString());
               teamComment.put("comment",commentText.getText());
            } catch (Exception ex) {
                   Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                   return;
               }
               }
        });

    }
}
