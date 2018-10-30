package uwaterloo.ca.lab4_203_02;


import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

public class GameLoopTask extends TimerTask { //this is the timers task that it will use. Uextends timertask as all those functions in timertask are needed

    private Activity myActivity;
    private Context myContext;
    public RelativeLayout MyRL; //our relative layout variable as updates will need to be made to images
    public static LinkedList<GameBlock> myGameBlocks = new LinkedList<>();//Linked list of all game blocks
    public static final int TOP_BOUNDARY = -91;
    public static final int LEFT_BOUNDARY = -91;
    public static final int RIGHT_BOUNDARY = 500;
    public static final int BOT_BOUNDARY = 500;
    public static final int SLOT_SEPARATION = 197;
    boolean[][] slotArray;
    int[] genBlock = new int[2];
    private boolean AllDoneMoving = false;

    public boolean gameOver = false;
    public boolean createBlock = false;



    public enum gameDirections{UP, DOWN, LEFT, RIGHT, NO_MOVEMENT}; //fsm
    public gameDirections direction = gameDirections.NO_MOVEMENT; //sets our direction to be initially no movement
    protected GameBlock newBlock;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameLoopTask(Activity a, Context c, RelativeLayout r) { //contructor
        myActivity = a; 
        myContext = c;
        MyRL = r;

        try {
            createBlock();//creates instance of block class
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ;createBlock(106, 303)
    }

    private int getRandomInt(int bound) {
        Random rdm = new Random();
        return rdm.nextInt(bound);
    }

    public static GameBlock getGameBlock(int x, int y) {
        GameBlock target = null;
        for (GameBlock e: myGameBlocks) {
            if(e.getCoords()[0] == x && e.getCoords()[1] == y){
                target = e;
            }
        }
        return target;
    }

    public static void updateNum(GameBlock block) {
        block.blockNum = block.blockNum*2;
        block.blockTV.setText(String.valueOf(block.blockNum));
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void createBlock(int x, int y) {
        newBlock = new GameBlock(myContext, x, y, MyRL);
        myGameBlocks.add(newBlock);
    }
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void createBlock(){ //our create block method used in this time
        slotArray = new boolean[4][4];
        boolean isEmpty = false;
        int blockNum = 0;
        int emptyNum = 15;

            for (GameBlock e: myGameBlocks) {
                int[] blockCoord = new int[2];
                blockCoord = e.getSlotArray();

                slotArray[blockCoord[1]][blockCoord[0]] = true;
//                printArray();
                blockNum++;
                emptyNum--;
            }


        if(emptyNum != 0) {
            int rdmIndex = getRandomInt(emptyNum+1);
//            System.out.println("Random int: " + rdmIndex);
            getSlot(rdmIndex);
        } else {
            gameOver = true;
            System.out.println("YOU LOST! GAME OVER!");
        }

        int genCoord[] = new int[2];
        genCoord = getCoords(genBlock);

//        System.out.println("gencoords at: " + genCoord[0] + ", " + genCoord[1]);

//        int rdmX = LEFT_BOUNDARY + getRandomInt()*SLOT_SEPARATION;
//        int rmdY = TOP_BOUNDARY + getRandomInt()*SLOT_SEPARATION;

        if(!gameOver) {
            newBlock = new GameBlock(myContext, genCoord[1], genCoord[0], MyRL);
            myGameBlocks.add(newBlock);//add the new block to the linked list

        }


    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void setDirection(gameDirections newDirection){ //set direction to be whatever our new direction is
        if(!gameOver) {
            direction = newDirection;



            for (GameBlock e: myGameBlocks) {

                e.setBlockDirection(newDirection);
            }

            for (GameBlock e: myGameBlocks) {

                e.setDestination();
            }

            createBlock();



        } else {
            System.out.println("GAME OVER!");
        }
    }

    public static int getNumber(int x, int y) {
        for (GameBlock e: myGameBlocks) {
            if(e.getCoords()[0] == x && e.getCoords()[1] == y) {
                return e.blockNum;
            }
        }

        return 0;
    }
//
//    private void printArray() {
//        for(int i=0; i<4;i++) {
//            for(int j=0; j<4; j++) {
//                if(i == 0) {
//                    System.out.print(slotArray[i][j] + " ");
//                } else if(i == 1 && j == 0) {
//                    System.out.println();
//                    System.out.print(slotArray[i][j] + " ");
//
//                } else if(i==2 && j == 0) {
//                    System.out.println();
//                    System.out.print(slotArray[i][j] + " ");
//                } else if(i == 3 && j==0){
//                    System.out.println();
//                    System.out.print(slotArray[i][j] + " ");
//                } else if(i == 3 && j == 3) {
//                    System.out.println(slotArray[i][j] + " ");
//                } else {
//                    System.out.print(slotArray[i][j] + " ");
//                }
//
//
//            }
//        }
//    }

    public void getSlot(int index) {
        for(int i = 0; i < slotArray[0].length; i++) {
            for(int j = 0; j < slotArray.length; j++) {
                if(!slotArray[i][j]) {
                    if(index == 0) {
                        genBlock[0] = i;
                        genBlock[1] = j;


                    }
                    index--;
                }

            }
        }
    }

    public int[] getCoords(int[] slot) {
        int[] coords = new int[3];

        switch(slot[0]){
            case 0:
                coords[0] = -91;
                break;
            case 1:
                coords[0] = 106;
                break;
            case 2:
                coords[0] = 303;
                break;
            case 3:
                coords[0] = 500;
                break;
            default:
                coords[0] = -1;
                break;
        }

        switch(slot[1]){
            case 0:
                coords[1] = -91;
                break;
            case 1:
                coords[1] = 106;
                break;
            case 2:
                coords[1] = 303;
                break;
            case 3:
                coords[1] = 500;
                break;
            default:
                coords[1] = -1;
                break;
        }
        return coords;
    }

    public static boolean isOccupied(int x, int y) {
        for (GameBlock e: myGameBlocks) {
            if(e.getCoords()[0] == x && e.getCoords()[1] == y) {
                return true;
            }
        }
        return false;
    }

    public void run(){ //when this timer task is run
        final LinkedList<GameBlock> RemoveBlocks = new LinkedList<>();
        myActivity.runOnUiThread(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    @Override
                    public void run() {

                        for (GameBlock e: myGameBlocks) {

                            e.move();//when runs, will call the move function in new block class
                            if(e.blockNum == 64) {
                                gameOver = true;
                                System.out.println("YOU WON!!");

                            }

                             //check if all blocks are done moving

                                if(!e.doneMoving) {
                                    AllDoneMoving = false;
                                } else {
                                    AllDoneMoving = true;
                                }

//                                System.out.println("Done Moving: " + AllDoneMoving);
//                                System.out.println("createBlock: " + createBlock);

//                            try{
//                                if(createBlock && AllDoneMoving) {
//                                    createBlock();
//                                    createBlock = false;
//                                }
//                            } catch (Exception er) {
//                                System.out.println("ERROR: " + er);
//                            }



                            if(e.doneMoving && e.Removed) {
                                e.destroyMe();
                                RemoveBlocks.add(e);
//                                System.out.println("Gameblocks size: " + myGameBlocks.size());
                            }
                        }

                        for (GameBlock e: RemoveBlocks) {
                            myGameBlocks.remove(e);

                        }

                    }
                }
        );
    }
}
