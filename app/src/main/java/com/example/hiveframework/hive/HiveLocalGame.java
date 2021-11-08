package com.example.hiveframework.hive;
import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.actionMessage.EndTurnAction;
import com.example.hiveframework.GameFramework.infoMessage.GameState;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.LocalGame;
import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveSelectAction;
import com.example.hiveframework.hive.players.HiveHumanPlayer1;

import java.util.ArrayList;

import edu.up.cs301.game.R;
/**
 * HiveLocalGame holds all information pertaining to a specific instance of the game
 * the game as it runs for a turn or move locally on the device before being sent over the framework
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 */
public class HiveLocalGame extends LocalGame {
    //Tag for logging
    private static final String TAG = "HiveLocalGame";

    Tile currentTile;
    int[] newIndexPt = new int[2]; //index pair, (col, row) for going from float to gameBoard index
    int oldX;
    int oldY;
    int newX;
    int newY;
    Tile.PlayerPiece piece;
    Tile.Bug type;

    /**
     * Constructor for the HiveLocalGame.
     */
    public HiveLocalGame() {

        // perform superclass initialization
        super();

        // create a new, unfilled-in HiveGameState object
        super.state = new HiveGameState();
        //initialize coords to be used later, -1 means nothing is there
        oldX = -1;
        oldY = -1;
    }

    /**
     * Constructor for the HiveLocalGame with loaded HiveState
     * @param hiveGameState
     */
    public HiveLocalGame(HiveGameState hiveGameState){
        super();
        super.state = new HiveGameState(hiveGameState);
        oldX = -1;
        oldY = -1;
    }

    /**
     * Check if the game is over. It is over, return a string that tells
     * who the winner(s), if any, are. If the game is not over, return null;
     *
     * @return
     * 		a message that tells who has won the game, or null if the
     * 		game is not over
     */
    @Override
    protected String checkIfGameOver() {

        HiveGameState state = (HiveGameState) super.state;

        // if we get here, then we've found a winner, so return the 0/1
        // value that corresponds to that mark; then return a message
        int gameWinner = state.getWhoseTurn();
        //return playerNames[gameWinner]+" is the winner.";
        return null;
    }

    /**
     * Notify the given player that its state has changed. This should involve sending
     * a GameInfo object to the player. If the game is not a perfect-information game
     * this method should remove any information from the game that the player is not
     * allowed to know.
     *
     * @param p
     * 			the player to notify
     */
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        // make a copy of the state, and send it to the player
        p.sendInfo(new HiveGameState(((HiveGameState) state)));
    }

    /**
     * Tell whether the given player is allowed to make a move at the
     * present point in the game.
     *
     * @param playerIdx
     * 		the player's player-number (ID)
     * @return
     * 		true iff the player is allowed to move
     */
    protected boolean canMove(int playerIdx) {
        return playerIdx == ((HiveGameState)state).getWhoseTurn();
    }

    /**
     * Makes a move on behalf of a player.
     *
     * @param action
     * 			The move that the player has sent to the game
     * @return
     * 			Tells whether the move was a legal one.
     */
    @Override
    protected boolean makeMove(GameAction action) {
        if(action == null){
            return false;
        }

        HiveGameState hiveState = (HiveGameState) super.state; //cast new state to reference as a hiveState
        // get the 0/1 id of our player
        int playerId = getPlayerIdx(action.getPlayer());
        // get the 0/1 id of the player whose move it is
        int whoseMove = hiveState.getWhoseTurn();
        if(action instanceof EndTurnAction){
            hiveState.setWhoseTurn(1 - whoseMove);
        }

        if(canMove(playerId)) {
            HiveSelectAction select = null; //start out as null
            HiveMoveAction move = null;

            if (action instanceof HiveSelectAction) { //if we were passed a request to select from board or hand
                select = (HiveSelectAction) action;
                if(hiveState.getPiecesRemain(select.getSelectedTile().getType()) <= 0){ //makes sure player has at least one of piece remaining
                    return false;
                }
                if(select.getSelectedImageButton() != null){ //selecting from the player's hand //put this in the HiveHumanPlayer
                    return hiveState.validMove(select.getSelectedTile()); //pass the newly created tile to calculate all possible moves
                }

                else{ //selecting from the game board
                    //get oldX and oldY and determine what tile they correspond to, then call gameState validMove on those
                    oldX = (int) select.getX();
                    oldY = (int) select.getY();
                    if(hiveState.getTile(oldX, oldY) == null){
                        return false; //request is out of bounds
                    }
                    if(hiveState.getTile(oldX, oldY).getType() != Tile.Bug.EMPTY){
                        return hiveState.validMove(hiveState.getTile(oldX, oldY));
                    }
                    return false; //selected an empty spot, there's no bug there to ask to move
                }

            } else if (action instanceof HiveMoveAction) { //we've been passed a request to move a piece
                move = (HiveMoveAction) action;

                if(move.getSelectedImageButton() != null){ //moving from hand to board
                    move.setSelectedImageButton(null); //reset the imageButton
                    //select.setSelectedImageButton(null); //reset the imagebutton
                    if(hiveState.makeMove(move.getCurrentTile(), (int) move.getX(), (int) move.getY())){
                        return true; //able to make the move
                    }
                }
                else if (move.isComputerMove()){ //the computer is trying to make a move
                    move.setComputerMove(false); //reset the computer since it tried to make a move
                    if(hiveState.makeMove(move.getCurrentTile(), (int) move.getX(), (int) move.getY())){
                        return true; //computer is able to make the move
                    }
                }
                else{ //moving from board spot to board spot
                    newX = (int) move.getX();
                    newY = (int) move.getY();
                    //now reset the move and select
                    //move.setCurrentTile(null);
                    //select.setSelectedTile(null);

                    if(hiveState.makeMove(hiveState.getTile(oldX, oldY), newX, newY)) { //pass in the tile to move
                        return true; //able to make the move
                    }
                }
            }
            // return false, indicating there wasn't any legal move
            return false;
        }
        return false;
    }

    /**
     *
     * @return
     */
    public int whoWon(){
        String gameOver = checkIfGameOver();
        if(gameOver == null || gameOver.equals("It's a cat's game.")) return -1; //if both queens are trapped
        if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
        return 1;
    }

}
