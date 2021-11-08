package com.example.hiveframework.hive.players;

import android.util.Log;
import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.actionMessage.EndTurnAction;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveSelectAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

/**
 * Dumb ai class
 */
public class HiveComputerPlayer1 extends GameComputerPlayer {
    private HiveGameState hiveGame;
    private static final String TAG = "TTTLessDumbAiPlayer1";
    private Random randPick = new Random();
    private boolean hasTapped = false;
    private float inX = -1;
    private float inY = -1;
    private float newX = -1;
    private float newY = -1; //store the coordinates that the player wants to move to, used in the touch event
    private float oldX = -1;
    private float oldY = -1;
    private Tile currentTile = null;
    private ImageButton selectedImageButton = null; //if null nothing selected, if not null this points to what is selected
    //array list of buttons to easily loop through and highlight the selected one
    private ArrayList<Tile> potentialMoves; //this comes from the gameState

    private HiveMoveAction moveAction;
    private HiveSelectAction selectAction;

    /**
     * constructor does nothing extra
     */
    public HiveComputerPlayer1(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        Log.d(TAG, "receiveInfo: receivng for computer");

        if(info == null){
            Log.d(TAG, "receiveInfo: info is null and returing ");
            return;
        }

        if(info instanceof HiveGameState) {
            hiveGame =  new HiveGameState((HiveGameState) info);

            Log.d(TAG, "receiveInfo: player is " + this.playerNum);
            Log.d(TAG, "receiveInfo: line 64 turn is " + hiveGame.getWhoseTurn());
            Log.d(TAG, "pm: "+ potentialMoves);
            if (this.playerNum != hiveGame.getWhoseTurn()) {
                Log.d(TAG, "receiveInfo: NOT YOUR TURN");
                return; //not you're turn
            }

            potentialMoves = hiveGame.getPotentialMoves();
            if(potentialMoves == null) {
                return; //there's nothing in the potentials list
            }

            if (this.playerNum == hiveGame.getWhoseTurn()) {
                Log.d(TAG, "receiveInfo: it is comps turn ");
                //need to create a random tile, then call isValid by sending a game.sendAction(select) on the tile as long as it's a validTile to create
                //then you can get the potentials

                potentialMoves = hiveGame.getPotentialMoves();
                currentTile = potentialMoves.get(0); //gets current tile from PM
                //then you can call game.sendAction(move) on the currentTile
                Log.d(TAG, "current tile" + currentTile);
                if(currentTile.getType() != null){ //the player has selected one of the pieces to "place" on the board
                    newX = currentTile.getIndexX(); //gets new move from current tile
                    Log.d(TAG, "receiveInfo: newX" + newX);
                    newY = currentTile.getIndexY();
                    Log.d(TAG, "receiveInfo: newY" + newY);
                    HiveMoveAction moveActionFromHand = new HiveMoveAction(this, newX, newY);
                    Log.d(TAG, "receiveInfo: move action for computer");
                    //moveActionFromHand.setSelectedImageButton(selectedImageButton);
                    //not sure if I can reset it since possibly it's just a pointer that gets assigned in moveAction... not sure about this one
                    //setSelectedImageButton(null); //reset the selected image since either nothing is going to happen or the pieces will move
                    moveActionFromHand.setCurrentTile(currentTile); //set the tile that the action is working on moving
                    Log.d(TAG, "receiveInfo: sendMOVEEE");
                    game.sendAction(moveActionFromHand);

                }
                /*else if(!hasTapped){ //selecting from the board so pass a selectAction with the x and y coords
                    oldX = newX;
                    oldY = newY;
                    hasTapped = !hasTapped;
                    game.sendAction(new HiveSelectAction(this, oldX, oldY));
                }
                else if(hasTapped && oldX != -1){ //this is the second time tapping so you've selected a gameboard tile and now another gameboard tile
                    game.sendAction(new HiveMoveAction(this, newX, newY));
                }*/

            }
        }
    }

    //setter and getters for instance variables
    public ImageButton getSelectedImageButton() {
        return selectedImageButton;
    }

    public void setSelectedImageButton(ImageButton selectedImageButton) {
        this.selectedImageButton = selectedImageButton;
    }

    public float getNewX() {
        return newX;
    }

    public float getNewY() {
        return newY;
    }
}
