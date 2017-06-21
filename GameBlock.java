package com.a2xia.shake2048;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Random;


public class GameBlock extends GameBlockTemplate {

    private final float GB_ACC = 4.0f;      // Game block acceleration factor
    private final float IMAGE_SCALE = 1.0f;
    private final int TV_OFFSET = 25;
    private int myCoordX;
    private int myCoordY;
    private int targetCoordX;
    private int targetCoordY;

    private GameLoop myGL;
    private RelativeLayout myRL;
    protected TextView myTV;
    protected int blockNumber;

    private int myVelocity;
    private GameLoop.eDir targetDirection;

    protected boolean toBeDestroyed = false;
    private GameBlock targetMergeBlock = null;

    protected int calculateMerges(int[] numAhead, int numOfOccupants){
        int numOfMerges = 0;

        switch(numOfOccupants){
            case 0:
                return 0;

            case 1:
                if(numAhead[0] == numAhead[1]) {
                    numOfMerges = 1;
                    toBeDestroyed = true;
                }
                break;

            case 2:
                if(numAhead[0] == numAhead[1]) {
                    numOfMerges = 1;
                }
                else if(numAhead[1] == numAhead[2]){
                    numOfMerges = 1;
                    toBeDestroyed = true;
                }
                break;

            case 3:
                if(numAhead[0] == numAhead[1]){
                    if(numAhead[2] == numAhead[3]) {
                        numOfMerges = 2;
                        toBeDestroyed = true;
                    }
                    else {
                        numOfMerges = 1;
                    }
                }
                else if(numAhead[1] == numAhead[2]){
                    numOfMerges = 1;
                }
                else if(numAhead[2] == numAhead[3]){
                    numOfMerges = 1;
                    toBeDestroyed = true;
                }

                break;
            default:
                return 0;
        }

        return numOfMerges;
    }

    public GameBlock(Context gbCTX, RelativeLayout gbRL, int coordX, int coordY, GameLoop gbGL){

        super(gbCTX);
        this.setImageResource(R.drawable.gameblock);
        this.setX(coordX);
        this.setY(coordY);
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        gbRL.addView(this);
        this.bringToFront();

        this.myGL = gbGL;
        this.myRL = gbRL;

        Random myRandomGen = new Random();
        blockNumber = (myRandomGen.nextInt(2) + 1) * 2;

        myCoordX = coordX;
        myCoordY = coordY;
        targetCoordX = myCoordX;
        targetCoordY = myCoordY;
        myVelocity = 0;

        myTV = new TextView(gbCTX);
        myTV.setX(coordX + TV_OFFSET);
        myTV.setY(coordY + TV_OFFSET);
        myTV.setText(String.format("%d", blockNumber));
        myTV.setTextSize(40.0f);
        myTV.setTextColor(Color.BLACK);

        myRL.addView(myTV);
        myTV.bringToFront();

        targetDirection = GameLoop.eDir.NM;

        Log.d("GameBLock: ", "My Position is: " + myCoordX + "," + myCoordY);

    }



    public GameLoop.eDir getDirection(){
        return targetDirection;
    }

    public int[] getCurrentCoordinate(){
        int[] thisCoord = new int[2];
        thisCoord[0] = myCoordX;
        thisCoord[1] = myCoordY;
        return thisCoord;
    }

    /* Allow blocks to report their own location */
    public int[] getTargetCoordinate(){
        int[] thisCoord = new int[2];
        thisCoord[0] = targetCoordX;
        thisCoord[1] = targetCoordY;
        return thisCoord;
    }

    public int getBlockNumber(){
        return blockNumber;
    }

    public boolean isToBeDestroyed(){
        return toBeDestroyed;
    }

    /* Double number of merged blocks */
    public void doubleMyNumber(){
        blockNumber *= 2;
        myTV.setText(String.format("%d", blockNumber));
    }

    /* Destroy one of the merged blocks */
    public void destroyMe(){
        myRL.removeView(myTV);
        myRL.removeView(this);
        targetMergeBlock.doubleMyNumber();
    }

    @Override
    public boolean setDestination(GameLoop.eDir thisDir){

        targetDirection = thisDir;

        int testCoord;
        int numOfOccupants = 0;
        int numOfSlotsAhead = 0;
        int numOfMerges = 0;
        int[] occupantNumbers = {0,0,0,0};
        GameBlock testBlock;

        switch(thisDir){
            case LEFT:

                testCoord = GameLoop.LEFT_BOUNDARY;

                while(testCoord != myCoordX){

                    testBlock = myGL.isOccupied(testCoord, myCoordY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord += GameLoop.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordX = GameLoop.LEFT_BOUNDARY
                               + numOfOccupants * GameLoop.SLOT_ISOLATION
                               - numOfMerges * GameLoop.SLOT_ISOLATION;


                break;

            case RIGHT:

                testCoord = GameLoop.RIGHT_BOUNDARY;

                while(testCoord != myCoordX){

                    testBlock = myGL.isOccupied(testCoord, myCoordY);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord -= GameLoop.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordX = GameLoop.RIGHT_BOUNDARY
                               - numOfOccupants * GameLoop.SLOT_ISOLATION
                               + numOfMerges * GameLoop.SLOT_ISOLATION;

                break;

            case UP:

                testCoord = GameLoop.UP_BOUNDARY;

                while(testCoord != myCoordY){

                    testBlock = myGL.isOccupied(myCoordX, testCoord);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord += GameLoop.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordY = GameLoop.UP_BOUNDARY
                               + numOfOccupants * GameLoop.SLOT_ISOLATION
                               - numOfMerges * GameLoop.SLOT_ISOLATION;

                break;

            case DOWN:

                testCoord = GameLoop.DOWN_BOUNDARY;

                while(testCoord != myCoordY){

                    testBlock = myGL.isOccupied(myCoordX, testCoord);

                    if(testBlock != null){
                        targetMergeBlock = testBlock;
                        occupantNumbers[numOfOccupants] = testBlock.getBlockNumber();
                        numOfOccupants++;
                    }

                    testCoord -= GameLoop.SLOT_ISOLATION;
                }

                occupantNumbers[numOfOccupants] = blockNumber;
                numOfMerges = calculateMerges(occupantNumbers, numOfOccupants);

                targetCoordY = GameLoop.DOWN_BOUNDARY
                               - numOfOccupants * GameLoop.SLOT_ISOLATION
                               + numOfMerges * GameLoop.SLOT_ISOLATION;

                break;
            default:
                break;
        }

        Log.d("Block " + blockNumber + " Report",
                occupantNumbers[0] + ", " +
                        occupantNumbers[1] + ", " +
                        occupantNumbers[2] + ", " +
                        occupantNumbers[3] + ", " +
                        numOfOccupants + ", " +
                        numOfMerges + ", " +
                        targetCoordX + ", " +
                        targetCoordY + ", " +
                        toBeDestroyed);

        
        if(targetCoordX == myCoordX && targetCoordY == myCoordY)
            return false;  //No need to set destination
        else
            return true;   //set destination completed

    }

    @Override
    public void move(){

        switch(targetDirection){

            case LEFT:

                if(myCoordX > targetCoordX){
                    if((myCoordX - myVelocity) <= targetCoordX){
                        myCoordX = targetCoordX;
                        myVelocity = 0;
                    }
                    else {
                        myCoordX -= myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            case RIGHT:

                if(myCoordX < targetCoordX){
                    if((myCoordX + myVelocity) >= targetCoordX){
                        myCoordX = targetCoordX;
                        myVelocity = 0;
                    }
                    else {
                        myCoordX += myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            case UP:

                if(myCoordY > targetCoordY){
                    if((myCoordY - myVelocity) <= targetCoordY){
                        myCoordY = targetCoordY;
                        myVelocity = 0;
                    }
                    else {
                        myCoordY -= myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            case DOWN:

                if(myCoordY < targetCoordY){
                    if((myCoordY + myVelocity) >= targetCoordY){
                        myCoordY = targetCoordY;
                        myVelocity = 0;
                    }
                    else {
                        myCoordY += myVelocity;
                        myVelocity += GB_ACC;
                    }
                }

                break;

            default:
                break;

        }

        this.setX(myCoordX);
        this.setY(myCoordY);

        myTV.setX(myCoordX + TV_OFFSET);
        myTV.setY(myCoordY + TV_OFFSET);
        myTV.bringToFront();

        if(myVelocity == 0) {
            targetDirection = GameLoop.eDir.NM;
        }

    }


}
