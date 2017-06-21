package com.a2xia.shake2048;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    RelativeLayout rl;
    final int GAMEBOARD_DIMENSION = 1000;
    final int GAMELOOPRATE = 25; //40 fps

    GameLoop myGameLoop;
    Timer myGameLoopTimer;

    TextView tvAccelerometer;
    AccelerometerEventListener accListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);

        rl = (RelativeLayout) findViewById(R.id.layout1);
        rl.getLayoutParams().width = GAMEBOARD_DIMENSION;
        rl.getLayoutParams().height = GAMEBOARD_DIMENSION;

        ImageView gameBoard = new ImageView(getApplicationContext());
        gameBoard.setImageResource(R.drawable.gameboard);
        rl.addView(gameBoard);

        tvAccelerometer = new TextView(getApplicationContext());
        tvAccelerometer.setText("Accelerometer Instantaneous Readings");
        tvAccelerometer.setTextColor(Color.BLACK);
        tvAccelerometer.setTextSize(40.0f);

        rl.addView(tvAccelerometer);

        myGameLoop = new GameLoop(this, rl, getApplicationContext());
        myGameLoopTimer = new Timer();
        myGameLoopTimer.schedule(myGameLoop, GAMELOOPRATE, GAMELOOPRATE);


        SensorManager sensorManager =(SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor Accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accListener = new AccelerometerEventListener(tvAccelerometer, myGameLoop);
        sensorManager.registerListener(accListener, Accelerometer,  SensorManager.SENSOR_DELAY_GAME);

    }

}
