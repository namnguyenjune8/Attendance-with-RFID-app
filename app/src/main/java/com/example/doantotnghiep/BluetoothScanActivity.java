package com.example.doantotnghiep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothScanActivity extends AppCompatActivity {


    public static final String TAG = com.example.doantotnghiep.BluetoothScanActivity.class.getSimpleName();

    BluetoothAdapter mBluetoothAdapter;
    IntentFilter filter;
    BroadcastReceiver mReceiver;
    String[] name;
    Handler bluetoothIn;
    ArrayList<String> mathe;
    Bundle scan_data;
    int countScans = 0;
    int numCountScans;
    int value;
    RippleBackground rippleBackground;

    String batchID;
    boolean selectDateCheck = false;
    Date selectedDate;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    //DatabaseHelper dbHandler = new DatabaseHelper(MainActivity.this);

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String for MAC address
    private static String address;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_scan);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(getBaseContext(), R.color.main_blue));
        }
        batchID = getIntent().getStringExtra("Batch ID");
        numCountScans = getIntent().getIntExtra("Number Scans", 3);
        value = getIntent().getIntExtra("Value", 1);
        selectDateCheck = getIntent().getBooleanExtra("Manual Date", false);
        selectedDate = (Date) getIntent().getSerializableExtra("Selected Date");
        //Toast.makeText(this, numCountScans + " " + value, Toast.LENGTH_LONG).show();

        rippleBackground = (RippleBackground) findViewById(R.id.content);

        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        init();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Thiết bị của bạn không hỗ trợ Bluetooth!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                turnOnBT();
            }
        }

        scan_data = new Bundle();
        name = new String[100];
        mathe = new ArrayList<String>();
        Log.d(TAG, "onCreate: ");
        searchDevices();
        // nhan du lieu tu thiet bi
        bluetoothIn = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                    //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string

                        int dataLength = dataInPrint.length();                          //get length of data received

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String mathe = recDataString.substring(1, 8);             //get sensor value from string between indices 1-5
                            if (countScans == numCountScans - 1) {
                                rippleBackground.stopRippleAnimation();
                                Intent in = new Intent(com.example.doantotnghiep.BluetoothScanActivity.this, com.example.doantotnghiep.MarkStudentsActivity.class);
                                in.putExtra("Batch ID", batchID);
                                in.putExtra("Value", value);
                                Intent intent = null;
                                intent.putExtra("Manual Date", selectDateCheck);
                                intent.putExtra("Selected Date", selectedDate);
                                startActivity(in);
                                finish();
                            } else {
                                countScans++;
                                Log.d("Mark", "" + countScans);
                                mBluetoothAdapter.startDiscovery();
                            }

                        }


                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }


    private void turnOnBT() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        BT.launch(enableBtIntent);
    }

    public void init() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Tạo BroadCastReceiver cho ACTION_FOUND
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals((action))) {
                    //Nhận tín hiệu từ thiết bị Bluetooth
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    //Check tên sinh viên dựa trên mã thẻ và show trên listview
                    if (!mathe.contains(device.getAddress().toUpperCase()))
                        mathe.add(device.getAddress().toUpperCase());
                    Toast.makeText(context, device.getName(), Toast.LENGTH_SHORT).show();
                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals((action))) {
                    if (mBluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                        turnOnBT();
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals((action))) {
                    if (countScans == numCountScans - 1) {
                        rippleBackground.stopRippleAnimation();
                        Intent in = new Intent(com.example.doantotnghiep.BluetoothScanActivity.this, com.example.doantotnghiep.MarkStudentsActivity.class);
                        in.putExtra("Batch ID", batchID);
                        in.putExtra("Value", value);
                        in.putStringArrayListExtra("MA THE's", mathe);
                        intent.putExtra("Manual Date", selectDateCheck);
                        intent.putExtra("Selected Date", selectedDate);
                        startActivity(in);
                        finish();
                    } else {
                        countScans++;
                        Log.d("Mark", "" + countScans);
                        mBluetoothAdapter.startDiscovery();
                    }
                }
            }
        };
        filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        if (mBluetoothAdapter != null)
            mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(mReceiver);
    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==RESULT_CANCELED)
//        {
//            Toast.makeText(getApplicationContext(),"Bluetooth must be enabled!", Toast.LENGTH_SHORT).show();
//            finish();
//        }
//    }
    ActivityResultLauncher<Intent> BT = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {


                    if (result.getResultCode() == RESULT_CANCELED) {
                        {
                            Toast.makeText(getApplicationContext(), "Phải mở Bluetooth!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                }
            });

    public void searchDevices() {
        rippleBackground.startRippleAnimation();
        if (mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
        mBluetoothAdapter.startDiscovery();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }
    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called

    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");

    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
    }
}
