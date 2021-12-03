package com.example.hiveframework.hive.players;

import android.util.Log;
import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.actionMessage.EndTurnAction;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.infoMessage.IllegalMoveInfo;
import com.example.hiveframework.GameFramework.infoMessage.NotYourTurnInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveSelectAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * HiveComputerPlayer1 serves as our less dumb computer player class
 * it randomly picks a move to carry out and tries to do it
 * can move from the hand to the board and from the board to the board
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
    private static final String TAG = "HiveLessDumbAiPlayer1";
    private Random randPick = new Random();
    private boolean hasTapped = false;
    private int newX = -1;
    private int newY = -1; //store the coordinates that the player wants to move to, used in the touch event
    private Tile potentialTile = null;
    //array list of buttons to easily loop through and highlight the selected one
    private ArrayList<Tile> potentialMoves = null; //this comes from the gameState

    private HiveMoveAction moveAction;
    private IllegalMoveInfo illegalMove = new IllegalMoveInfo();
    private EndTurnAction endTurn = new EndTurnAction(this);
    private Tile.Bug[] bugArray; //holds the type of all the bugs possible to play for the computer to select from
    private Random whatAmIDoingToday = new Random();
    private boolean played = false;
    private Tile tileFromHand = null; //tile that the computer is playing from their hand

    /**
     * picks a random tile from the getComputerTiles() arrayList
     * used to choose what tile to move
     *
     * @return a randomly picked tile that the computer player has on the board
     */
    private Tile randomTileFromBoard() {
        int sizeOfList = hiveGame.getComputerPlayersTiles().size();
        if(!hiveGame.getComputerPlayersTiles().isEmpty()){
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
        //need to create a random tile, then call isValid on the tile
        if(potentialMoves != null){
            if(!potentialMoves.isEmpty()) {
                int pick = randPick.nextInt(potentialMoves.size());
                return potentialMoves.get(pick);
            }
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
    https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    */
    
    /**
     * constructor does nothing extra other than initializes the player
     * and an array of types of bugs that could be placed
     */
    public HiveComputerPlayer1(String name) {
        super(name);
        bugArray = new Tile.Bug[] {Tile.Bug.QUEEN_BEE, Tile.Bug.SPIDER, Tile.Bug.BEETLE, Tile.Bug.ANT, Tile.Bug.GRASSHOPPER};
    }

    @Override
    /**
     * Receives info for the computer plauer to use
     * to either send a move from hand action, move from board, or skip action
     */
    protected void receiveInfo(GameInfo info) {

        if(info == null || info instanceof NotYourTurnInfo){
            return;
        }

        if(info instanceof HiveGameState) {
            hiveGame =  new HiveGameState((HiveGameState) info);
            if (this.playerNum != hiveGame.getWhoseTurn()) {
                return; //not you're turn
            }
            if(played){
                played = false;
                game.sendAction(endTurn);
                return;
            }

            int computersMove = 0;
            if(hiveGame.getComputerPlayersTiles().size() > 2){ //there's tiles on the board, so I got options :)
                computersMove = whatAmIDoingToday.nextInt(7);
            }
            switch(computersMove) {
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
                                sleep(1.5);
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
                                sleep(1.5);
                                played = true;

                                game.sendAction(moveAction); //now finally SSSEEEENNNDDDD ittttt
                            }
                        }Log.i(TAG, "this was not a valid move");
                   }
                    break;
                    case 2: case 6: //picking a tile from the tiles already placed on the board:
                    Log.i(TAG,"I'm trying to play from the board");
                    if(hiveGame.getComputerPlayersTiles().size() < 3){ //only move things on the board if there's several pieces there
                        Log.i(TAG,"There's not enough pieces for me to move around the board yet");
                        sleep(1.5);
                        played = true;

                        game.sendAction(endTurn);
                        return;
                    }
                    ArrayList<ArrayList<Tile>> gameBoard = new ArrayList<ArrayList<Tile>>();
                    gameBoard = hiveGame.getGameBoard();
                    int i =0;
                    int goalX = -1;
                    int goalY = -1;
                    boolean queenFound = false;
                    //From James: The gameboard is a 2D array you must iterate with nested loops
                    for (ArrayList<Tile> t : gameBoard) //iterate game board to see if queen is there
                        //if queen is found set new goal X and Y
                        //may not be able to move there
                    {
                        if(t != null) {
                            if (t.get(i).getType() == Tile.Bug.QUEEN_BEE) {
                                 queenFound = true;
                                 goalX = t.get(i).getIndexX();
                                 goalY = t.get(i).getIndexY();
                            }
                        }
                        i++;
                    }

                    Tile random = randomTileFromBoard();
                    if(random != null) {//there was nothing belonging to the ai on the board :(
                        Log.i(TAG, "I'm trying to move my: "+ random.getType());
                        //This is for moving from a board position to a new board position
                        if (hiveGame.selectTile(random)) {
                            potentialMoves = hiveGame.getPotentialMoves(); //then calls selectMove on it to see about populating those gosh darn potentialMoves
                            potentialTile = randomTileFromPotentials(); //gets a random viable moving location
                            if (queenFound){
                                //potentialTile.setIndexX(goalX); //From James: this is trying to place the tile on top of the queen and that doesn't work
                                //potentialTile.setIndexY(goalY);
                            }
                            //create the action with the coordinates we'd like to move to from the potential tile
                            Log.i(TAG, "I'm moving to this location: ("+  potentialTile.getIndexX() + ","+ potentialTile.getIndexY() + ")");
                            moveAction = new HiveMoveAction(this, potentialTile.getIndexX(), potentialTile.getIndexY());
                            moveAction.setCurrentTile(random); //this is the tile that's moving
                            moveAction.setComputerPotentialMoves(potentialMoves); //copy over the local potential moves to the computers, to be used in hiveLocalGame
                            moveAction.setComputerMove(true); //tell the game the computer is moving

                            played = true;
                            game.sendAction(moveAction);
                        }
                    }
                        break;
                case 1: //The computer player decides to skip their turn
                    Log.i(TAG,"Today I will skip my turn");
                    if( hiveGame.getPotentialMoves() != null)
                    {
                        if (hiveGame.getPotentialMoves().size()  < 1)
                        sleep(1.5);
                    }
                    played = false;
                    game.sendAction(endTurn);
                    return;
            }
            sleep(1.5);
            game.sendAction(endTurn); //ends the turn if there's nothing valid to do
            return;
        }
    }
}
