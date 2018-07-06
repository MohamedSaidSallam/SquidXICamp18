package com.example.mohamedxyz.myapplication;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "MainActivity";
    private final String DEVICE_ADDRESS="00:21:13:00:EA:2C";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private Sensor rotationVectorSensor;
    private SensorEventListener gyroscopeSensorListener;
    private SensorEventListener rvListener;

    private int SensorDelay = SensorManager.SENSOR_DELAY_NORMAL;//NORMAL or FASTEST

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        PackageManager packageManager = getPackageManager();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //u can check if null here
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        boolean gyroExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        boolean AccelExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        final EditText TextBoxToSend = findViewById(R.id.TextBoxToSend);
        final Button SendTextButton = findViewById(R.id.SendTextButton);
        final Button ClearLogBtn = findViewById(R.id.ClearLogBtn);
        final TextView SendLog = findViewById(R.id.SendLog);
        final EditText CustomBtnFunction = findViewById(R.id.CustomBtnFunction);
        final Button SetCustomBtnFunction = findViewById(R.id.SetCustomBtnFunction);
        final TextView SensorValuesTextView = findViewById(R.id.SensorValuesTextView);
        final TextView SensorsAvailability = findViewById(R.id.SensorsAvailability);
        final Button BluetoothButton = findViewById(R.id.BluetoothButton);

        String TempSensorsAvailability = "Gyroscope/Accelerometer: " + gyroExists + "/" + AccelExists;
        SensorsAvailability.setText(TempSensorsAvailability);

        gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                /*float Temp = sensorEvent.values[1] * 10;
                if((Temp < 0.1f && Temp > 0)) Temp = 0; //|| (Temp > -0.01f && Temp < 0)
                if((Temp > -0.1f && Temp < 0)) Temp = 0;
                SensorValuesTextView.setText(Float.toString(Temp));*/
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorDelay);

        rvListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float[] rotationMatrix = new float[16];
                float[] remappedRotationMatrix = new float[16];
                float[] orientations = new float[3];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
                SensorManager.getOrientation(remappedRotationMatrix, orientations);
                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                for(int i = 0; i < 3; i++) {
                    orientations[i] = Float.valueOf( df.format( (float)(Math.toDegrees(orientations[i])) ));//Find a better way
                }
                SensorValuesTextView.setText(Arrays.toString(orientations));

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        sensorManager.registerListener(rvListener,rotationVectorSensor, SensorDelay);

        SendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onCreate: ChangingText");
                if(SendText(TextBoxToSend.getText().toString())) {
                    if (SendLog.getText().toString().equals("")) {
                        SendLog.setText(TextBoxToSend.getText().toString());
                    } else {
                        String Temp = SendLog.getText().toString() + ", " + TextBoxToSend.getText().toString();
                        SendLog.setText(Temp);
                    }
                    TextBoxToSend.setText("");
                }
            }
        });
        ClearLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendLog.setText("");
                Toast.makeText(MainActivity.this, "Log Cleared", Toast.LENGTH_SHORT).show();
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
                            SendText(button.getText().toString());
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
                    if(BTconnect()){
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    public boolean BTinit() {

        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Device doesn't Support Bluetooth/App Can't use Bluetooth",Toast.LENGTH_LONG).show();
        }
        if(!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(),"Please Pair the Device first.",Toast.LENGTH_LONG).show();
        }else{
            for (BluetoothDevice iterator : bondedDevices) {
                if(iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
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
    public boolean SendText(String string){

        string.concat("\n"); //Check if it works without this
        if(string.equals("")) return false;
        try {
            outputStream.write(string.getBytes());
            Toast.makeText(MainActivity.this, "Text Sent: " + string, Toast.LENGTH_SHORT).show();
            return  true;
        } catch (IOException e) {
            e.printStackTrace(); //WHAT DOES THIS DO ?!?!?!?!
            Toast.makeText(MainActivity.this, "Error: Couldn't Send text.", Toast.LENGTH_SHORT).show();
            return  false;
        }
    }
    //"Good Practice"
    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorDelay);
        sensorManager.registerListener(rvListener,rotationVectorSensor, SensorDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(gyroscopeSensorListener);
        sensorManager.unregisterListener(rvListener);
    }
}
