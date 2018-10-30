package uwaterloo.ca.lab4_203_02;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Timer;


public class Lab4_203_02 extends AppCompatActivity {
    RelativeLayout l1;
    TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8;
    public final int GAMEBOARD_DIMENSION = 800; //constant indiciating dimension of our game board

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab4_203_02);

        l1 = (RelativeLayout) findViewById(R.id.labels); //l1 is our relative layout. sets layout params as our gameboard dimension
        l1.getLayoutParams().width = GAMEBOARD_DIMENSION;
        l1.getLayoutParams().height = GAMEBOARD_DIMENSION;

        tv2 = createView(tv2, "Waiting..."); //this is our textbox for debugging
        l1.setBackgroundResource(R.drawable.gameboard); //sets background of our relative layout to be our gameboard


       //****SENSORS****
        try{
            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE); //this is the sensor manager


            //Accelerometer readings
            Sensor accel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION); //same comments for light sensor. this is just for accelerometer




        Timer timer1 = new Timer(); //creates a new timer
        GameLoopTask task1 = new GameLoopTask(this, getApplicationContext(), l1); //makes a task of gameloop task and assigns the timer we created before to schedule this task to last 50 ns, every 50 ns
        timer1.schedule(task1, 50, 50);


        tv2.setTextSize(50.0f); //stuff relating to the direction textview
        tv2.setTextColor(Color.BLACK);


        SensorEventListener l2 = new AccelSensorEventListener(task1, l1, tv2);
        sensorManager.registerListener(l2, accel, SensorManager.SENSOR_DELAY_GAME); //registering our accelerometer sensor to the snesor manager
        } catch (Exception error) {
            System.out.println("ERROR IN SENSORMANAGER");
        }
    }

    protected TextView createView(TextView tv, String text) { //function for creating different the different textviews
        tv = new TextView(getApplicationContext()); //declaring the textview.
        tv.setText(text);
        l1.addView(tv);//adds textview to the linear layout
        return tv;
    }



}




