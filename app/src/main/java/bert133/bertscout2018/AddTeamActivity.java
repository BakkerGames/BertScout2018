package bert133.bertscout2018;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTeamActivity extends AppCompatActivity {

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
                if (!value.equals("")) {
                    if (String.valueOf(teamNumberList.getText()).length() > 0) {
                        teamNumberList.append(", ");
                    }
                    teamNumberList.append(value);
                    teamNumberEdit.setText("");
                }
            }
        });
    }
}
