package bert133.bertscout2018;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AddTeamActivity extends AppCompatActivity {

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team);

        Button addTeamButton = (Button) findViewById(R.id.add_teams_add_button);
        addTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText teamNumberEdit = (EditText) findViewById(R.id.add_teams_team_number_edit);
                TextView teamNumberList = (TextView) findViewById(R.id.add_teams_list_text);
                String value = String.valueOf(teamNumberEdit.getText());
                while (value.startsWith("0")) {
                    value = value.substring(1);
                }

                int teamNumber = Integer.parseInt(value);
                JSONObject currTeam = mDBHelper.getTeamInfo(teamNumber);

                if (currTeam == null) {

                    currTeam = new JSONObject();

                    try {
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_TEAM, teamNumber);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_RATING, 0);
                        currTeam.put(DBContract.TableTeamInfo.COLNAME_PICKED, 0);
                    } catch (JSONException ex) {
                        Toast.makeText(getApplicationContext(), "Error filling TeamInfo", Toast.LENGTH_LONG).show();
                        return;
                    }

                    try {
                        mDBHelper.updateTeamInfo(currTeam);
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error saving TeamInfo", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (!value.equals("")) {
                        if (String.valueOf(teamNumberList.getText()).length() > 0) {
                            teamNumberList.append(", ");
                        }
                        teamNumberList.append(value);
                        teamNumberEdit.setText("");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), String.format("Team %s already exists!", teamNumber), Toast.LENGTH_LONG).show();
                }

                teamNumberEdit.setText("");
            }
        });
    }
}
