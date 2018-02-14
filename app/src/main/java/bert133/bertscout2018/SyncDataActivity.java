package bert133.bertscout2018;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;
import java.util.zip.DataFormatException;

public class SyncDataActivity extends AppCompatActivity {

    private TextView status;
    private Button btnConnect;
    private ListView listView;
    private Button sendTeamDataButton;
    private Button sendMatchDataButton;

    private Dialog dialog;
    private ArrayAdapter<String> chatAdapter;
    private ArrayList<String> chatMessages;
    private BluetoothAdapter bluetoothAdapter;
    private ChatController chatController;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_OBJECT = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String DEVICE_OBJECT = "device_name";

    private static final int REQUEST_ENABLE_BLUETOOTH = 1;

    public BluetoothDevice connectingDevice;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    private Context context = this;
    private DBHelper mDBHelper = new DBHelper(context);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_data);

        btnConnect = findViewById(R.id.sync_connect_button);
        status = findViewById(R.id.sync_connect_status_text);
        listView = findViewById(R.id.sync_list);
        sendTeamDataButton = findViewById(R.id.sync_send_team_data_button);
        sendMatchDataButton = findViewById(R.id.sync_send_match_data_button);

        sendTeamDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray teamDataList = mDBHelper.getTeamInfoList(false);
                JSONArray sendList = new JSONArray();
                for (int i = 0; i < teamDataList.length(); i++) {
                    try {
                        JSONObject team = (JSONObject) teamDataList.get(i);
                        if (sendList.length() == 0) {
                            sendList.put(DBHelper.SYNC_HEADER_TEAM);
                        }
                        if (sendList.toString().length() + team.toString().length() < ChatController.MAX_MESSAGE_BYTES - 4) {
                            sendList.put(team);
                        } else {
                            sendMessage(sendList.toString());
                            sendList = new JSONArray();
                        }
                    } catch (Exception ex) {
                    }
                }
                if (sendList.length() > 0) {
                    sendMessage(sendList.toString());
                }
            }
        });

        sendMatchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONArray matchDataList = mDBHelper.getMatchInfoList(false);
                JSONArray sendList = new JSONArray();
                for (int i = 0; i < matchDataList.length(); i++) {
                    try {
                        JSONObject match = (JSONObject) matchDataList.get(i);
                        if (sendList.length() == 0) {
                            sendList.put(DBHelper.SYNC_HEADER_MATCH);
                        }
                        if (sendList.toString().length() + match.toString().length() < ChatController.MAX_MESSAGE_BYTES - 4) {
                            sendList.put(match);
                        } else {
                            sendMessage(sendList.toString());
                            sendList = new JSONArray();
                        }
                    } catch (Exception ex) {
                    }
                }
                if (sendList.length() > 0) {
                    sendMessage(sendList.toString());
                }
            }
        });

        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //show bluetooth devices dialog when click connect button
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBluetoothPickDialog();
            }
        });

        //set chat adapter
        chatMessages = new ArrayList<>();
        chatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, chatMessages);
        listView.setAdapter(chatAdapter);

    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }
            }
        }
    };

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            btnConnect.setEnabled(false);
                            sendTeamDataButton.setEnabled(true);
                            sendMatchDataButton.setEnabled(true);
                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            btnConnect.setEnabled(false);
                            sendTeamDataButton.setEnabled(false);
                            sendMatchDataButton.setEnabled(false);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus("Not connected");
                            chatMessages.clear();
                            chatAdapter.notifyDataSetChanged();
                            btnConnect.setEnabled(true);
                            sendTeamDataButton.setEnabled(false);
                            sendMatchDataButton.setEnabled(false);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    setStatus("### message_write ###");
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    chatMessages.add("Sent: " + writeMessage);
                    chatAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ:
                    setStatus("### message_read ###");
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatMessages.add(connectingDevice.getName() + ":  " + readMessage);
                    chatAdapter.notifyDataSetChanged();
                    MergeReceivedData(readMessage);
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    setStatus("### device_object ###");
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(), Toast.LENGTH_SHORT).show();
                    btnConnect.setEnabled(false);
                    break;
                case MESSAGE_TOAST:
                    setStatus("### message_toast ###");
                    Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void showBluetoothPickDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_bluetooth);
        dialog.setTitle("Bluetooth Devices");

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        bluetoothAdapter.startDiscovery();

        //Initializing bluetooth adapters
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //locate listviews and attatch the adapters
        ListView listViewPairedDevList = (ListView) dialog.findViewById(R.id.pairedDeviceList);
        ListView listViewDiscoveredDevList = (ListView) dialog.findViewById(R.id.discoveredDeviceList);
        listViewPairedDevList.setAdapter(pairedDevicesAdapter);
        listViewDiscoveredDevList.setAdapter(discoveredDevicesAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
        }

        //Handling listview item click event
        listViewPairedDevList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                connectToDevice(address);
                dialog.dismiss();
            }

        });

        listViewDiscoveredDevList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                bluetoothAdapter.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                connectToDevice(address);
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setStatus(String s) {
        status.setText(s);
    }

    private void connectToDevice(String deviceAddress) {
        bluetoothAdapter.cancelDiscovery();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        chatController.connect(device);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BLUETOOTH:
                if (resultCode == Activity.RESULT_OK) {
                    chatController = new ChatController(this, handler);
                } else {
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void sendMessage(String message) {
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            Toast.makeText(this, "Connection was lost!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            chatController.write(send);
            try{
                Thread.sleep(2000);
            } catch(Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
        } else {
            chatController = new ChatController(this, handler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (chatController != null) {
            if (chatController.getState() == ChatController.STATE_NONE) {
                chatController.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatController != null)
            chatController.stop();
    }

    private String getTeamDataForSending() {
        // show all teams already there
        JSONArray teamListJA = mDBHelper.getTeamInfoList(true);
        return teamListJA.toString();
    }

    private String getMatchDataForSending() {
        // show all teams already there
        JSONArray matchListJA = mDBHelper.getMatchInfoList(true);
        return matchListJA.toString();
    }

    private void MergeReceivedData(String message) {
        try {
            JSONArray dataArray = new JSONArray(message);
            if (dataArray.getString(0).equals(DBHelper.SYNC_HEADER_TEAM)) {
                chatMessages.add(dataArray.getString(0));
                chatAdapter.notifyDataSetChanged();
                int addCount = 0;
                for (int i = 1; i < dataArray.length(); i++) {
                    JSONObject newRow = dataArray.getJSONObject(i);
                    int teamNumber = newRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER);
                    JSONObject existingRow = mDBHelper.getTeamInfo(teamNumber);
                    if (existingRow == null) {
                        newRow.remove(DBContract.TableTeamInfo._ID); // can't save another device's id values
                        mDBHelper.updateTeamInfo(newRow);
                        addCount++;
                    } else if (newRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION) >
                            existingRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION)) {
                        newRow.put(DBContract.TableTeamInfo._ID, existingRow.getInt(DBContract.TableTeamInfo._ID));
                        // subtract one from version, it will be added back upon save
                        newRow.put(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION, existingRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION) - 1);
                        mDBHelper.updateTeamInfo(newRow);
                        addCount++;
                    }
                    chatMessages.add(newRow.toString());
                    chatAdapter.notifyDataSetChanged();
                }
                chatMessages.add(String.format("%d rows added", addCount));
                chatAdapter.notifyDataSetChanged();
            } else if (dataArray.getString(0).equals(DBHelper.SYNC_HEADER_MATCH)) {
                chatMessages.add(dataArray.getString(0));
                chatAdapter.notifyDataSetChanged();
                int addCount = 0;
                for (int i = 1; i < dataArray.length(); i++) {
                    JSONObject newRow = dataArray.getJSONObject(i);
                    int teamNumber = newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM);
                    int matchNumber = newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER);
                    JSONObject existingRow = mDBHelper.getMatchInfo(teamNumber, matchNumber);
                    if (existingRow == null) {
                        newRow.remove(DBContract.TableMatchInfo._ID); // can't save another device's id values
                        mDBHelper.updateMatchInfo(newRow);
                        addCount++;
                    } else if (newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION) >
                            existingRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION)) {
                        newRow.put(DBContract.TableMatchInfo._ID, existingRow.getInt(DBContract.TableMatchInfo._ID));
                        // subtract one from version, it will be added back upon save
                        newRow.put(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION, existingRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION) - 1);
                        mDBHelper.updateMatchInfo(newRow);
                        addCount++;
                    }
                    chatMessages.add(newRow.toString());
                    chatAdapter.notifyDataSetChanged();
                }
                chatMessages.add(String.format("%d rows added", addCount));
                chatAdapter.notifyDataSetChanged();
            } else {
                chatMessages.add(dataArray.getString(0));
                chatMessages.add(String.format("Unknown identifier: %s", dataArray.getString(0)));
                chatAdapter.notifyDataSetChanged();
                throw new DataFormatException(String.format("Unknown identifier: %s", dataArray.getString(0)));
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Merge error! " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
