package bert133.bertscout2018;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ClearDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_data);

        // --- buttons ---

        Button goButton = findViewById(R.id.clear_password_go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText passwordText = findViewById(R.id.clear_password_text);
                String passwordValue = passwordText.getText().toString();
                if (passwordValue.startsWith("bertdata")) {
                    RelativeLayout layout2 = findViewById(R.id.clear_relative_2);
                    layout2.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Wrong password! " + passwordText.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

        Button clearDataButton = (Button) findViewById(R.id.clear_data_button);
        clearDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
                db.execSQL(DBContract.TableTeamInfo.SQL_QUERY_DELETE_TABLE);
                db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_DELETE_TABLE);
                db.execSQL(DBContract.TableTeamInfo.SQL_QUERY_CREATE_TABLE);
                db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_CREATE_TABLE);
                Toast.makeText(getApplicationContext(), "Data cleared!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
