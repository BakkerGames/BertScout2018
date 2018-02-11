package bert133.bertscout2018;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddTeamActivity extends AppCompatActivity {

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);
    private String passwordCorrectValue = "bertdata";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        // show all teams already there
        ShowTeamList();

        Button passwordGoButton = findViewById(R.id.add_teams_password_go);
        passwordGoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordText = findViewById(R.id.add_teams_password_text);
                String passwordValue = passwordText.getText().toString();
                if (passwordValue.startsWith(passwordCorrectValue)) {
                    RelativeLayout layout2 = findViewById(R.id.add_teams_layout);
                    layout2.setVisibility(View.VISIBLE);
                    RelativeLayout layout3 = findViewById(R.id.add_teams_list_layout);
                    layout3.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong password! " + passwordText.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button addTeamButton = (Button) findViewById(R.id.add_teams_add_button);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText teamNumberEdit = (EditText) findViewById(R.id.add_teams_team_number_edit);
                TextView teamNumberList = (TextView) findViewById(R.id.add_teams_list_text);
                String value = String.valueOf(teamNumberEdit.getText());
                int teamNumber = Integer.parseInt(value);
                if (teamNumber < 1) {
                    teamNumberEdit.setText("");
                    return;
                }
                JSONObject currTeam = mDBHelper.getTeamInfo(teamNumber);

                if (currTeam == null) {

                    currTeam = new JSONObject();

                    try {
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER, teamNumber);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION, 0);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_RATING, 0);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER, 0);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED, false);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM_COMMENT, "");
                    } catch (JSONException ex) {
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        mDBHelper.updateTeamInfo(currTeam);
                    } catch (Exception ex) {
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    ShowTeamList();
                } else {
                    Toast.makeText(getApplicationContext(), String.format("Team %s already exists!", teamNumber), Toast.LENGTH_SHORT).show();
                }

                teamNumberEdit.setText("");
            }
        });

        Button deleteTeamButton = (Button) findViewById(R.id.add_teams_delete_button);
        deleteTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText teamNumberEdit =  findViewById(R.id.add_teams_team_number_edit);
                TextView teamNumberList =  findViewById(R.id.add_teams_list_text);
                String value = String.valueOf(teamNumberEdit.getText());
                int teamNumber = Integer.parseInt(value);
                if (teamNumber < 1) {
                    teamNumberEdit.setText("");
                    return;
                }
                JSONObject currTeam = mDBHelper.getTeamInfo(teamNumber);

                if (currTeam != null) {
                    try {
                        JSONArray matchList = mDBHelper.getMatchInfoByTeam(teamNumber);
                        if (matchList == null || matchList.length() == 0){
                            mDBHelper.deleteTeamInfo(teamNumber);
                        }else{
                            Toast.makeText(context, String.format("Team %s has matches -- Cannot delete!", teamNumber), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception ex) {
                        Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    ShowTeamList();
                } else {
                    Toast.makeText(getApplicationContext(), String.format("Team %s not found!", teamNumber), Toast.LENGTH_SHORT).show();
                }
                teamNumberEdit.setText("");
            }
        });

    }

    public void ShowTeamList(){
        JSONArray teamList = mDBHelper.getTeamInfoList(false);
        TextView teamNumberList = (TextView) findViewById(R.id.add_teams_list_text);
        teamNumberList.setText("");
        try {
            for (int i = 0; i < teamList.length(); i++) {
                JSONObject teamInfo = teamList.getJSONObject(i);
                if (i > 0) {
                    teamNumberList.append(", ");
                }
                teamNumberList.append(String.format("%d", teamInfo.getInt("team")));
            }
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            return;
        }
    }
}
