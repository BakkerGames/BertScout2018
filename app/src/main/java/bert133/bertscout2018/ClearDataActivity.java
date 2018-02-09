package bert133.bertscout2018;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ClearDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_data);

        // --- buttons ---

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
