package com.example.mohamedxyz.myapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/*Functions:
    BTinit
    BTconnect
    SendText
*/

public class BTService extends Activity {

    final String DEVICE_ADDRESS="00:21:13:00:EA:2C"; //BT module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    Activity activity;

    BTService(Activity activity){
        this.activity=activity;
    }
    /*public void startConnection() {
        // Create an intent for SearchActivity
        Intent intent = new Intent(activity, SearchActivity.class);
        //start SearchActivity through intent and expect for result.
        //The result is based on result code, which is REQUEST_DISCOVERY
        activity.startActivityForResult(intent, REQUEST_DISCOVERY);
    }*/

    public boolean BTinit() {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this.activity,"Device doesn't Support Bluetooth",Toast.LENGTH_LONG).show();
        }
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            this.activity.startActivityForResult(enableAdapter, 0);//THE PROBLEM -.-
            try {
                Thread.sleep(1000); //this takes too long
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty()) {
            Toast.makeText(this.activity,"Please Pair the Device first.",Toast.LENGTH_LONG).show();
        }else{
            for (BluetoothDevice iterator : bondedDevices) {
                if(iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        //if(! found) Toast.makeText(this.activity,"Didn't Connect",Toast.LENGTH_LONG).show(); //Testing this...
        return found;
    }
    public boolean BTconnect() {

        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
        }

        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }
    public boolean SendText(String string, boolean ShowToast){

        if(string.equals("")) return false;
        string = string.concat("\n"); //Check if it works without this
        try {
            outputStream.write(string.getBytes());
            if(ShowToast) Toast.makeText(this.activity, "Text Sent: " + string, Toast.LENGTH_SHORT).show();
            return  true;
        } catch (IOException e) {
            e.printStackTrace(); //WHAT DOES THIS DO ?!?!?!?!
            Toast.makeText(this.activity, "Error: Couldn't Send text.", Toast.LENGTH_SHORT).show();
            return  false;
        }
    }
}
