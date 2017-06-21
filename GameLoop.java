package com.a2xia.shake2048;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;


public class GameLoop extends TimerTask implements HandGestureCOntrollerModuleInterface{

    public enum eDir{LEFT, RIGHT, UP, DOWN, NM}; //NM (No Movement)

    private RelativeLayout gameloopRL;
    private Context gameloopCTX;
    private Activity thisActivity;

    public static final int LEFT_BOUNDARY = 22;
    public static final int UP_BOUNDARY = 22;
    public static final int SLOT_ISOLATION = 233;

    public static final int RIGHT_BOUNDARY = LEFT_BOUNDARY + 3*SLOT_ISOLATION;
    public static final int DOWN_BOUNDARY = UP_BOUNDARY + 3*SLOT_ISOLATION;

    public static final int NUMBER_OF_SLOTS = 16;
    public static final int WINNING_NUMBER = 256;

    private LinkedList<GameBlock> myGBList;

    private Random myRandomGen;
    private boolean generateBlock;

    private boolean endGameFlag = false;

    private void createBlock(){

        boolean[][] boardOccupence = {{false, false, false, false},
                                      {false, false, false, false},
                                      {false, false, false, false},
                                      {false, false, false, false}};
        int[] currentGBCoord;
        int[] currentGBindex = {0,0};
        int[] newGBCoord = {0,0};
        int numberOfEmptySlots = NUMBER_OF_SLOTS;
        int randomSlotNum = 0;

        myRandomGen = new Random();

        for(GameBlock gb: myGBList){
            currentGBCoord = gb.getTargetCoordinate();
            currentGBindex[0] = (currentGBCoord[0] - LEFT_BOUNDARY) / SLOT_ISOLATION;
            currentGBindex[1] = (currentGBCoord[1] - UP_BOUNDARY) / SLOT_ISOLATION;
            boardOccupence[currentGBindex[1]][currentGBindex[0]] = true;
            numberOfEmptySlots--;
        }

        Log.d("Board Row 1: ", boardOccupence[0][0] + ", " + boardOccupence[0][1] + ", " + boardOccupence[0][2] + ", " + boardOccupence[0][3]);
        Log.d("Board Row 1: ", boardOccupence[1][0] + ", " + boardOccupence[1][1] + ", " + boardOccupence[1][2] + ", " + boardOccupence[1][3]);
        Log.d("Board Row 1: ", boardOccupence[2][0] + ", " + boardOccupence[2][1] + ", " + boardOccupence[2][2] + ", " + boardOccupence[2][3]);
        Log.d("Board Row 1: ", boardOccupence[3][0] + ", " + boardOccupence[3][1] + ", " + boardOccupence[3][2] + ", " + boardOccupence[3][3]);
        Log.d("Slot Count: ", " " + numberOfEmptySlots);

        if(numberOfEmptySlots == 0){
            Log.wtf("HEY YOU, LOOK HERE!!", "YOU'VE FAILED THE GAME!!!");
            endGameFlag = true;
            return;
        }

        randomSlotNum = myRandomGen.nextInt(numberOfEmptySlots);

        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 4; j++){
                if(!boardOccupence[i][j]){
                    if(randomSlotNum == 0){
                        newGBCoord[0] = LEFT_BOUNDARY + j * SLOT_ISOLATION;
                        newGBCoord[1] = UP_BOUNDARY + i * SLOT_ISOLATION;
                    }
                    randomSlotNum--;
                }
            }
        }

        //Create a Game Block on the board
        //randomSlotNum = myRandomGen.nextInt(2);
        GameBlock newBlock;

        //if(randomSlotNum == 0) {
            newBlock = new GameBlock(gameloopCTX, gameloopRL, newGBCoord[0], newGBCoord[1], this);
            myGBList.add(newBlock);
        //}
        //else {
        //    newBlock = new SuperMergerBlock(gameloopCTX, gameloopRL, newGBCoord[0], newGBCoord[1], this);
        //    myGBList.add(newBlock);
        //}


    }


    public GameLoop(Activity myActivity, RelativeLayout rl, Context ctx){
        thisActivity = myActivity;
        gameloopCTX = ctx;
        gameloopRL = rl;

        myGBList = new LinkedList<GameBlock>();

        createBlock();
    }

    //This method is used by the Accelerometer Handler to change the game direction
    public void setDirection(eDir targetDir){

        boolean noPendingMovement = true;

        //Stop responding to more directional commands if the game has reached the end;
        if(endGameFlag) return; //FILTER

        for(GameBlock gb : myGBList)
            if(gb.setDestination(targetDir))
                noPendingMovement = false; //VALVE

        generateBlock = !noPendingMovement;
        //createBlock();

    }

    public GameBlock isOccupied(int coordX, int coordY){

        int[] checkCoord;

        for(GameBlock gb : myGBList){
            checkCoord = gb.getCurrentCoordinate();
            if(checkCoord[0] == coordX && checkCoord[1] == coordY){
                Log.d("Game Loop Report: ", "Occupant Found @ " + coordX + "," + coordY);
                return gb;
            }
        }

        return null;

    }

    public void run(){

        thisActivity.runOnUiThread(
            new Runnable(){
                public void run() {
                    boolean noMotion = true;
                    LinkedList<GameBlock> removalList = new LinkedList<GameBlock>();

                    for(GameBlock gb : myGBList) {
                        gb.move();

                        if(gb.getBlockNumber() == WINNING_NUMBER){
                            endGameFlag = true;
                            Log.wtf("You Win!");
                        }

                        if(gb.isToBeDestroyed()){
                            removalList.add(gb);
                        }

                        if(gb.getDirection() != eDir.NM) {
                            noMotion = false;
                        }
                    }

                    if(noMotion){

                        if(generateBlock) {
                            createBlock();
                            generateBlock = false;
                        }

                        for(GameBlock gb : removalList){
                            gb.destroyMe();
                            myGBList.remove(gb);
                        }
                    }
                }
            }
        );
    }

}
