package com.example.mohamedxyz.squidxicamp18;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat; //todo: This might be causing problems
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

//(STUPID)
//Remove

public class MainActivity extends AppCompatActivity {

    //Main UI
    TextView DataDisplay;
    Switch TouchSensorSwitch;
    ToggleButton ButtonsJoystickToggleBtn, SendingStatusToggleBtn;
    Button ConnectArduinoBT, SendTextBtn;
    //Arrows Controls
    ImageButton FLbtn, Fbtn, FRbtn, Rbtn, BRbtn, Bbtn, BLbtn, Lbtn;
    ImageView Logo;
    SeekBar Speedlvl;
    TextView SpeedlvlText;
    int Speed = 255;
    //Joystick
    RelativeLayout JoystickLayout;
    JoyStickClass JoystickController;
    //RV Sensor
    private SensorManager sensorManager;
    private Sensor rotationVectorSensor;
    private SensorEventListener rvListener;
    private int SensorDelay = SensorManager.SENSOR_DELAY_FASTEST;//NORMAL or FASTEST
    private float[] ZeroPos = {0, 0, 0};
    private float[] LastSendRotation = {0, 0};
    private int DegDiffToSend = 5;
    private boolean SetZeroPosState = false;
    private boolean ActivateSensor = false;
    Button SetZeroPos;
    //Bluetooth
    BTService BTService =new BTService(this);
    //MISC
    ConstraintLayout layout;
    private boolean SendingStatus = false;



    @SuppressLint("ClickableViewAccessibility") //remove this
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        //Main UI
        DataDisplay = findViewById(R.id.DataDisplay);
        TouchSensorSwitch = findViewById(R.id.TouchSensorSwitch);
        ButtonsJoystickToggleBtn = findViewById(R.id.ButtonsJoystickToggleBtn);
        SendTextBtn = findViewById(R.id.SendTextBtn);
        SendingStatusToggleBtn = findViewById(R.id.SendingStatusToggleBtn);
        ConnectArduinoBT = findViewById(R.id.ConnectArduinoBT);
        //Arrows Controls
        FLbtn = findViewById(R.id.FLbtn);
        Fbtn = findViewById(R.id.Fbtn);
        FRbtn = findViewById(R.id.FRbtn);
        Rbtn = findViewById(R.id.Rbtn);
        BRbtn = findViewById(R.id.BRbtn);
        Bbtn = findViewById(R.id.Bbtn);
        BLbtn = findViewById(R.id.BLbtn);
        Lbtn = findViewById(R.id.Lbtn);
        Logo = findViewById(R.id.Logo);
        Speedlvl = findViewById(R.id.Speedlvl);
        SpeedlvlText = findViewById(R.id.SpeedlvlText);
        //Joystick
        JoystickLayout = findViewById(R.id.JoystickLayout);
        final int joystickSize = 750; //todo: Make it dynamic
        //RV Sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        SetZeroPos = findViewById(R.id.SetZeroPos);
        //MISC
        layout = findViewById(R.id.ConstraintLayout);
        final Drawable JoystickBG = ContextCompat.getDrawable(this, R.drawable.image_button_bg);

        //Make Arrows Color Change when clicked (STUPID)
        SetOnClickColorFilter(FLbtn);
        SetOnClickColorFilter(Fbtn);
        SetOnClickColorFilter(FRbtn);
        SetOnClickColorFilter(Rbtn);
        SetOnClickColorFilter(BRbtn);
        SetOnClickColorFilter(Bbtn);
        SetOnClickColorFilter(BLbtn);
        SetOnClickColorFilter(Lbtn);

        //DebugLine (Remove)
        //DataDisplay.setText(String.valueOf(JoystickLayout.getWidth()));

        //Initializing Joystick
        JoystickController = new JoyStickClass(getApplicationContext(), JoystickLayout, R.drawable.image_button);
        JoystickController.setStickSize((int) (joystickSize * 0.3), (int) (joystickSize * 0.3));
        JoystickController.setLayoutSize(joystickSize, joystickSize);
        JoystickController.setLayoutAlpha(255);//150
        JoystickController.setStickAlpha(150);//100
        JoystickController.setOffset((int) (joystickSize * 0.18));//90 todo:Check What it does
        JoystickController.setMinimumDistance((int) (joystickSize * 0.1));//50 todo:Check What it does


        JoystickLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                JoystickController.drawStick(arg1);
                if(arg1.getAction() == MotionEvent.ACTION_DOWN || arg1.getAction() == MotionEvent.ACTION_MOVE) {
                    int Y = (int) LimitRange(JoystickController.getY() * -255 / (joystickSize/2), -255, 255);
                    int X = (int) LimitRange(JoystickController.getX() * 255 / (joystickSize/2),-255 , 255);
                    if((Y > 0 && Y < 30) || (Y < 0 && Y > -30))   Y = 0;
                    if((X > 0 && X < 30) || (X < 0 && X > -30)) X = 0;
                    String TempS = String.valueOf(Y +","+ X);
                    DataDisplay.setText(TempS);
                    if(SendingStatus) BTService.SendText(TempS);
                } else if(arg1.getAction() == MotionEvent.ACTION_UP) {
                    DataDisplay.setText(R.string.DataDisplay);
                    if(SendingStatus) BTService.SendText("0,0");
                }
                return true;
            }
        });

        //Initializing RV Sensor
        if(rotationVectorSensor == null){
            ToastMsg("RV Sensor not available");
            TouchSensorSwitch.setEnabled(false);
        }else {
            rvListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    float[] ToSendRotation = new float[2];
                    float[] rotationMatrix = new float[16];
                    float[] remappedRotationMatrix = new float[16];
                    float[] orientations = new float[3];
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values);
                    SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, remappedRotationMatrix);
                    SensorManager.getOrientation(remappedRotationMatrix, orientations);
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.CEILING);
                    for (int i = 0; i < 3; i++) {
                        orientations[i] = (float) (Math.toDegrees(orientations[i]));//Find a better way
                    }
                    if (SetZeroPosState) {
                        System.arraycopy(orientations, 0, ZeroPos, 0, 3);
                        SetZeroPosState = false;
                    }
                    for (int i = 0; i < 3; i++) {
                        orientations[i] -= ZeroPos[i];
                        //orientations[i] = Float.valueOf(df.format(orientations[i]));//Find a better way
                    }
                    if (LastSendRotation[0] + DegDiffToSend < orientations[1] || LastSendRotation[0] - DegDiffToSend > orientations[1]) {
                        ToSendRotation[0] = orientations[1];
                    } else {
                        ToSendRotation[0] = LastSendRotation[0];
                    }
                    if (LastSendRotation[1] + DegDiffToSend < orientations[2] || LastSendRotation[1] - DegDiffToSend > orientations[2]) {
                        ToSendRotation[1] = orientations[2];
                    } else {
                        ToSendRotation[1] = LastSendRotation[1];
                    }
                    if (!Arrays.equals(LastSendRotation, ToSendRotation)) {
                        //String TempS = String.valueOf(ToSendRotation[0]+","+ToSendRotation[1]);
                        String TempS = String.valueOf(
                                Float.valueOf(df.format(LimitRange(ToSendRotation[0], -85, 85) *255/85))
                                        +","+ Float.valueOf(df.format(LimitRange(ToSendRotation[1], -60, 60)*255/60)));
                        DataDisplay.setText(TempS);
                        if (SendingStatus) BTService.SendText(TempS);
                    }
                    LastSendRotation = ToSendRotation;
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {
                }
            };
        }

        //Main UI
        TouchSensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ArrayList<View> views = GetViewsByTag(layout, "Disable");
                for(int i = 0; i < views.size(); i++){
                    views.get(i).setEnabled(! views.get(i).isEnabled());
                }
                if(b){
                    sensorManager.registerListener(rvListener,rotationVectorSensor, SensorDelay);
                    ActivateSensor = true;
                    SetZeroPos.setVisibility(View.VISIBLE);
                    setActivityBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                    SetColorFilter(getResources().getColor(R.color.GrayedOut));
                    JoystickBG.setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.GrayedOut), PorterDuff.Mode.MULTIPLY));
                    ToastMsg("Sensor Input Enabled");
                }else{
                    sensorManager.unregisterListener(rvListener);
                    ActivateSensor = false;
                    SetZeroPos.setVisibility(View.INVISIBLE);
                    JoystickBG.setColorFilter(new PorterDuffColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.MULTIPLY));
                    setActivityBackgroundColor(0xffffffff);
                    SetColorFilter(0);
                    DataDisplay.setText(R.string.DataDisplay);
                    ToastMsg("Touch Input Enabled");
                }
            }
        });
        ButtonsJoystickToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //(STUPID)
                //todo: Maybe Add animation
                if(b){
                    FLbtn.setVisibility(View.INVISIBLE);
                    Fbtn .setVisibility(View.INVISIBLE);
                    FRbtn.setVisibility(View.INVISIBLE);
                    Rbtn .setVisibility(View.INVISIBLE);
                    BRbtn.setVisibility(View.INVISIBLE);
                    Bbtn .setVisibility(View.INVISIBLE);
                    BLbtn.setVisibility(View.INVISIBLE);
                    Lbtn .setVisibility(View.INVISIBLE);
                    Logo .setVisibility(View.INVISIBLE);
                    Speedlvl .setVisibility(View.INVISIBLE);
                    SpeedlvlText .setVisibility(View.INVISIBLE);

                    JoystickLayout.setVisibility(View.VISIBLE);

                    DataDisplay.setText(R.string.DataDisplay);
                }else{
                    FLbtn.setVisibility(View.VISIBLE);
                    Fbtn .setVisibility(View.VISIBLE);
                    FRbtn.setVisibility(View.VISIBLE);
                    Rbtn .setVisibility(View.VISIBLE);
                    BRbtn.setVisibility(View.VISIBLE);
                    Bbtn .setVisibility(View.VISIBLE);
                    BLbtn.setVisibility(View.VISIBLE);
                    Lbtn .setVisibility(View.VISIBLE);
                    Logo .setVisibility(View.VISIBLE);
                    Speedlvl .setVisibility(View.VISIBLE);
                    SpeedlvlText .setVisibility(View.VISIBLE);

                    JoystickLayout.setVisibility(View.INVISIBLE);
                }

            }
        });
        SendingStatusToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    SendingStatus = true;
                    ToastMsg("Sending Data");
                }else{
                    SendingStatus = false;
                    ToastMsg("Stopped Sending Data");
                }
            }
        });
        SendTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                final View mView = getLayoutInflater().inflate(R.layout.sendtext_dialog, null);
                final EditText editText = mView.findViewById(R.id.editText);
                final Button button = mView.findViewById(R.id.button);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DataDisplay.setText(editText.getText());
                        if(SendingStatus) BTService.SendText(editText.getText().toString());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        ConnectArduinoBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(BTService.BTinit()){
                    if(BTService.BTconnect()){
                        Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //MISC
        SetZeroPos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetZeroPosState = true;
                ToastMsg("Set");
            }
        });
        Speedlvl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //SpeedlvlText.setText(""+ i+ "%");
                SpeedlvlText.setText(String.valueOf( i + "%"));
                Speed = 255 * i/100;
                DataDisplay.setText(String.valueOf(Speed));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void ToastMsg(String Msg){
        Toast.makeText(MainActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }
    private void setActivityBackgroundColor(int color) {
        this.getWindow().getDecorView().setBackgroundColor(color);
    }
    private static ArrayList<View> GetViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(GetViewsByTag((ViewGroup) child, tag));
            }
            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }
        }
        return views;
    }
    //THIS IS STUPID!!!! (STUPID)
    private void SetColorFilter(int color){
        FLbtn.setColorFilter(color);
        Fbtn .setColorFilter(color);
        FRbtn.setColorFilter(color);
        Rbtn .setColorFilter(color);
        BRbtn.setColorFilter(color);
        Bbtn .setColorFilter(color);
        BLbtn.setColorFilter(color);
        Lbtn .setColorFilter(color);
    }
    //THIS IS BEYOND STUPID!!!! (STUPID)
    @SuppressLint("ClickableViewAccessibility")
    private void SetOnClickColorFilter(final ImageButton imgbtn) {
        imgbtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    imgbtn.setColorFilter(getResources().getColor(R.color.colorAccent));
                    SendSpeedFromName(imgbtn);
                    return true;

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) { //might be an else if
                    imgbtn.setColorFilter(Color.argb(255, 0, 0, 0));
                    SendSpeedFromName(DataDisplay);
                    return true;
                }
                return false;
            }
        });
    }
    private void SendSpeedFromName(View v){
        String Text = v.getResources().getResourceEntryName(v.getId());
        int DataToSendX = 0, DataToSendY = 0;
        switch (Text) {
            case "FLbtn":
                //255,-127
                DataToSendX = Speed;
                DataToSendY = -Speed / 2;
                break;
            case "Fbtn":
                //255,0
                DataToSendX = Speed;
                break;
            case "FRbtn":
                //255,127
                DataToSendX = Speed;
                DataToSendY = Speed / 2;
                break;
            case "Rbtn":
                //127,255
                DataToSendX = Speed /2;
                DataToSendY = Speed;
                break;
            case "BRbtn":
                //-255,127
                DataToSendX = -Speed;
                DataToSendY = Speed / 2;
                break;
            case "Bbtn":
                //-255,0
                DataToSendX = -Speed;
                break;
            case "BLbtn":
                //-255,-127
                DataToSendX = -Speed;
                DataToSendY = -Speed / 2;
                break;
            case "Lbtn":
                //127,-255
                DataToSendX = Speed / 2;
                DataToSendY = -Speed;
                break;
            case "DataDisplay":
                //0,0
                break;
            default:
                ToastMsg(String.valueOf("SendSpeedFromName: Error(" + Text + ")"));
        }
        DataDisplay.setText(String.valueOf(DataToSendX + "," + DataToSendY));
        if(SendingStatus) BTService.SendText(String.valueOf(DataToSendX + "," + DataToSendY));
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(ActivateSensor) sensorManager.registerListener(rvListener,rotationVectorSensor, SensorDelay);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(rvListener);
    }
    //There's probably a builtin function that does this
    private float LimitRange(float f, int l1, int l2){
        if(f > l2){
            return l2;
        }else if(f < l1){
            return l1;
        }
        return f;
    }
}
