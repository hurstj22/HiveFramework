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
 * HiveComputerPlayer1 serves as our dumb computer player class
 * it randomly picks a move to carry out and tries to do it
 *
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 *
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
    private ArrayList<Tile> potentialMoves = null; //this comes from the gameState

    private HiveMoveAction moveAction;
    private HiveSelectAction selectAction;
    private EndTurnAction endTurn;
    private Tile.Bug[] bugArray; //holds the type of all the bugs possible to play for the computer to select from

    //private static final List<Tile.Bug> VALUES =
    //        Collections.unmodifiableList(Arrays.asList(values()));
    //private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    //public static Tile.Bug randomLetter()  {
    //    return VALUES.get(RANDOM.nextInt(SIZE));
    //}

    /**
     * constructor does nothing extra
     */
    public HiveComputerPlayer1(String name) {
        super(name);
        bugArray = new Tile.Bug[] {Tile.Bug.QUEEN_BEE, Tile.Bug.SPIDER, Tile.Bug.BEETLE, Tile.Bug.ANT, Tile.Bug.GRASSHOPPER};
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        Log.d(TAG, "receiveInfo: receivng for computer");
        if(info == null){
            Log.d(TAG, "receiveInfo: info is null and returing ");
            return;
        }

        if(info instanceof HiveGameState) {
            hiveGame = new HiveGameState((HiveGameState) info);

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
                //need to create a random tile, then call isValid on the tile, as long as it's a validTile to create
                // ie there's still that type of bug left in the playersHand array
                //then you can get the potentials from the hiveGame.getPotentialMoves(). Then add the tile you created to the move.setCurrentTile(randTile) AND
                //add the x and y's of the first potential spot to the move thus simulating onTouch commands by using the constructor.
                // Then call move.setComputerMove to true to let the game know the computer is trying to move. Then call game.sendAction(move)

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

                game.sendAction(endTurn); //ends the turn if there's nothing valid to do
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
