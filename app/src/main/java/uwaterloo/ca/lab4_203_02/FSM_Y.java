package uwaterloo.ca.lab4_203_02;


public class FSM_Y {

    //our FSM states
    enum FSMStates{WAIT, RISE, FALL, STABLE, DETERMINED};
    private FSMStates state;

    //the gestures that we can obtain
    enum Gestures{DOWN, UP, UNDETERMINED};
    private Gestures sig;

    //our first variable in threshold is the minimum slope for the response to trigger
    // our second variable is the max amplitude for response of the first peak
    //our third bariable is the max amplitude once we get a certain amount of samples (our default is 23)
    private final float[] THRESHOLD_UP = {0.5f, 0.8f, 0.7f};
    private final float[] THRESHOLD_DOWN = {-0.5f, -0.8f, 0.5f};

    //the reading should settle down after a certain amount of samples and thus we can see if it is
    //rising/falling and such after the occurrence of the maximum of the 1st response peak.
    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 23;

    //Keep the most recent historical reading so we can calculate the most recent slope
    private float previousReading;


    //our Constructor.
    // FSM_X is default in wait state
    public FSM_Y(){
        state = FSMStates.WAIT;
        sig = Gestures.UNDETERMINED;//default gesture is undetermined
        sampleCounter = SAMPLE_COUNTER_DEFAULT; //counter is set to our default of 33
        previousReading = 0;
        //TV = displayTV;
    }

    public FSMStates getState(){
        return state;
    } //when we call getState it returns state of FSM

    public Gestures getGesture(){
        return sig;
    } //when we call getGesture it returns the gesture

    //resets our FSM to the default state
    public void resetFSM(){
        state = FSMStates.WAIT;
        sig = Gestures.UNDETERMINED;
        sampleCounter = SAMPLE_COUNTER_DEFAULT;
        previousReading = 0;

    }

    //main FSM_X State Logic
    public void activateFSM(float accInput){

        //First, calculate the slope between the most recent input and the
        //most recent historical readings
        float accSlope = accInput - previousReading;

        //checks the state of FSM
        switch(state){

            case WAIT:

                if(accSlope >= THRESHOLD_UP[0]){ //UP
                    state = FSMStates.RISE;
                } else if(accSlope <= THRESHOLD_DOWN[0]) { //DOWN
                    state = FSMStates.FALL;
                }


                break;

            case RISE: //case for rising


                if(accSlope <= 0){ //UP Gesture Characteristic

                    if(previousReading >= THRESHOLD_UP[1]){
                        state = FSMStates.STABLE;
                    }
                    else{
                        state = FSMStates.DETERMINED;
                        sig = Gestures.UNDETERMINED;
                    }

                }

                break;

            case FALL: //our case for falling


                if(accSlope >= 0){ //DOWN Gesture Characteristic

                    if(previousReading <= THRESHOLD_DOWN[1]){
                        state = FSMStates.STABLE;
                    }
                    else{
                        state = FSMStates.DETERMINED;
                        sig = Gestures.UNDETERMINED;
                    }

                }

                break;


            case STABLE:
                //System.out.println("stabilizing...");
                //This part is to wait for the stabilization.

                sampleCounter--;

                //Once reached zero, check the threshold and determine the gesture.
                if(sampleCounter == 0){

                    state = FSMStates.DETERMINED;

                    if(accSlope > 0) {
                        if((Math.abs(accInput) < THRESHOLD_UP[2]) ){ //if the value of the reading is less than the third threshold value
                            sig = Gestures.UP;

                        }
                        else{
                            sig = Gestures.UNDETERMINED; //otherwise state is determined and gesture is undetermined

                        }
                    } else {//if accslope is < 0 basically --> for our DOWN case
                        if((Math.abs(accInput) < THRESHOLD_UP[2]) ){//if the value of the reading is less than the third threshold value
                            sig = Gestures.DOWN;

                        }
                        else{//otherwise the gesture cannot be determined
                            sig = Gestures.UNDETERMINED; //otherwise state is determined and gesture is undetermined

                        }
                    }

                }

                break;

            case DETERMINED:

                //Once determined, report the gesture and reset the FSM_Y.
                System.out.println("FSM_Y: " + String.format("Gesture is %s", sig.toString()));

                break;

            default:
                resetFSM();
                break;

        }

        previousReading = accInput;

    }

}
