package com.example.mohamedxyz.myapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "MainActivity";

    private SensorManager sensorManager;
    //private Sensor gyroscopeSensor;
    private Sensor rotationVectorSensor;
    //private SensorEventListener gyroscopeSensorListener;
    private SensorEventListener rvListener;

    int SensorDelay = SensorManager.SENSOR_DELAY_NORMAL;//NORMAL or FASTEST
    private float[] ZeroPos = {0, 0, 0};
    private boolean SetZeroPosState = false;
    private boolean StartSendingState = false;

    BTService BTService =new BTService(this);

    Button SendTextButton, ClearLogBtn, SetCustomBtnFunction, BluetoothButton, SetZeroPos, SendSensorData;
    EditText TextBoxToSend, CustomBtnFunction;
    TextView SendLog, SensorValuesTextView, SensorsAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//better to change the default settings
        setContentView(R.layout.activity_main);

        TextBoxToSend = findViewById(R.id.TextBoxToSend);
        SendTextButton = findViewById(R.id.SendTextButton);
        ClearLogBtn = findViewById(R.id.ClearLogBtn);
        SendLog = findViewById(R.id.SendLog);
        CustomBtnFunction = findViewById(R.id.CustomBtnFunction);
        SetCustomBtnFunction = findViewById(R.id.SetCustomBtnFunction);
        SensorValuesTextView = findViewById(R.id.SensorValuesTextView);
        SensorsAvailability = findViewById(R.id.SensorsAvailability);
        BluetoothButton = findViewById(R.id.BluetoothButton);
        SetZeroPos = findViewById(R.id.SetZeroPos);
        SendSensorData = findViewById(R.id.SendSensorData);

        PackageManager packageManager = getPackageManager();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //u can check if null here
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        boolean gyroExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        boolean AccelExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER);
        boolean OriExists = true;

        //Checks for Sensors Availability
        if(rotationVectorSensor == null) OriExists = false;
        String TempSensorsAvailability = "Gyroscope/Accelerometer/Ori: " + gyroExists + "/" + AccelExists + "/" + OriExists;
        SensorsAvailability.setText(TempSensorsAvailability);

        /*gyroscopeSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float Temp = sensorEvent.values[1] * 10;
                if((Temp < 0.1f && Temp > 0)) Temp = 0; //|| (Temp > -0.01f && Temp < 0)
                if((Temp > -0.1f && Temp < 0)) Temp = 0;
                SensorValuesTextView.setText(Float.toString(Temp));
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };*/

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
                    orientations[i] =  (float)(Math.toDegrees(orientations[i]));//Find a better way
                }
                if(SetZeroPosState){
                    System.arraycopy(orientations, 0, ZeroPos, 0, 3);
                    SetZeroPosState = false;
                }
                for(int i = 0; i < 3; i++) {
                    orientations[i] -= ZeroPos[i];
                    orientations[i] = Float.valueOf( df.format( orientations[i] ));//Find a better way
                }
                SensorValuesTextView.setText(Arrays.toString(orientations));
                //if(StartSendingState) BTService.SendText(Float.toString(orientations[0]), false);
                if(StartSendingState) BTService.SendText(Arrays.toString(orientations), false);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };

        SetZeroPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetZeroPosState = true;
            }
        });
        SendSensorData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartSendingState = !StartSendingState;
                //BTService.SendText(Float.toString(ZeroPos[1]) + Float.toString(ZeroPos[0]), true);
            }
        });

        SendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(TAG, "onCreate: ChangingText");
                if(BTService.SendText(TextBoxToSend.getText().toString(), true)) {
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
                            BTService.SendText(button.getText().toString(), true);
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
                if(BTService.BTinit()){
                    if(BTService.BTconnect()){
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
    //"Good Practice"
    @Override
    protected void onResume(){
        super.onResume();
        //sensorManager.registerListener(gyroscopeSensorListener, gyroscopeSensor, SensorDelay);
        sensorManager.registerListener(rvListener,rotationVectorSensor, SensorDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //sensorManager.unregisterListener(gyroscopeSensorListener);
        sensorManager.unregisterListener(rvListener);
    }
}
