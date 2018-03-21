package bert133.bertscout2018;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);

    public static final String TEAM_MESSAGE = "bert133.bertscout2018.TEAM_MESSAGE";

    //public static String[] teams;

    private void createDatabase() {
        SQLiteDatabase db = openOrCreateDatabase(DBHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
        // this creates the tables if first time, otherwise does nothing
        db.execSQL(DBContract.TableTeamInfo.SQL_QUERY_CREATE_TABLE);
        db.execSQL(DBContract.TableMatchInfo.SQL_QUERY_CREATE_TABLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createDatabase();

        // show all teams already there
        JSONArray teamListJA = mDBHelper.getTeamInfoListPickSort(false);
        List<String> teamList = new ArrayList<String>();
        try {
            for (int i = 0; i < teamListJA.length(); i++) {
                JSONObject teamInfo = teamListJA.getJSONObject(i);
                String teamText = String.format("%d", teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER));
                if (teamInfo.getBoolean(DBContract.TableTeamInfo.COLNAME_TEAM_PICKED)) {
                    teamText = teamText + " \r\nPICKED";
                } else {
                    int tempRating = teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_RATING);
                    int tempPickNumber = teamInfo.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_PICK_NUMBER);
//                    if (tempRating > 0) {
//                        String tempRatingString = "*****".substring(0, tempRating);
//                        teamText = teamText + " \r\n" + tempRatingString;
//                    }
                    if (tempPickNumber > 0) {
                        teamText = teamText + String.format(" \r\n#%d", tempPickNumber);
                    }
                }
                teamList.add(teamText);
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG);
        }

        ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, teamList);
        GridView gridView = (GridView) findViewById(R.id.main_gridview);
        gridView.setAdapter(gridViewArrayAdapter);

        gridView.setLongClickable(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GridView gridView = (GridView) findViewById(R.id.main_gridview);
                TextView v = (TextView) gridView.getChildAt(i);

                // send team number
                Intent intent = new Intent(MainActivity.this, MatchActivity.class);
                String teamMessage = (String) v.getText();
                if (teamMessage.contains(" ")) {
                    teamMessage = teamMessage.substring(0, teamMessage.indexOf(" "));
                }
                intent.putExtra(TEAM_MESSAGE, teamMessage);
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View arg1,
                                           int position, long arg3) {
                GridView gridView = (GridView) findViewById(R.id.main_gridview);
                TextView v = (TextView) gridView.getChildAt(position);

                // send team number
                Intent intent = new Intent(MainActivity.this, TeamActivity.class);
                String teamMessage = (String) v.getText();
                if (teamMessage.contains(" ")) {
                    teamMessage = teamMessage.substring(0, teamMessage.indexOf(" "));
                }
                intent.putExtra(TEAM_MESSAGE, teamMessage);
                startActivity(intent);

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_team) {
            Intent intent = new Intent(MainActivity.this, AddTeamActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_sync_data) {
            Intent intent = new Intent(MainActivity.this, SyncDataActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_clear_data) {
            Intent intent = new Intent(MainActivity.this, ClearDataActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
