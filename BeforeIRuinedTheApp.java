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


public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "MainActivity";


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

            }
        });
    }
}
