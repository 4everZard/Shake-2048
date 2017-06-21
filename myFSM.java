package com.a2xia.shake2048;

import android.util.Log;

public class myFSM {

    //FSM Constants
    enum FSMState{WAIT, A_RISE, A_FALL, B_FALL, B_RISE, DETERMINED};
    //FSM Initial State
    private FSMState myState;

    //Signature Constants
    enum mySig{SIG_A, SIG_B, SIG_X};
    //Default Signature
    private mySig Signature;

    //Signature A Constants
    private final float[] THRESHOLD_A = {0.6f, 2.0f, -0.4f};
    //Signature B Constants
    private final float[] THRESHOLD_B = {-0.6f, -2.0f, 0.4f};

    private float previousInput;

    private void mainFSM(float currentInput){

        float deltaInput = currentInput - previousInput;

        switch(myState){

            case WAIT:

                if(deltaInput >= THRESHOLD_A[0]) {
                    myState = FSMState.A_RISE;
                }
                else if(deltaInput <= THRESHOLD_B[0]){
                    myState = FSMState.B_FALL;
                }

                break;

            case A_RISE:

                if(deltaInput <= 0){
                    if(currentInput >= THRESHOLD_A[1])
                        myState = FSMState.A_FALL;
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case A_FALL:

                if(deltaInput >= 0){
                    if(currentInput <= THRESHOLD_A[2]){
                        Signature = mySig.SIG_A;
                        myState = FSMState.DETERMINED;
                    }
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case B_FALL:

                if(deltaInput >= 0){
                    if(currentInput <= THRESHOLD_B[1])
                        myState = FSMState.B_RISE;
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case B_RISE:

                if(deltaInput <= 0){
                    if(currentInput >= THRESHOLD_B[2]){
                        Signature = mySig.SIG_B;
                        myState = FSMState.DETERMINED;
                    }
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case DETERMINED:

                break;

            default:
                resetFSM();
                break;

        }

    }



    public myFSM(){
        myState = FSMState.WAIT;
        Signature = mySig.SIG_X;
        previousInput = 0.0f;
    }

    public void resetFSM(){
        myState = FSMState.WAIT;
        Signature = mySig.SIG_X;
        previousInput = 0.0f;
    }

    public void supplyInput(float input){
        //Log.d("Debug FSM", "Input: " + input);

        mainFSM(input);

        previousInput = input;
    }

    public mySig getSignature(){
        if(myState == FSMState.DETERMINED){
            return Signature;
        }
        else{
            return mySig.SIG_X;
        }
    }

    public boolean isReady(){
        if(myState == FSMState.WAIT)
            return true;
        else
            return false;
    }

}

