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
    private Button sendDisconnectButton;

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

    private static final String ACK_MESSAGE = "ACK";
    private static final String NAK_MESSAGE = "NAK";

    private static final int SLEEP_TIME = 200;

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
        sendDisconnectButton = findViewById(R.id.sync_disconnect_button);

        sendTeamDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessages.clear();
                chatAdapter.notifyDataSetChanged();
                JSONArray teamDataList = mDBHelper.getTeamInfoList(false);
                for (int i = 0; i < teamDataList.length(); i++) {
                    try {
                        JSONArray sendList = new JSONArray();
                        sendList.put(DBHelper.SYNC_HEADER_TEAM);
                        JSONObject team = (JSONObject) teamDataList.get(i);
                        sendList.put(team);
                        sendMessage(sendList.toString());
                        chatAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        sendMatchDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatMessages.clear();
                chatAdapter.notifyDataSetChanged();
                JSONArray matchDataList = mDBHelper.getMatchInfoList(false);
                for (int i = 0; i < matchDataList.length(); i++) {
                    try {
                        JSONArray sendList = new JSONArray();
                        sendList.put(DBHelper.SYNC_HEADER_MATCH);
                        JSONObject match = (JSONObject) matchDataList.get(i);
                        sendList.put(match);
                        sendMessage(sendList.toString());
                        chatAdapter.notifyDataSetChanged();
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        sendDisconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                if (chatController != null) {
                    chatController.stop();
                }
                chatMessages.add("Disconnected");
                chatAdapter.notifyDataSetChanged();
            }
        });

        //check device support bluetooth or not
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available!", Toast.LENGTH_LONG).show();
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

    private boolean ackFlag = false;
    private boolean nakFlag = false;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case ChatController.STATE_CONNECTED:
                            setStatus("Connected to: " + connectingDevice.getName());
                            chatMessages.add("Connected to: " + connectingDevice.getName());
                            chatAdapter.notifyDataSetChanged();
                            btnConnect.setEnabled(false);
                            sendTeamDataButton.setEnabled(true);
                            sendMatchDataButton.setEnabled(true);
                            sendDisconnectButton.setEnabled(true);
                            break;
                        case ChatController.STATE_CONNECTING:
                            setStatus("Connecting...");
                            chatMessages.add("Connecting...");
                            chatAdapter.notifyDataSetChanged();
                            btnConnect.setEnabled(false);
                            sendTeamDataButton.setEnabled(false);
                            sendMatchDataButton.setEnabled(false);
                            sendDisconnectButton.setEnabled(false);
                            break;
                        case ChatController.STATE_LISTEN:
                        case ChatController.STATE_NONE:
                            setStatus("Not connected");
                            chatMessages.add("Not connected");
                            chatAdapter.notifyDataSetChanged();
                            btnConnect.setEnabled(true);
                            sendTeamDataButton.setEnabled(false);
                            sendMatchDataButton.setEnabled(false);
                            sendDisconnectButton.setEnabled(false);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    setStatus("### message_write ###");
                    byte[] writeBuf = (byte[]) msg.obj;
                    String writeMessage = new String(writeBuf);
                    chatMessages.add("SENT: " + writeMessage);
                    chatAdapter.notifyDataSetChanged();
                    break;
                case MESSAGE_READ:
                    setStatus("### message_read ###");
                    byte[] readBuf = (byte[]) msg.obj;
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    chatMessages.add("RCVD: " + readMessage);
                    chatAdapter.notifyDataSetChanged();
                    if (readMessage.equals(ACK_MESSAGE)) {
                        ackFlag = true;
                        break;
                    }
                    if (readMessage.equals(NAK_MESSAGE)) {
                        nakFlag = true;
                        break;
                    }
                    if (MergeReceivedData(readMessage)) {
                        sendMessage(ACK_MESSAGE);
                    } else {
                        sendMessage(NAK_MESSAGE);
                    }
                    break;
                case MESSAGE_DEVICE_OBJECT:
                    setStatus("### device_object ###");
                    connectingDevice = msg.getData().getParcelable(DEVICE_OBJECT);
                    chatMessages.add("Connected to " + connectingDevice.getName());
                    chatAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), "Connected to " + connectingDevice.getName(), Toast.LENGTH_LONG).show();
                    btnConnect.setEnabled(false);
                    sendTeamDataButton.setEnabled(true);
                    sendMatchDataButton.setEnabled(true);
                    sendDisconnectButton.setEnabled(true);
                    break;
                case MESSAGE_TOAST:
                    setStatus("### message_toast ###");
                    String msgValueMT = msg.getData().getString("toast");
                    chatMessages.add(msgValueMT);
                    chatAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), msgValueMT, Toast.LENGTH_LONG).show();
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
                    Toast.makeText(this, "Bluetooth still disabled, turn off application!", Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

    private void sendMessage(String message) {
        if (chatController.getState() != ChatController.STATE_CONNECTED) {
            chatMessages.add("Connection lost!");
            chatAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Connection lost!", Toast.LENGTH_LONG).show();
            return;
        }
        if (message.length() > 0) {
            if (message.equals(ACK_MESSAGE) || message.equals(NAK_MESSAGE)) {
                byte[] sendAckNak = message.getBytes();
                chatController.write(sendAckNak);
                return;
            }
            ackFlag = false;
            nakFlag = false;
            byte[] send = message.getBytes();
            chatController.write(send);
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (Exception ex) {
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

    private boolean MergeReceivedData(String message) {
        try {
            JSONArray dataArray = new JSONArray(message);
            if (dataArray.getString(0).equals(DBHelper.SYNC_HEADER_TEAM)) {
                chatAdapter.notifyDataSetChanged();
                int addCount = 0;
                for (int i = 1; i < dataArray.length(); i++) {
                    JSONObject newRow = dataArray.getJSONObject(i);
                    int teamNumber = newRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_NUMBER);
                    JSONObject existingRow = mDBHelper.getTeamInfo(teamNumber);
                    if (existingRow == null) {
                        newRow.remove(DBContract.TableTeamInfo._ID); // can't save another device's id values
                        // subtract one from version, it will be added back upon save
                        newRow.put(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION,
                                newRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION) - 1);
                        mDBHelper.updateTeamInfo(newRow);
                        addCount++;
                    } else {
                        if (newRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION) >
                                existingRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION)) {
                            newRow.put(DBContract.TableTeamInfo._ID, existingRow.getInt(DBContract.TableTeamInfo._ID));
                            // subtract one from version, it will be added back upon save
                            newRow.put(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION,
                                    newRow.getInt(DBContract.TableTeamInfo.COLNAME_TEAM_VERSION) - 1);
                            mDBHelper.updateTeamInfo(newRow);
                            addCount++;
                        }
                    }
                }
                chatMessages.add(String.format("%d rows added", addCount));
                chatAdapter.notifyDataSetChanged();
            } else if (dataArray.getString(0).equals(DBHelper.SYNC_HEADER_MATCH)) {
                chatAdapter.notifyDataSetChanged();
                int addCount = 0;
                for (int i = 1; i < dataArray.length(); i++) {
                    JSONObject newRow = dataArray.getJSONObject(i);
                    int teamNumber = newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_TEAM);
                    int matchNumber = newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_NUMBER);
                    JSONObject existingRow = mDBHelper.getMatchInfo(teamNumber, matchNumber);
                    if (existingRow == null) {
                        newRow.remove(DBContract.TableMatchInfo._ID); // can't save another device's id values
                        // subtract one from version, it will be added back upon save
                        newRow.put(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION,
                                newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION) - 1);
                        mDBHelper.updateMatchInfo(newRow);
                        addCount++;
                    } else {
                        if (newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION) >
                                existingRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION)) {
                            newRow.put(DBContract.TableMatchInfo._ID, existingRow.getInt(DBContract.TableMatchInfo._ID));
                            // subtract one from version, it will be added back upon save
                            newRow.put(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION,
                                    newRow.getInt(DBContract.TableMatchInfo.COLNAME_MATCH_VERSION) - 1);
                            mDBHelper.updateMatchInfo(newRow);
                            addCount++;
                        }
                    }
                }
                chatMessages.add(String.format("%d rows added", addCount));
                chatAdapter.notifyDataSetChanged();
            } else {
                chatMessages.add(dataArray.getString(0));
                chatMessages.add(String.format("Unknown identifier: %s", dataArray.getString(0)));
                chatAdapter.notifyDataSetChanged();
                throw new DataFormatException(String.format("Unknown identifier: %s", dataArray.getString(0)));
            }
            return true;
        } catch (Exception ex) {
            Toast.makeText(this, "Merge error! " + ex.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
    }
}
