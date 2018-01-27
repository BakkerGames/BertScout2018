package bert133.bertscout2018;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SyncDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        Button syncSendButton = (Button) findViewById(R.id.sync_data_send_button);
        syncSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"sending",Toast.LENGTH_SHORT).show();
            }
        });

        Button syncListenButton = (Button) findViewById(R.id.sync_data_listen_button);
        syncListenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"listening",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
