package com.example.mohamedxyz.squidxicamp18;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ImageButton[] Arrows;
    ToggleButton SensorTouchToggleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        Arrows[0] = findViewById(R.id.FLbtn);
        Arrows[1] = findViewById(R.id.Fbtn);
        Arrows[2] = findViewById(R.id.FRbtn);
        Arrows[3] = findViewById(R.id.Rbtn);
        Arrows[4] = findViewById(R.id.BRbtn);
        Arrows[5] = findViewById(R.id.Bbtn);
        Arrows[6] = findViewById(R.id.BLbtn);
        Arrows[7] = findViewById(R.id.Lbtn);

        SensorTouchToggleBtn = findViewById(R.id.SensorTouchToggleBtn);//it probably shouldn't be final
        final TextView LogTextView = findViewById(R.id.LogTextView);

        for(int i = 0; i < 8; i++){
            //ButtonTint(i);
        }


        final int SensorTouchToggleBtnID = SensorTouchToggleBtn.getId();
        SensorTouchToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ConstraintLayout layout = findViewById(R.id.ConstraintLayout);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View child = layout.getChildAt(i);
                    if(child.getId() == SensorTouchToggleBtnID ) continue;
                    //if(child instanceof ImageButton) child.setColorFilter(Color.argb(150, 255, 255, 255));
                    child.setEnabled(! child.isEnabled());
                }
                if(b){
                    ToastMsg("Sensor Input Enabled");
                    for(int i = 0; i < 8; i++){
                        //Arrows[i].setColorFilter(Color.argb(150,255,255,255));
                    }

                }else{
                    ToastMsg("Touch Input Enabled");
                    for(int i = 0; i < 8; i++){
                        //Arrows[i].setColorFilter(Color.argb(255,0,0,0));
                    }
                }
            }
        });
        //LogTextView.append(Fbtn.getTag().toString());

    }
    private void ButtonTint(final int Btni){
        Arrows[Btni].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    Arrows[Btni].setColorFilter(Color.argb(255, 4, 4, 142));
                    return true;
                }
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    Arrows[Btni].setColorFilter(Color.argb(255, 0, 0, 0));
                    return true;
                }
                return false;
            }
        });
    }
    private void ToastMsg(String Msg){
        Toast.makeText(MainActivity.this, Msg, Toast.LENGTH_SHORT).show();
    }
}
