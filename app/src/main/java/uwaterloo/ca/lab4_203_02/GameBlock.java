package uwaterloo.ca.lab4_203_02;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class GameBlock extends GameBlockTemplate { //our actual gameblock object that will move around the relative layout

    private final float IMAGE_SCALE = 0.5f; //image scale of object
    public int positionX, positionY; //blocks positions
    private int velocityX, velocityY; //blocks velocity
    private int accelX, accelY; //blocks accel, is constant
    private final int numOffset = 180;
    private int[] coords;
    private int[] slotCoord;
    private int destinationX, destinationY;
    public int blockNum;
    public boolean merged;
    public boolean beforeMerged;
    public boolean Removed;
    public GameBlock targetBlock;

    public boolean doneMoving = true;


    private RelativeLayout myRL;
    public TextView blockTV;

    private GameLoopTask.gameDirections myDir;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameBlock(Context c, int coordX, int coordY, RelativeLayout r1){ //constructor
        super(c); //supers context
        myRL = r1;
        positionY = coordY;
        positionX = coordX;
        destinationX = coordX;
        destinationY = coordY;

        this.setImageResource(R.drawable.gameblock); //draws game block and sets scale to what we initialized as our scale
        this.setScaleX(IMAGE_SCALE);
        this.setScaleY(IMAGE_SCALE);
        this.setX(coordX); //sets our x coordinate to our x coordinate that is passed through our constuctor
        this.setY(coordY); //same for y



        myRL.addView(this); //add the newly create block to the layout

        this.velocityX = 10; //velocity for constructor is starting 10
        this.velocityY = 10;
        this.accelX = 5; //accel is constant 5
        this.accelY = 5;

        blockNum = getRandomBlockNumber();
//        blockNum = 2;

//        try{
            blockTV = new TextView(c);
            blockTV.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            myRL.addView(blockTV);
            blockTV.setText(String.valueOf(blockNum));

            blockTV.setX(coordX + numOffset);
            blockTV.setY(coordY + numOffset);
            blockTV.setTextColor(Color.BLACK);
            blockTV.bringToFront();
//        } catch(Exception e) {
//            System.out.println(e);
//        }



    }

    public void setBlockDirection(GameLoopTask.gameDirections newDir) { //get block direction function. changes current direction to whatever our new direction is when passed through this 
        myDir = newDir;

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void setPositionX(int X){
        this.setX(X);
        blockTV.setX(X + numOffset);

    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void setPositionY(int Y){
        this.setY(Y);
        blockTV.setY(Y + numOffset);
    }

    private int getRandomBlockNumber() {
        Random rdm = new Random();
        if(rdm.nextInt(2) == 0) {
            return 2;
        } else {
            return 4;
        }
    }

    public void getCurrentSlot() {
        slotCoord = new int[3];

        switch(positionX){
            case -91:
                slotCoord[0] = 0;
                break;
            case 106:
                slotCoord[0] = 1;
                break;
            case 303:
                slotCoord[0] = 2;
                break;
            case 500:
                slotCoord[0] = 3;
                break;
            default:
                slotCoord[0] = -1;
                break;
        }

        switch(positionY){
            case -91:
                slotCoord[1] = 0;
                break;
            case 106:
                slotCoord[1] = 1;
                break;
            case 303:
                slotCoord[1] = 2;
                break;
            case 500:
                slotCoord[1] = 3;
                break;
            default:
                slotCoord[1] = -1;
                break;
        }
    }

    public int[] getSlotArray() {
        int[] slotArray = new int[3];

        switch(positionX){
            case -91:
                slotArray[0] = 0;
                break;
            case 106:
                slotArray[0] = 1;
                break;
            case 303:
                slotArray[0] = 2;
                break;
            case 500:
                slotArray[0] = 3;
                break;
            default:
                slotArray[0] = -1;
                break;
        }

        switch(positionY){
            case -91:
                slotArray[1] = 0;
                break;
            case 106:
                slotArray[1] = 1;
                break;
            case 303:
                slotArray[1] = 2;
                break;
            case 500:
                slotArray[1] = 3;
                break;
            default:
                slotArray[1] = -1;
                break;
        }

        return slotArray;
    }

    @Override
    public void setDestination() {
        int blockCount = 0;
        int slotCount = 0;
        int checkX = 0;
        int checkY = 0;
        int[] line = new int[4];
        merged = false;
        beforeMerged = false;

        getCurrentSlot();

        if(myDir == GameLoopTask.gameDirections.DOWN) {

            destinationX = positionX;
            destinationY = GameLoopTask.BOT_BOUNDARY;

            checkY = 3;
            while(checkY > slotCoord[1]) {
                if(GameLoopTask.isOccupied(positionX, GameLoopTask.TOP_BOUNDARY + checkY*GameLoopTask.SLOT_SEPARATION)) {
                    slotCount++;
                    line[blockCount]=GameLoopTask.getNumber(positionX, GameLoopTask.TOP_BOUNDARY + checkY*GameLoopTask.SLOT_SEPARATION);
                    targetBlock = GameLoopTask.getGameBlock(positionX, GameLoopTask.TOP_BOUNDARY + checkY*GameLoopTask.SLOT_SEPARATION);
                    blockCount++;

                } else {
                    slotCount++;
                }
                checkY--;
            }

            checkMerge(line, blockCount);


            if(merged) {
                Removed = true;
                destinationY = positionY + (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if (beforeMerged) {
                destinationY = positionY + (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if(blockCount != 0) {
                destinationY = positionY + (slotCount - blockCount) * GameLoopTask.SLOT_SEPARATION;
            }

        } else if(myDir == GameLoopTask.gameDirections.UP) {
            destinationX = positionX;
            destinationY = GameLoopTask.TOP_BOUNDARY;
            checkY = 0;
            while(checkY < slotCoord[1]) {
                if(GameLoopTask.isOccupied(positionX, GameLoopTask.TOP_BOUNDARY + checkY*GameLoopTask.SLOT_SEPARATION)) {
                    slotCount++;
                    line[blockCount]=GameLoopTask.getNumber(positionX, GameLoopTask.TOP_BOUNDARY + checkY*GameLoopTask.SLOT_SEPARATION);
                    targetBlock = GameLoopTask.getGameBlock(positionX, GameLoopTask.TOP_BOUNDARY + checkY*GameLoopTask.SLOT_SEPARATION);
                    blockCount++;

                }else{
                    slotCount++;
                }
                checkY++;
            }

            checkMerge(line, blockCount);

            if(merged) {
                Removed = true;
                destinationY = positionY - (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if (beforeMerged) {
                destinationY = positionY - (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if(blockCount != 0) {
                destinationY = positionY - (slotCount - blockCount) * GameLoopTask.SLOT_SEPARATION;
            }

        } else if(myDir == GameLoopTask.gameDirections.LEFT) {
            destinationX = GameLoopTask.LEFT_BOUNDARY;
            destinationY = positionY;
            checkX = 0;
            while(checkX < slotCoord[0]) {
                if(GameLoopTask.isOccupied(GameLoopTask.LEFT_BOUNDARY + checkX*GameLoopTask.SLOT_SEPARATION, positionY)) {
                    slotCount++;
                    line[blockCount]=GameLoopTask.getNumber(GameLoopTask.LEFT_BOUNDARY + checkX*GameLoopTask.SLOT_SEPARATION, positionY);
                    targetBlock = GameLoopTask.getGameBlock(GameLoopTask.LEFT_BOUNDARY + checkX*GameLoopTask.SLOT_SEPARATION, positionY);
                    blockCount++;

                }else{
                    slotCount++;
                }
                checkX++;
            }

            checkMerge(line, blockCount);

            if(merged) {
                Removed = true;
                destinationX = positionX - (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if (beforeMerged) {
                destinationX = positionX - (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if(blockCount != 0) {
                destinationX = positionX - (slotCount - blockCount) * GameLoopTask.SLOT_SEPARATION;
            }

        } else if(myDir == GameLoopTask.gameDirections.RIGHT) {
            destinationX = GameLoopTask.RIGHT_BOUNDARY;
            destinationY = positionY;
            checkX = 3;
            while(checkX > slotCoord[0]) {
                if(GameLoopTask.isOccupied(GameLoopTask.LEFT_BOUNDARY + checkX*GameLoopTask.SLOT_SEPARATION, positionY)) {
                    slotCount++;
                    line[blockCount]=GameLoopTask.getNumber(GameLoopTask.LEFT_BOUNDARY + checkX*GameLoopTask.SLOT_SEPARATION, positionY);
                    targetBlock = GameLoopTask.getGameBlock(GameLoopTask.LEFT_BOUNDARY + checkX*GameLoopTask.SLOT_SEPARATION, positionY);
                    blockCount++;

                }else{
                    slotCount++;
                }
                checkX--;
            }

            checkMerge(line, blockCount);

            if(merged) {
                Removed = true;
                destinationX = positionX + (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if (beforeMerged) {
                destinationX = positionX + (slotCount - blockCount + 1) * GameLoopTask.SLOT_SEPARATION;
            } else if(blockCount != 0) {
                destinationX = positionX + (slotCount - blockCount) * GameLoopTask.SLOT_SEPARATION;
            }
        }
    }

    public int[] getCoords() {
        coords = new int[2];
        coords[0] = positionX;
        coords[1] = positionY;

        return coords;
    }

    private void checkMerge(int[] line,  int blocks) {
        if(blocks == 1) {
            for(int i = 0; i < line.length; i++) {
                if(line[i] != 0 && line[i] == blockNum) {
                    merged = true;
                }
            }
        } else if (blocks == 2) {
            if (line[1] == blockNum && line[0] != line[1]) {
                merged = true;
            } else if(line[0] == line[1] && line[1] != blockNum) {
                beforeMerged = true;
            } else if(line[0] == line[1] && line[1] == blockNum) {
                beforeMerged = true;
            }

        } else if (blocks == 3) {
            if (line[2] == blockNum && line[2] != line[1] && line[1] != line[0]) {
                merged = true;
            } else if(line[2] == blockNum && line[2] != line[1] && line[1] == line[0]) {
                merged = true;
            } else if(line[2] != blockNum && line[1] == line[0]) {
                beforeMerged = true;
            } else if(line[2] == blockNum && line[2] == line[1] && line[1] == line[0]) {
                merged = true;
            } else if(line[2] != blockNum && line[2] == line[1] && line[1] != line[0]) {
                beforeMerged = true;
            }
        }
    }

    public void destroyMe() {
        myRL.removeView(this);
        myRL.removeView(blockTV);
        GameLoopTask.updateNum(targetBlock);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void move(){ //our actual move function

        if(doneMoving) {
            velocityY = 10;
            velocityX = 10;
        }

        if(myDir == GameLoopTask.gameDirections.NO_MOVEMENT) {
            doneMoving = true;
        }else if(myDir == GameLoopTask.gameDirections.UP) { //if direction is up
            if((positionY - velocityY) < destinationY) { //this if statement states that if we go beyond our boundaries due to the velocity, it will just set our position to the edge of the boundary thus it will not clip through the game board
                positionY = destinationY;
                setPositionY(destinationY);
                doneMoving = true;

            }else if(positionY > destinationY) { //this states that if we still have to get to the boundaries then we add more to velocity and change our position accordinly

                velocityY += accelY;
                positionY -= velocityY;


                setPositionY(positionY);//sets our objects position
                doneMoving = false;
            }
        } else if(myDir == GameLoopTask.gameDirections.DOWN) { //if our direction is down
            if((positionY + velocityY) > destinationY) { //same logic as before. If our velocity will lead our picture clipping out of our gameboard, then it will just set our picture to be on the edge of the boundary instead
                positionY = destinationY;
                setPositionY(destinationY);
                doneMoving = true;
            }else if(positionY < destinationY) { //if our position still needs to get to the boundary then we add to velocity and change velocity accordingly while setting our objects position to that position
                velocityY += accelY;
                positionY += velocityY;

                setPositionY(positionY);
                doneMoving = false;
            }
        } else if(myDir == GameLoopTask.gameDirections.LEFT) { //if direction is left. Same logic as before. Just in X axis
            if((positionX - velocityX) < destinationX) {
                positionX = destinationX;
                setPositionX(destinationX);
                doneMoving = true;
            }else if(positionX > destinationX) {
                velocityX += accelX;
                positionX -= velocityX;

                setPositionX(positionX);
                doneMoving = false;
            }

        } else if (myDir == GameLoopTask.gameDirections.RIGHT) { //if direction is right. Same logic as before
            if((positionX + velocityX) > destinationX) {
                positionX = destinationX;
                setPositionX(destinationX);
                doneMoving = true;

            }else if(positionX < destinationX) {
                velocityX += accelX;
                positionX += velocityX;

                setPositionX(positionX);
                doneMoving = false;
            }
        }
    }
}
