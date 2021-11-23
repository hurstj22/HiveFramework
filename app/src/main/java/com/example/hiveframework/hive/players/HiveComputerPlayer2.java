package com.example.hiveframework.hive.players;

import android.util.Log;
import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.actionMessage.EndTurnAction;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.infoMessage.NotYourTurnInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;

import java.util.ArrayList;
import java.util.Random;

/**
 * HiveComputerPlayer2 serves as our dumb computer player class
 * it randomly picks a move to carry out
 *
 * @author Isaac Reinhard
 * @author Kelly Ngyuen
 * @author Ali Sheehan
 * @author James Hurst
 * @version November 2021
 *
 */
public class HiveComputerPlayer2 extends GameComputerPlayer {

    private HiveGameState hiveGame;
    private static final String TAG = "HiveDumbAiPlayer2";
    private Random randPick = new Random();
    private int newX = -1;
    private int newY = -1; //store the coordinates that the player wants to move to, used in the touch event
    private int oldX = -1;
    private int oldY = -1;
    private Tile currentTile = null;
    private ImageButton selectedImageButton = null; //if null nothing selected, if not null this points to what is selected
    //array list of buttons to easily loop through and highlight the selected one
    private ArrayList<Tile> potentialMoves = null; //this comes from the gameState

    private HiveMoveAction moveAction;
    private EndTurnAction endTurn = new EndTurnAction(this);
    private Tile.Bug[] bugArray; //holds the type of all the bugs possible to play for the computer to select from
    private Random whatAmIDoingToday = new Random();

    private boolean played = false; //if the computer has completed an action successfully
    private Tile tileFromHand = null; //tile that the computer is playing from their hand

    /**
     * picks a random tile from the getComputerTiles() arrayList
     * used to choose what tile to move
     *
     * @return a randomly picked tile that the computer player has on the board
     */
    private Tile randomTileFromBoard() {
        int sizeOfList = hiveGame.getComputerPlayersTiles().size();
        if(sizeOfList > 0){
            int pick = randPick.nextInt(sizeOfList);
            return hiveGame.getComputerPlayersTiles().get(pick);
        }
        return null; //there was nothing on the board, so there's nothing to return :(
    }

    /**
     * picks a random tile from the potentials list
     * Used to pass in viable coordinates that the computer could move to
     *
     * @return a randomly picked tile from the list of potential spots that are acceptable to move
     */
    private Tile randomTileFromPotentials() {
        if(potentialMoves != null){
            int pick = randPick.nextInt(potentialMoves.size());
            return potentialMoves.get(pick);
        }
        return null; //if there's nothing in the potentials throw back null
    }

    /**
     * creates a random tile to be used to place a tile from the hand to the board
     * @return a randomly generated tile with indices at -1 to indicate the tile is coming from the board
     */
    private Tile randomTileFromHand(){
        int id = -1; //this is going to be a generic computer id
        Tile.Bug type = bugArray[randPick.nextInt(5)]; //0 - 4, selects random type of bug to create
        Tile.PlayerPiece piece;
        if(hiveGame.getWhoseTurn() == 0){
            piece = Tile.PlayerPiece.W; //1st player
        }
        else{
            piece = Tile.PlayerPiece.B; //2nd player
        }
        return new Tile(-1, -1, piece, type, id); //returns a completely randomly generated tile
    }

    /**
     * creates a queen tile to be placed on the board
     * @return a generated tile with type queen bee and indices at -1 to indicate the tile is coming from the board
     */
    private Tile queenFromHand(){
        int id = -1; //this is going to be a generic computer id
        Tile.PlayerPiece piece;
        if(hiveGame.getWhoseTurn() == 0){
            piece = Tile.PlayerPiece.W; //1st player
        }
        else{
            piece = Tile.PlayerPiece.B; //2nd player
        }
        return new Tile(-1, -1, piece, Tile.Bug.QUEEN_BEE, id); //returns a completely randomly generated tile
    }

    /**
     * constructor does nothing extra other than initializes the player
     * and an array of types of bugs that could be placed
     */
    public HiveComputerPlayer2(String name) {
        super(name);
        bugArray = new Tile.Bug[] {Tile.Bug.QUEEN_BEE, Tile.Bug.SPIDER, Tile.Bug.BEETLE, Tile.Bug.ANT, Tile.Bug.GRASSHOPPER};
    }

    @Override
    /**
     * Receives all information for the computer player to place a tile on the board
     */
    protected void receiveInfo(GameInfo info) {
        //Log.d(TAG, "receiveInfo: receivng for computer");

        if(info == null || info instanceof NotYourTurnInfo){
            Log.d(TAG, "receiveInfo: info is null and returning ");
            return;
        }

        if(info instanceof HiveGameState) {
            hiveGame =  new HiveGameState((HiveGameState) info);

            //Log.d(TAG, "receiveInfo: player is " + this.playerNum);
            //Log.d(TAG, "receiveInfo: line 121 turn is " + hiveGame.getWhoseTurn());
            //Log.d(TAG, "pm: "+ potentialMoves);
            if (this.playerNum != hiveGame.getWhoseTurn()) {
                Log.i(TAG, "receiveInfo: NOT YOUR TURN");
                Log.i(TAG, "receiveInfo: this.playerNum:"+ this.playerNum);
                Log.i(TAG, "receiveInfo: hiveGame.whoseTurn:" + hiveGame.getWhoseTurn());
                return; //not you're turn
            }
            if(played){
                played = false;
                game.sendAction(endTurn);
                return;
            }
            Log.d(TAG, "receiveInfo: it is comps turn ");
            //need to create a random tile, then call isValid on the tile
            int computersMove = 0;
            if(hiveGame.getComputerPlayersTiles().size() > 2){ //there's tiles on the board, so I got options :)
                computersMove = whatAmIDoingToday.nextInt(6);
            }
            switch(computersMove) { //I believe this picks 0 - 2 since it's not inclusive of 3.
                //This switch statement is the brains of the operation: chooses between placing on the board, moving from one spot to another, and skipping turn

                case 3: //several chances for the ai to choose from their hand
                case 4:
                case 5:
                case 0: //picking a tile from the computer's hand and placing it on the board
                    Log.i(TAG,"I'm trying to play from my hand");
                    if(hiveGame.getPiecesRemain()[playerNum][0] == 1){ //if you haven't put the queen down... do it! Comp always plays queen first
                        tileFromHand = queenFromHand(); //put the queen down
                    }
                    else {
                        tileFromHand = randomTileFromHand(); //select a random other tile from the hand
                    }

                    Log.i(TAG,"My tile is a: "+ tileFromHand.getType());
                    if (hiveGame.getPiecesRemain(tileFromHand.getType()) > 0) { //makes sure player has at least one of that piece remaining
                        Log.i(TAG,"I have : "+ hiveGame.getPiecesRemain(tileFromHand.getType()) + " " + tileFromHand.getType() + "'s left");
                        if (hiveGame.validMove(tileFromHand)) { //if there's validMoves to be made populate the local potentialMoves
                            Log.i(TAG, "this was a valid move!");
                            potentialMoves = hiveGame.getPotentialMoves();
                            if (potentialMoves == null || potentialMoves.size() <= 0) { //if for some reason there was nothing to do with that Tile then end your turn :(
                                Log.i(TAG, "receiveInfo: potentials was empty:");
                                sleep(2);
                                played = true;

                                game.sendAction(endTurn); //ends the turn if there's nothing valid to do
                                return; //there's nothing in the potentials list
                            } else { //yay we have things we can do
                                Tile potentialTile = randomTileFromPotentials(); //so lets get one of those potentialTiles jimbo
                                newX = potentialTile.getIndexX();
                                newY = potentialTile.getIndexY();
                                Log.i(TAG, "I'm moving to this location: ("+ newX + ","+ newY + ")");

                                moveAction = new HiveMoveAction(this, newX, newY);
                                moveAction.setCurrentTile(tileFromHand); //update with what tile was originally moving from the hand
                                moveAction.setComputerPotentialMoves(potentialMoves); //copy over the local potential moves to the computers
                                // so that the gameState can be updated later in the HiveLocalGame
                                moveAction.setComputerMove(true);
                                sleep(2);
                                played = true;

                                game.sendAction(moveAction); //now finally SSSEEEENNNDDDD ittttt
                            }
                        }Log.i(TAG, "this was not a valid move");
                    }
                    break;
                case 1: //The computer player decides to skip their turn
                    Log.i(TAG,"Today I will skip my turn");
                    sleep(2);
                    played = false;
                    game.sendAction(endTurn);
                    return;
            }
            played = false;
            game.sendAction(endTurn); //ends the turn if there's nothing valid to do
            return;
        }
    }

}
