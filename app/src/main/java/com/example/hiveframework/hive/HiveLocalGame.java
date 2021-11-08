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

public class HiveLocalGame extends LocalGame {
    //Tag for logging
    private static final String TAG = "HiveLocalGame";

    Tile currentTile;
    int[] newIndexPt = new int[2]; //index pair, (col, row) for going from float to gameBoard index
    int oldX;
    int oldY;
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
            if (action instanceof HiveSelectAction) { //if we were passed a request to select from board or hand
                HiveSelectAction select = (HiveSelectAction) action;

                if(select.getSelectedImageButton() != null){ //selecting from the player's hand //put this in the HiveHumanPlayer
                    hiveState.validMove(select.getSelectedTile()); //pass the newly created tile to calculate all possible moves
                }
                else{ //selecting from the game board
                    //get newX and newY and determine what tile they correspond to, then call gameState validMove on those
                    oldX = (int) select.getX();
                    oldY = (int) select.getY();
                    return hiveState.validMove(hiveState.getTile((int) select.getX(),(int) select.getY()));
                }

            } else if (action instanceof HiveMoveAction) { //we've been passed a request to move a piece
                HiveMoveAction move = (HiveMoveAction) action;

                if(move.getSelectedImageButton() != null){ //moving from hand to board
                    move.setSelectedImageButton(null); //reset the imageButton
                    return hiveState.makeMove(move.getCurrentTile(), (int) move.getX(), (int) move.getY());
                }
                else{ //moving from board spot to board spot
                    return hiveState.makeMove(hiveState.getTile(oldX, oldY), (int) move.getX(), (int) move.getY());
                }
            }
            // return true, indicating the it was a legal move
            return true;
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
