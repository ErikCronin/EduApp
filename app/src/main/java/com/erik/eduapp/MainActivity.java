package com.erik.eduapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accSensor;
    private boolean isAccAvail, isNotFirstTime;
    private float lastX, lastY, lastZ;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Check if sensor is available
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            accSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isAccAvail = true;
        } else {
            isAccAvail = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float currentX = sensorEvent.values[0];
        float currentY = sensorEvent.values[1];
        float currentZ = sensorEvent.values[2];

        if(isNotFirstTime){
            float xDifference = Math.abs(lastX - currentX);
            float yDifference = Math.abs(lastY - currentY);
            float zDifference = Math.abs(lastZ - currentZ);

            float shakeThreshold = 5f;
            if((xDifference > shakeThreshold && yDifference > shakeThreshold)
                    || (xDifference > shakeThreshold && zDifference > shakeThreshold)
                    || (yDifference > shakeThreshold && zDifference > shakeThreshold)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //when phone is shaken it will do this
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    Intent intent = new Intent(this, GameActivity.class);
                    startActivity(intent);
                } else {
                    vibrator.vibrate(500);
                    //depreciated in API 26
                }
            }
        }

        lastX = currentX;
        lastY = currentY;
        lastZ = currentZ;
        isNotFirstTime = true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAccAvail){
            sensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isAccAvail){
            sensorManager.unregisterListener(this);
        }
    }

    public void startGame(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
