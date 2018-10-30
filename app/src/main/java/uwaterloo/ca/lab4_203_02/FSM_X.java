package uwaterloo.ca.lab4_203_02;

public class FSM_X {

    //our FSM states
    enum FSMStates{WAIT, RISE, FALL, STABLE, DETERMINED};
    private FSMStates state; //the state of our FSM

    //the gestures that we can obtain
    enum Gestures{LEFT, RIGHT, UNDETERMINED};
    private Gestures sig; //gesture variable

    //our first variable in threshold is the minimum slope for the response to trigger
    // our second variable is the max amplitude for response of the first peak
    //our third bariable is the max amplitude once we get a certain amount of samples (our default is 33)
    private final float[] THRESHOLD_RIGHT = {0.55f, 0.8f, 0.5f};
    private final float[] THRESHOLD_LEFT = {-0.55f, -0.8f, 0.3f};

    //Keep the most recent historical reading so we can calculate the most recent slope
    private float previousReading;

    //the reading should settle down after a certain amount of samples and thus we can see if it is
    //rising/falling and such after the occurrence of the maximum of the 1st response peak.
    private int sampleCounter;
    private final int SAMPLE_COUNTER_DEFAULT = 33;


    //our Constructor.
    // FSM_X is default in wait state
    public FSM_X(){
        state = FSMStates.WAIT;
        sig = Gestures.UNDETERMINED;  //default gesture is undetermined
        sampleCounter = SAMPLE_COUNTER_DEFAULT; //counter is set to our default of 33
        previousReading = 0;

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

            case WAIT://our wait case, checks if rising or falling when in wait to change to rise or fall

                if(accSlope >= THRESHOLD_RIGHT[0]){ //RIGHT
                    state = FSMStates.RISE;
                } else if(accSlope <= THRESHOLD_LEFT[0]) { //LEFT
                    state = FSMStates.FALL;
                }

                break;

            case FALL: //our case for falling

                if(accSlope >= 0){

                    if(previousReading <= THRESHOLD_LEFT[1]){
                        state = FSMStates.STABLE;
                    }
                    else{
                        state = FSMStates.DETERMINED;
                        sig = Gestures.UNDETERMINED;
                    }

                }

                break;

            case RISE: //if our case is rising

                if(accSlope <= 0){ //RIGHT GESTURE CHARACTERISTIC

                    if(previousReading >= THRESHOLD_RIGHT[1]){ //if the previous reading is greater or less than our max amp check then it is stable
                        state = FSMStates.STABLE;
                    }
                    else{
                        state = FSMStates.DETERMINED;
                        sig = Gestures.UNDETERMINED; //otherwise state is determined and gesture is undetermined
                    }

                }

                break;

            case STABLE:

                sampleCounter--; //count down to skip the stablizing readings
                //continously decreases counter for every read

                //Once reached zero, check the threshold and determine the gesture.
                if(sampleCounter == 0){

                    state = FSMStates.DETERMINED;

                    if(accSlope > 0) { //if accslope is greater than 0
                        if((Math.abs(accInput) < THRESHOLD_RIGHT[2]) ){ //if the value of the reading is less than the third threshold value
                            sig = Gestures.RIGHT;

                        }
                        else{
                            sig = Gestures.UNDETERMINED; //otherwise gesture cannot be determined

                        }
                    } else { //if accslope is < 0 basically --> for our left case
                        if((Math.abs(accInput) < THRESHOLD_RIGHT[2]) ){ //if the value of the reading is less than the third threshold value
                            sig = Gestures.LEFT; //declares as left gesture if condition applies

                        }
                        else{ //otherwise the gesture cannot be determined
                            sig = Gestures.UNDETERMINED;

                        }
                    }

                }

                break;

            case DETERMINED:

                //if case is determined for our state, then it will ouput our gesture
                System.out.println("FSM_X " + String.format("gesture is %s", sig.toString()));
                break;

            default:
                resetFSM();
                break;

        }

        //puts our current accinput to our previous reading everytime
        previousReading = accInput;

    }

}
