package com.example.mohamedxyz.myapplication;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "MainActivity";
    private final String DEVICE_ADDRESS = "00:21:13:00:EA:2C"; //MAC Address of Bluetooth Module +++ This is a very stupid idea
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PackageManager packageManager = getPackageManager();
        boolean gyroExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        boolean AccelExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        final EditText TextBoxToSend = findViewById(R.id.TextBoxToSend);
        final Button SendTextButton = findViewById(R.id.SendTextButton);
        final Button ClearLogBtn = findViewById(R.id.ClearLogBtn);
        final TextView SendLog = findViewById(R.id.SendLog);
        final EditText CustomBtnFunction = findViewById(R.id.CustomBtnFunction);
        final Button SetCustomBtnFunction = findViewById(R.id.SetCustomBtnFunction);
        final TextView SensorsAvailability = findViewById(R.id.SensorsAvailability);
        final Button BluetoothButton = findViewById(R.id.BluetoothButton);


        String TempSensorsAvailability = "Gyroscope/Accelerometer: " + gyroExists + "/" + AccelExists;
        SensorsAvailability.setText(TempSensorsAvailability);

        SendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onCreate: ChangingText");
                try {
                    outputStream.write(TextBoxToSend.getText().toString().getBytes()); //transmits the value of command to the bluetooth module
                    //try without toString***************
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Toast.makeText(MainActivity.this, "Text Sent: " + TextBoxToSend.getText().toString(), Toast.LENGTH_SHORT).show();
                if (SendLog.getText().toString().equals("")) {
                    SendLog.setText(TextBoxToSend.getText().toString());
                } else {
                    String Temp = SendLog.getText().toString() + ", " + TextBoxToSend.getText().toString();
                    SendLog.setText(Temp);
                }
                TextBoxToSend.setText("");
            }
        });
        ClearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Log Cleared", Toast.LENGTH_SHORT).show();
                SendLog.setText("");
            }
        });
        SetCustomBtnFunction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (! CustomBtnFunction.getText().toString().equals("")) {
                    final LinearLayout LL = findViewById(R.id.CustomBtnsLayout);
                    final Button button = new Button(MainActivity.this);
                    //button.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT));
                    button.setText(CustomBtnFunction.getText().toString());
                    button.setTextSize(12);
                    button.setTransformationMethod(null);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this, button.getText().toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    button.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            LL.removeView(button);
                            return true;
                        }
                    });
                    LL.addView(button);
                    CustomBtnFunction.setText("");
                    Toast.makeText(MainActivity.this, "Button Created", Toast.LENGTH_SHORT).show();
                }
            }
        });
        BluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BTinit()){
                    BTconnect();
                }
            }
        });
    }
    //Initializes bluetooth module
    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter == null) //Checks if the device supports bluetooth
        {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Checks if bluetooth is enabled. If not, the program will ask permission from the user to enable it
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter,0);

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Checks for paired bluetooth devices
        {
            Toast.makeText(getApplicationContext(), "Please pair the device first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect()
    {
        boolean connected = true;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Creates a socket to handle the outgoing connection
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Connection to bluetooth device successful", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //gets the output stream of the socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }
}
