package uwaterloo.ca.lab4_203_02;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RelativeLayout;
import android.widget.TextView;





public class AccelSensorEventListener implements SensorEventListener {

    private TextView tv; //output values

    private RelativeLayout l1;
    private float[][] readings = new float[100][3]; //the readings that will be stored in this array to be outputed to file
    private uwaterloo.ca.lab4_203_02.FSM_X FSM_X; //Declaring FSM_X that will be used for x axis gestures
    private uwaterloo.ca.lab4_203_02.FSM_Y FSM_Y; //Declaring FSM_Y that will be used for y axis gestures
    private GameLoopTask myTask;

    private final float C = 25.0f;

    public AccelSensorEventListener(GameLoopTask task, RelativeLayout layout, TextView tv1) { //public so we can put in the values of OutputView and MaxReading which are in the main class
        myTask = task;
        l1 = layout;
        tv = tv1;
        FSM_X = new FSM_X();
        FSM_Y = new FSM_Y();

    }

    public float[][] getHistoryReadings(){
        return readings;
    } //once called, this will return our readings

    public void onAccuracyChanged(Sensor s, int i) {
    }

    private void insertHistory(float[] values) {

        //Add filtered readings to the array
        for(int i = 1; i < 100; i++){
            readings[i - 1][0] = readings[i][0];
            readings[i - 1][1] = readings[i][1];
            readings[i - 1][2] = readings[i][2];
        }

        //Applying Low Pass Filter to Accelerometer Readings
        for(int i = 0; i < 3; i++) {
            readings[99][i] += (values[i] - readings[99][i]) / C;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onSensorChanged(SensorEvent se) {

        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) { //If its the accelerometer instead


            insertHistory(se.values); //inserts our se values to the readings

            FSM_X.activateFSM(readings[99][0]); //since readings[99][0] are our x axis variables, we activate FSM_X using those readings
            FSM_Y.activateFSM(readings[99][1]); //same thing as above but this for our y axis so we use readings[99][1]

            if (FSM_X.getState() == uwaterloo.ca.lab4_203_02.FSM_X.FSMStates.DETERMINED
                    && FSM_Y.getState() == uwaterloo.ca.lab4_203_02.FSM_Y.FSMStates.DETERMINED) { //if both FSM's have the state DETERMINED
                tv.setText("UNKNOWN");
                myTask.setDirection(GameLoopTask.gameDirections.NO_MOVEMENT); //set direction from task to no movement if unkown
                FSM_X.resetFSM(); //Reset the FSM's
                FSM_Y.resetFSM();
            } else if (FSM_X.getState() == uwaterloo.ca.lab4_203_02.FSM_X.FSMStates.DETERMINED
                    || FSM_Y.getState() == uwaterloo.ca.lab4_203_02.FSM_Y.FSMStates.DETERMINED) { //if one FSM  has the state DETERMINED

                //System.out.println("both determined");
                if (FSM_X.getGesture() == uwaterloo.ca.lab4_203_02.FSM_X.Gestures.UNDETERMINED
                        && FSM_Y.getGesture() != uwaterloo.ca.lab4_203_02.FSM_Y.Gestures.UNDETERMINED) { //if the FSM for Y axis is DETERMINED
                    //System.out.println("Y determined");
                    tv.setText(FSM_Y.getGesture().toString());
                    determineDir(FSM_Y.getGesture().toString());//gets gesture from Y and changes it to string. This is now the direction block is going to move. Function is below
                } else if (FSM_Y.getGesture() == uwaterloo.ca.lab4_203_02.FSM_Y.Gestures.UNDETERMINED
                        && FSM_X.getGesture() != uwaterloo.ca.lab4_203_02.FSM_X.Gestures.UNDETERMINED) { //if the FSM for X axis is DETERMINED
                    //System.out.println("X determined");
                    tv.setText(FSM_X.getGesture().toString()); //this is just setting textview we had before to indicate our gesture
                    determineDir(FSM_X.getGesture().toString()); //same thing as before, but now we passing this to determine dir
                } else {
                    //System.out.println("not determined");
                    tv.setText("UNKNOWN");
                    myTask.setDirection(GameLoopTask.gameDirections.NO_MOVEMENT); //no movement if we cant determine
                }

                FSM_X.resetFSM(); //Resets FSM's
                FSM_Y.resetFSM();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void determineDir(String gesture) { //just a function to make determining direction easier
            switch (gesture) { //switch case for gesture
                case "UP": //if up is our gesture, the direction passed through game loop task and the one that affects the block will move it up
                    myTask.setDirection(GameLoopTask.gameDirections.UP);
                    break;
                case "DOWN": //same things as before just for down
                    myTask.setDirection(GameLoopTask.gameDirections.DOWN);
                    break;
                case "LEFT": //for left gestures 
                    myTask.setDirection(GameLoopTask.gameDirections.LEFT);
                    break;
                case "RIGHT"://for right gestures
                    myTask.setDirection(GameLoopTask.gameDirections.RIGHT);
                    break;
                default: //our default direction will be no movement if it cannot be determined so the block remains static
                    myTask.setDirection(GameLoopTask.gameDirections.NO_MOVEMENT);
            }


        }

}
