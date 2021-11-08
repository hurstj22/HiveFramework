package com.example.hiveframework.hive;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.infoMessage.IllegalMoveInfo;
import com.example.hiveframework.GameFramework.infoMessage.NotYourTurnInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.utilities.Logger;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.HiveSurfaceView;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveSelectAction;

import java.util.ArrayList;

import edu.up.cs301.game.R;

public class LessDumbAI extends GameComputerPlayer  {

    //Tag for logging
    private static final String TAG = "TTTLessDumbAiPlayer1";
    private HiveGameState hiveGame;
    // the surface view
    private HiveSurfaceView surfaceView;

    private ArrayList<Tile> potentialMoves;

    // the ID for the layout to use
    private int layoutId;

    /**
     * constructor
     *
     * @param name
     * 		the player's name
     * @param layoutId
     *      the id of the layout to use
     */
    public LessDumbAI
    (String name, int layoutId) {
        super(name);
        this.layoutId = layoutId;
    }

    /**
     * Callback method, called when player gets a message
     *
     * @param info
     * 		the message
     */
    @Override
    public void receiveInfo(GameInfo info) {

        if (surfaceView == null) return;

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            surfaceView.flash(Color.RED, 50);
        }
        else if (!(info instanceof HiveGameState))
            // if we do not have a HiveGameState, ignore
            return;
        else {
                //make a copy of the passed in gameState to pull data from
                hiveGame = new HiveGameState((HiveGameState) info);

            }

        while(hiveGame.getWhoseTurn() == 1){
            potentialMoves = hiveGame.getPotentialMoves();
            Log.d(TAG, "pm: "+ potentialMoves);

        }
    }

    public boolean DumbTurn() {
        float newX = -1;
        float newY = -1;
        potentialMoves = hiveGame.getPotentialMoves();
        Log.d(TAG, "pm: "+ potentialMoves);

            //convert coordinates to position in gameBoard
/**
        newX = gameBoardPosition[1];
            newY = gameBoardPosition[0];

            if(selectedImageButton != null){ //the player has selected one of the pieces to "place" on the board
                HiveMoveAction moveActionFromHand = new HiveMoveAction(this, newX, newY);
                moveActionFromHand.setSelectedImageButton(selectedImageButton);
                //not sure if I can reset it since possibly it's just a pointer that gets assigned in moveAction... not sure about this one
                setSelectedImageButton(null); //reset the selected image since either nothing is going to happen or the pieces will move
                moveActionFromHand.setCurrentTile(currentTile); //set the tile that the action is working on moving
                game.sendAction(moveActionFromHand);
            }
            else if(!hasTapped){ //selecting from the board so pass a selectAction with the x and y coords
                oldX = newX;
                oldY = newY;
                hasTapped = !hasTapped;
                game.sendAction(new HiveSelectAction(this, oldX, oldY));
            }
            else if(hasTapped && oldX != -1){ //this is the second time tapping so you've selected a gameboard tile and now another gameboard tile
                game.sendAction(new HiveMoveAction(this, newX, newY));
            }
            return true;



    */return false;}
}
