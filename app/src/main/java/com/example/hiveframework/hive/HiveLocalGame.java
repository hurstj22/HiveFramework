package com.example.hiveframework.hive;
import android.util.Log;
import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.actionMessage.EndTurnAction;
import com.example.hiveframework.GameFramework.infoMessage.GameState;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.LocalGame;
import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveSelectAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveUndoTurnAction;
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

    int oldX; //for going from float to gameBoard index
    int oldY;
    int newX;
    int newY;

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
        int gameWinner = state.winOrNah();
        if(gameWinner > 0) { //someone won!
            // if we get here, then we've found a winner, so return the 0/1
            if(gameWinner == 2) {
                return "It's a cat's game.";
            }
            return playerNames[gameWinner] + " is the winner.";
        }
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
            hiveState.setPlacedPiece(false);
            return true; //successfully changed turns
        }

        if(action instanceof HiveUndoTurnAction) {
            //HiveUndoTurnAction undoTurnAction = (HiveUndoTurnAction) action;
            //HiveGameState undoTurn = undoTurnAction.getStartTurn();
            hiveState = new HiveGameState(hiveState.getUndoTurn());
            return true;
        }
        if(canMove(playerId)) {
            HiveSelectAction select = null; //start out as null
            HiveMoveAction move = null;

            if (action instanceof HiveSelectAction) { //if we were passed a request to select from board or hand
                select = (HiveSelectAction) action;

                if(select.getX() > -1){ //selecting from the game board
                    //get oldX and oldY and determine what tile they correspond to, then call gameState validMove on those
                    oldX = (int) select.getX();
                    oldY = (int) select.getY();
                    if(hiveState.getTile(oldX, oldY) == null){
                        return false; //request is out of bounds
                    }
                    else if(hiveState.getWhoseTurn() == 0 &&
                            hiveState.getTile(oldX, oldY).getPlayerPiece() != Tile.PlayerPiece.W){
                        return false; //trying to select someone elses piece! :( How could you!?
                    }
                    else if(hiveState.getWhoseTurn() == 1 &&
                            hiveState.getTile(oldX, oldY).getPlayerPiece() != Tile.PlayerPiece.B){
                        return false; //trying to select someone elses piece! :( How could you!?
                    }
                    else if(hiveState.getTile(oldX, oldY).getType() != Tile.Bug.EMPTY){
                        return hiveState.selectTile(hiveState.getTile(oldX, oldY));
                    }
                    return false; //selected an empty spot, there's no bug there to ask to move
                }

                if(select.getSelectedImageButton() != null){ //selecting from the player's hand
                    if(select.getSelectedTile() == null){
                        return false; //no image was selected
                    }
                    else if(hiveState.getPiecesRemain(select.getSelectedTile().getType()) <= 0) { //makes sure player has at least one of piece remaining
                        return false;
                    }
                    hiveState.setSelectFlag(true);
                    hiveState.setCurrentIdSelected(select.getSelectedImageButton().getId());

                    return hiveState.validMove(select.getSelectedTile()); //pass the newly created tile to calculate all possible moves
                }

            } else if (action instanceof HiveMoveAction) { //we've been passed a request to move a piece
                move = (HiveMoveAction) action;

                if(move.getSelectedImageButton() != null){ //moving from hand to board (only for human tho)
                    hiveState.setCurrentIdSelected(-1); //reset the imageButton
                    hiveState.setSelectFlag(false);
                    if(hiveState.makeMove(move.getCurrentTile(), (int) move.getX(), (int) move.getY())){
                        //set the move boolean in hiveState to true
                        hiveState.setPlacedPiece(true);
                        return true; //able to make the move
                    }
                }
                else if (move.isComputerMove()){ //the computer is trying to make a move
                    hiveState.setPotentialMoves(move.getComputerPotentialMoves()); //update the gameStates potentials to be compared against in the makeMove
                    if(hiveState.makeMove(move.getCurrentTile(), (int) move.getX(), (int) move.getY())){
                        move.getCurrentTile().setIndexX((int) move.getX()); //update the x and y indices
                        move.getCurrentTile().setIndexY((int) move.getY());

                        hiveState.addComputerPlayersTiles(move.getCurrentTile()); //now add it to the arrayList kept in the gameState of all computer tiles
                        move.setComputerMove(false); //reset the computer since it tried to make a move
                        return true; //computer was able to make the move
                    }
                }
                else if(hiveState.getTile(oldX, oldY).getType() != Tile.Bug.EMPTY){ //moving from board spot to board spot
                    newX = (int) move.getX();
                    newY = (int) move.getY();

                    if(hiveState.makeMove(hiveState.getTile(oldX, oldY), newX, newY)) { //pass in the tile to move
                        //set the move boolean in hiveState to true
                        hiveState.setPlacedPiece(true);
                        return true; //able to make the move
                    }
                }
            }
            Log.i("makeMove", "Unable to perform requested move*****");
            // return false, indicating there wasn't any legal move
            return false;
        }
        return false;
    }

    /**
     *
     * @return an int representation of who won, 0 for a player, 1 for a cat's game
     */
    public int whoWon(){
        String gameOver = checkIfGameOver();
        if(gameOver == null) return -1; //if both queens are trapped
        if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
        if(gameOver.equals("It's a cat's game.")) return 1;

        return -1;
    }

}
