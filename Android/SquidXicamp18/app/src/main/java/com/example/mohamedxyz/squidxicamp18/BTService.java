package com.example.mohamedxyz.squidxicamp18;

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

//This was edit for better use in this project

public class BTService extends Activity {

    final String DEVICE_ADDRESS="00:21:13:00:EA:2C"; //BT module
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private boolean SuccConnection = false;

    Activity activity;

    BTService(Activity activity){
        this.activity=activity;
    }

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
                Thread.sleep(1000); //todo: this takes too long
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
        //Toast.makeText(this.activity,"Didn't Connect",Toast.LENGTH_LONG).show(); //Testing this...
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
            SuccConnection = true;
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
    public void SendText(String string){
        if(! SuccConnection || string.equals("")) return;
        //string = string.concat("\n"); //todo: Check if it works without this
        try {
            outputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace(); //WHAT DOES THIS DO ?!?!?!?!
            Toast.makeText(this.activity, "BTService Error: Couldn't Send text.", Toast.LENGTH_SHORT).show();
        }
    }
}
