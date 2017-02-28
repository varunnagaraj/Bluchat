package com.example.varunnagaraj.bluchat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;
import java.util.StringTokenizer;

/**
 * Created by Varun Nagaraj on 27-02-2017.
 */

public class ScanDevices  extends Activity{

    private TextView pairedDeviceTitle, newDeviceTitle;
    private ListView pairedDeviceList, newDeviceList;
    private Button scanDevicesButton;
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private ArrayAdapter<String> newDevicesArrayAdapter;

    private TextView progressText;
    private ProgressBar spinner;

    public DeviceDBHandler dbHandler;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            updateDB();
        }
    };

    public static String DEVICE_ADDRESS = "deviceAddress";
    public static String DEVICE_NAMES = "devicename";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_devices);
        dbHandler = new DeviceDBHandler(this,null,null,1);

        // Quick permission check
        int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
        permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
        if (permissionCheck != 0) {

            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
        }
//        startDiscovery();
        pairedDeviceTitle = (TextView) findViewById(R.id.pairedDeviceTitle);
        newDeviceTitle = (TextView) findViewById(R.id.newDeviceTitle);

        pairedDeviceList = (ListView) findViewById(R.id.pairedDeviceList);
        newDeviceList= (ListView) findViewById(R.id.newDeviceList);

        scanDevicesButton = (Button) findViewById(R.id.scanDevicesButton);

        scanDevicesButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        newDeviceList.clearChoices();
                        startDiscovery();
                        scanDevicesButton.setVisibility(View.GONE);
                        waitforscanning(v);
                    }
                }
        );

        initializeValues();

        pairedDeviceList.setOnItemClickListener(mDeviceClickListener);
        newDeviceList.setOnItemClickListener(mDeviceClickListener);



    }


    public void waitforscanning(View view){

        Runnable r = new Runnable() {
            @Override
            public void run() {
                long futureTime = System.currentTimeMillis() + 16000;
                while(System.currentTimeMillis() < futureTime){
                    synchronized (this){
                        try{
                            wait(futureTime-System.currentTimeMillis());
                        }catch(Exception e){}
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };

        Thread waitThread = new Thread(r);
        waitThread.start();
    }

    public void updateDB(){
        int count = newDevicesArrayAdapter.getCount();
        String devicename = null;
        for(int i=0;i<count;i++){
            devicename = newDevicesArrayAdapter.getItem(i);
            Devices devices = new Devices(devicename," "," ");
//            dbHandler.addDevice(devices);
        }
//        Toast.makeText(this, devicename, Toast.LENGTH_SHORT).show();
    }
    private void startDiscovery() {
//        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        progressText = (TextView) findViewById(R.id.progressText);
        newDeviceTitle.setVisibility(View.VISIBLE);

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        spinner.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
        bluetoothAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            bluetoothAdapter.cancelDiscovery();

            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            int count = newDevicesArrayAdapter.getCount();
            String devicename = null;
            for(int i=0;i<count;i++){
                devicename += newDevicesArrayAdapter.getItem(i) + "\n";
                Devices devices = new Devices(devicename," "," ");
//            dbHandler.addDevice(devices);
            }


            Intent intent = new Intent();
            intent.putExtra(DEVICE_ADDRESS, address);
            intent.putExtra(DEVICE_NAMES, devicename);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
    private void initializeValues() {
        pairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);
        newDevicesArrayAdapter = new ArrayAdapter<String>(this,
                R.layout.device_name);

        pairedDeviceList.setAdapter(pairedDevicesArrayAdapter);
        newDeviceList.setAdapter(newDevicesArrayAdapter);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(discoveryFinishReceiver, filter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter
                .getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            pairedDeviceTitle.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n"
                        + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired)
                    .toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    private final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevicesArrayAdapter.add(device.getName() + "\n"
                            + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                    .equals(action)) {
//                setProgressBarIndeterminateVisibility(false);
                spinner.setVisibility(View.GONE);
                progressText.setVisibility(View.GONE);
//                scanDevicesButton.setVisibility(View.VISIBLE);
                setTitle(R.string.select_device);
                if (newDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(
                            R.string.none_found).toString();
                    newDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };
}
