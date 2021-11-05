package com.example.hiveframework.hive;
import android.widget.ImageButton;

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
    }

    /**
     * Constructor for the HiveLocalGame with loaded HiveState
     * @param hiveGameState
     */
    public HiveLocalGame(HiveGameState hiveGameState){
        super();
        super.state = new HiveGameState(hiveGameState);
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
        HiveHumanPlayer1 player;
        // get the 0/1 id of our player
        int playerId = getPlayerIdx(action.getPlayer());
        // get the 0/1 id of the player whose move it is
        int whoseMove = hiveState.getWhoseTurn();

        if(canMove(playerId)) {
            player = (HiveHumanPlayer1) action.getPlayer();
            if (action instanceof HiveSelectAction) { //if we were passed a request to select from board or hand
                HiveSelectAction select = (HiveSelectAction) action;

                if(player.getSelectedImageButton() != null){ //selecting from the player's hand
                    //loop through array list of resource id's, if it matches an id then call gameState validMove on it to select
                    //make a new tile using the selectedImageButton's id to figure out what type, the player index to figure out whom's, and set the indices to???
                    if(playerId == getPlayerIdx(players[0])){
                        piece = Tile.PlayerPiece.W;
                    }
                    else{
                        piece = Tile.PlayerPiece.B;
                    }
                    //pass in the buttonId, the Tile class figures out what type of Bug that is
                    currentTile = new Tile(-1,-1, piece, type, player.getSelectedImageButton().getId());
                }
                else{ //selecting from the game board
                    //get newX and newY and determine what tile they correspond to, then call gameState validMove on those
                    newIndexPt = player.getSurfaceView().mapPixelToSquare(((HiveMoveAction) action).getX(), ((HiveMoveAction) action).getY());
                    return hiveState.validMove(hiveState.getTile(newIndexPt[0], newIndexPt[1]));
                }

            } else if (action instanceof HiveMoveAction) { //we've been passed a request to move a piece
                HiveMoveAction move = (HiveMoveAction) action;

                if(player.getSelectedImageButton() != null){ //moving from hand to board
                    //access newX/Y for spot moving to
                    newIndexPt = player.getSurfaceView().mapPixelToSquare(((HiveMoveAction) action).getX(), ((HiveMoveAction) action).getY());
                    player.setSelectedImageButton(null); //reset the selected image since either nothing is going to happen or the pieces will move
                    return hiveState.makeMove(currentTile, newIndexPt[0], newIndexPt[1]);
                }
                else{ //moving from board spot to board spot
                    //access oldX/Y for spot moving from, newX/Y for spot moving to
                    //map onTouch data to gameBoard indices then make the swap
                    newIndexPt = player.getSurfaceView().mapPixelToSquare(((HiveMoveAction) action).getX(), ((HiveMoveAction) action).getY());
                    return hiveState.makeMove(currentTile, newIndexPt[0], newIndexPt[1]);
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

    /**
     *
     * @param imageId
     * @param player
     * @return
     */
    public Tile.Bug findBugType(int imageId, HiveHumanPlayer1 player){
        ArrayList <ImageButton> imagesArray = player.getImagesArray();

        switch(imageId){
            //case player.getActivity().findViewById(R.id.antP1Image).getId():

        }
        //for (ImageButton bug: imagesArray) {
            //if(bug.)
        return Tile.Bug.EMPTY; //if returns empty you know it didn't find any match
    }


}
