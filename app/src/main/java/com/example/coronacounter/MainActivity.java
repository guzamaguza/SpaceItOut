package com.example.coronacounter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.lang.Math;
import java.util.TimerTask;
import android.os.Handler;
import java.util.logging.LogRecord;

import android.os.Vibrator;



public class MainActivity extends AppCompatActivity {
    int counter = 0;
    
    /*
    public void timer( String[] args ){
        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(
            new TimerTask() {
            View myView = findViewById(R.id.broadcastReceiver);
            myView.startDiscovery();
        },0,30000);//every 30 seconds
    }
    */

    Handler handler = new Handler();
    private Runnable periodicUpdate = new Runnable () {
        @Override
        public void run() {
            // scheduled another events to be in 10 seconds later
            handler.postDelayed(periodicUpdate, 10*1000); //milliseconds
                    // below is whatever you want to do

        }
    };


    TextView alertTextView;
    TextView violationTextView;
    TextView counterTextView;

    ListView listView;
    TextView statusTextView;
    Button searchButton;
    ArrayList<String> bluetoothDevices = new ArrayList<>();
    ArrayList<String> addresses = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    BluetoothAdapter bluetoothAdapter;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action",action);


            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                statusTextView.setText("Finished");
                searchButton.setEnabled(true);
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress();
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI,Short.MIN_VALUE));
                //Log.i("Device Found","Name: " + name + " Address: " + address + " RSSI: " + rssi);

                //Integer rssiNum = rssi;
                //System.out.println(rssi);
                double RSSI= Integer.parseInt(rssi);
                double num = -69 - RSSI;
                double denom = 10*2;
                double exponent = num/denom;
                double base = 10;
                double distance_m = Math.pow(base,exponent);
                double distance_ft = distance_m * 3.28;
                String distance = Double.toString(distance_ft);
                String distance_round = distance.substring(0, (distance.length() - 12));
                //double distance_ft_round = Math.round(distance_ft , 2);
                //String distance_ft_round = Double.toString(distance_ft);
                //String output_distance = String.format("%.2f", distance_ft_round);
                //DecimalFormat formater = new DecimalFormat("0.00");
                //double counter = 0;


                if (distance_ft < 6.00) {
                    // Get instance of Vibrator from current Context
                    Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                    TextView text = (TextView) findViewById(R.id.counterTextView);
                    counter = counter + 1;
                    text.setText(String.valueOf(counter));

                    final TextView alert = findViewById(R.id.alertTextView);
                    alert.setVisibility(View.VISIBLE);

/*
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    alert.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    },5000);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isDestroyed() && !isFinishing()) {
                                alert.setVisibility(View.INVISIBLE);
                            }
                        }
                    },5000);

*/
                }

                    if (!addresses.contains(address)) {
                    addresses.add(address);
                    String deviceString = "";
                    if (name == null || name.equals("")) {
                        //deviceString = address + " - RSSI " + rssi + "dBm" + distance_ft;
                        deviceString = "Device:   " + address + "              " + "Distance: " + distance_round + "  Feet Away!!!";
                        /*
                        if (distance_ft < 6.00) {
                            // Get instance of Vibrator from current Context
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 400 milliseconds
                            v.vibrate(500);


                            //alertTextView.setVisibility(View.VISIBLE);
                            //alertTextView.postDelayed(new Runnable() {
                            //    public void run() {
                            //        alertTextView.setVisibility(View.VISIBLE);
                            //    }
                            //}, 7000);

                            //findViewById(alertTextView).setVisibility(View.VISIBLE);
                            //TextView textView = (TextView)findViewById(R.id.alertTextView);
                            //textView.setVisibility(TextView.VISIBLE);

                            //counterTextView.setText("One");
                            //counter = counter + 1;
                            //String counter_out = String.valueOf(counter);
                            //counterTextView.setText("Hi");
                        }
                        */

                    } else {
                        //deviceString = name + " - RSSI " + rssi + "dBm" + distance_ft;
                        deviceString = "Device:   " + address + "              " + "Distance: " + distance_round + "  Feet Away!!!";


                        /*
                        if (distance_ft < 6.00) {
                            // Get instance of Vibrator from current Context
                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 400 milliseconds
                            v.vibrate(500);


                            //alertTextView.setVisibility(View.VISIBLE);
                            //TextView textView = (TextView)findViewById(R.id.alertTextView);
                            //textView.setVisibility(TextView.VISIBLE);

                            //counterTextView.setText("One");
                            //counter = counter + 1;
                            //String counter_out = String.valueOf(counter);
                            //counterTextView.setText("Hi");
                        }
                        */
                    }


                    bluetoothDevices.add(deviceString);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public void searchClicked(View view) {
        statusTextView.setText("Searching...");
        searchButton.setEnabled(false);
        bluetoothDevices.clear();
        addresses.clear();
        bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        statusTextView = findViewById(R.id.statusTextView);
        searchButton = findViewById(R.id.searchButton);

        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,bluetoothDevices);

        listView.setAdapter(arrayAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);

    }

    //@Override
    //protected void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);
    //    setContentView(R.layout.activity_main);
    //}


}
