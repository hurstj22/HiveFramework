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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * HiveComputerPlayer1 serves as our less dumb computer player class
 * it randomly picks a move to carry out that would bring it
 * closer to the other player's queen bee and tries to do it
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
        //need to create a random tile, then call isValid on the tile
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
    protected void receiveInfo(GameInfo info) {

        if(info == null){

            return;
        }

        if(info instanceof HiveGameState) {
            hiveGame =  new HiveGameState((HiveGameState) info);
            if (this.playerNum != hiveGame.getWhoseTurn()) {
                return; //not you're turn
            }


            switch(whatAmIDoingToday.nextInt(6)) {//I believe this picks 0 - 2 since it's not inclusive of 3.
                //This switch statement is the brains of the operation: chooses between placing on the board, moving from one spot to another, and skipping turn

                case 3:
                case 4:
                case 5:
                case 0: //picking a tile from the computer's hand and placing it on the board
                    Log.i(TAG,"I'm trying to play from my hand");
                    Tile randomTileFromHand = randomTileFromHand();
                    Log.i(TAG,"My tile is a: "+ randomTileFromHand.getType());
                    if (hiveGame.getPiecesRemain(randomTileFromHand.getType()) > 0) { //makes sure player has at least one of that piece remaining
                        Log.i(TAG,"I have : "+ hiveGame.getPiecesRemain(randomTileFromHand.getType()) + " " + randomTileFromHand.getType() + "'s left");
                        if (hiveGame.validMove(randomTileFromHand)) { //if there's validMoves to be made populate the local potentialMoves
                            Log.i(TAG, "this was a valid move!");
                            potentialMoves = hiveGame.getPotentialMoves();
                            if (potentialMoves == null || potentialMoves.size() <= 0) { //if for some reason there was nothing to do with that Tile then end your turn :(
                                Log.i(TAG, "receiveInfo: potentials was empty:");
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                game.sendAction(endTurn); //ends the turn if there's nothing valid to do
                                return; //there's nothing in the potentials list
                            } else { //yay we have things we can do
                                Tile potentialTile = randomTileFromPotentials(); //so lets get one of those potentialTiles jimbo
                                newX = potentialTile.getIndexX();
                                newY = potentialTile.getIndexY();
                                Log.i(TAG, "I'm moving to this location: ("+ newX + ","+ newY + ")");

                                moveAction = new HiveMoveAction(this, newX, newY);
                                moveAction.setCurrentTile(randomTileFromHand); //update with what tile was originally moving from the hand
                                moveAction.setComputerPotentialMoves(potentialMoves); //copy over the local potential moves to the computers
                                                                                    // so that the gameState can be updated later in the HiveLocalGame
                                moveAction.setComputerMove(true);
                                game.sendAction(moveAction); //now finally SSSEEEENNNDDDD ittttt
                            }
                        }Log.i(TAG, "this was not a valid move");
                   }
                    break;
                case 5: //picking a tile from the tiles already placed on the board:
                    Log.i(TAG,"I'm trying to play from the board");
                    ArrayList<ArrayList<Tile>> gameBoard = new ArrayList<ArrayList<Tile>>();
                    gameBoard = hiveGame.getGameBoard();
                    int i =0;
                    int goalX = -1;
                    int goalY = -1;
                    boolean queenFound = false;
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
                        //then calls validMove on it to see about populating those gosh darn potentialMoves

                        //This is good for moving from a board position to a new board position
                        if (hiveGame.validMove(random)) {
                            potentialMoves = hiveGame.getPotentialMoves();
                            currentTile = randomTileFromPotentials(); //gets a random viable moving location
                            if (queenFound){
                                currentTile.setIndexX(goalX);
                                currentTile.setIndexY(goalY);
                            }else {
                                currentTile.setIndexX(random.getIndexX()); //I don't think you want to set these in this case, if you're pulling from potentials,
                                //the x and y's are already set. What you want to do is update the x and y of the moveAction
                                currentTile.setIndexY(random.getIndexY());
                            }
                            moveAction.setCurrentTile(random);
                            moveAction.setComputerMove(true);
                            game.sendAction(moveAction);
                        }
                    }
                        break;
                case 1: //The computer player decides to skip their turn
                    Log.i(TAG,"Today I will skip my turn");
                    if( hiveGame.getPotentialMoves() != null)
                    {
                        if (hiveGame.getPotentialMoves().size()  < 1)
                        {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    game.sendAction(endTurn);
                    return;
            }

            game.sendAction(endTurn); //ends the turn if there's nothing valid to do
            return;
        }
        game.sendAction(endTurn);
        return;
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
