package bert133.bertscout2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String TEAM_MESSAGE = "bert133.bertscout2018.TEAM_MESSAGE";

    public static String[] teams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teams = new String[80];
        for (int i = 0; i < 80; i++) {
            teams[i] = String.format("%d", i + 1001);
        }

        List<String> teamList = new ArrayList<String>(Arrays.asList(teams));
        ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, teamList);
        GridView gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(gridViewArrayAdapter);

        gridView.setLongClickable(true);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GridView gridView = (GridView) findViewById(R.id.gridView);
                TextView v = (TextView) gridView.getChildAt(i);

                Intent intent = new Intent(MainActivity.this, MatchActivity.class);
                intent.putExtra(TEAM_MESSAGE, v.getText());
                startActivity(intent);
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View arg1,
                                           int position, long arg3) {
                GridView gridView = (GridView) findViewById(R.id.gridView);
                TextView v = (TextView) gridView.getChildAt(position);

//                Intent intent = new Intent(MainActivity.this, MatchActivity.class);
//                intent.putExtra(TEAM_MESSAGE, v.getText());
//                startActivity(intent);

                Toast.makeText(MainActivity.this, v.getText(), Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Add Team!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_sync_data) {
            Toast.makeText(this, "Sync Data!", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_clear_data) {
            Toast.makeText(this, "Clear Data!", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
