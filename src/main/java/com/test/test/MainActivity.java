package com.test.test;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.*;




public class MainActivity extends ActionBarActivity implements SensorEventListener{


    private TextView tv;
    //the Sensor Manager
    private SensorManager sManager;
    private double pass;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //get the TextView from the layout file
        tv = (TextView) findViewById(R.id.gyro);

        //get a hook to the sensor service
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    }


    public double min(ArrayList<Double> nums){
        double min = nums.get(0);
        for(int i = 1; i < nums.size(); i++){
            if(nums.get(i) < min){
                min = nums.get(i);
            }
        }
        return min;
    }

    public double max(ArrayList<Double> nums){
        double max = nums.get(0);
        for(int i = 1; i < nums.size(); i++){
            if(nums.get(i) > max){
                max = nums.get(i);
            }
        }
        return max;
    }

    public void buttonOnClick(View v){
        Button button = (Button) v;
        ((Button) v).setText("Click Again");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final Handler handler=new Handler();
                handler.post(new Runnable(){
                ArrayList<Double> degs = new ArrayList<Double>();
                    int timer = 0;
                    @Override
                    public void run() {
                        timer++;
                        double freq = 0; double amp = 0; double cyc = 0;
                        TextView tV2 = (TextView) findViewById (R.id.tV2);
                        if(degs.size() <= 1000){
                            degs.add(pass);
                        } else if (degs.size() > 1000) {
                            degs.remove(0);
                            degs.add(pass);
                            double max = max(degs); double min = min(degs);
                            for(int i = 1; i < degs.size() - 1; i += 10){
                                if(((int) degs.get(i - 1).doubleValue() < (int) degs.get(i).doubleValue() && degs.get(i + 1) < (int) degs.get(i).doubleValue()) || ( (int) degs.get(i - 1).doubleValue() > (int) degs.get(i).doubleValue() && (int) degs.get(i + 1).doubleValue() > (int) degs.get(i).doubleValue())){
                                    cyc += 0.5;
                                }
                            }
                            freq = (int) cyc;
                            if(min < 0){
                                min *= -1;
                            }
                            max *= 3.14/180; min *= 3.14/180;
                            double tAmp = (8.89 * (max + min)) / 2;
                            if(tAmp > amp){
                                amp = tAmp;
                            }
                            if(timer % 250 == 0){
                                tV2.setText("Frequency: " + String.valueOf(freq) + " " + "Amplitude: " + String.valueOf(amp));
                            }
                        }
                        handler.postDelayed(this,1); // set time here to refresh textView
                    }
                });
            }
        });
    }

    //when this Activity starts
    @Override
    protected void onResume()
    {
        super.onResume();
        /*register the sensor listener to listen to the gyroscope sensor, use the
        callbacks defined in this class, and gather the sensor information as quick
        as possible*/
        sManager.registerListener(this, sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_FASTEST);
    }

    //When this Activity isn't visible anymore
    @Override
    protected void onStop()
    {
        //unregister the sensor listener
        sManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1)
    {
        //Do nothing.
    }

    @Override
    public void onSensorChanged(SensorEvent event)
    {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        //else it will output the Roll, Pitch and Yawn values
        tv.setText("Orientation X (Roll) :"+ Float.toString(event.values[2]) +"\n"+
                "Orientation Y (Pitch) :"+ Float.toString(event.values[1]) +"\n"+
                "Orientation Z (Yaw) :"+ Float.toString(event.values[0]));
        pass = (double) event.values[2];
    }
}
