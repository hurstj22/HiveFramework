package com.example.hiveframework.hive;
import com.example.hiveframework.GameFramework.infoMessage.GameState;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.LocalGame;
import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;

public class HiveLocalGame extends LocalGame {
    //Tag for logging
    private static final String TAG = "HiveLocalGame";

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
     * Constructor for the HiveLocalGame with loaded tttState
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

        // get the row and column position of the player's move
        HiveMoveAction tm = (HiveMoveAction) action;
        HiveGameState state = (HiveGameState) super.state;

        // get the 0/1 id of our player
        int playerId = getPlayerIdx(tm.getPlayer());

        // get the 0/1 id of the player whose move it is
        int whoseMove = state.getWhoseTurn();

        // make it the other player's turn
        state.setWhoseTurn(1 - whoseMove);

        // return true, indicating the it was a legal move
        return true;
    }

    //TESTING

    public int whoWon(){
        String gameOver = checkIfGameOver();
        if(gameOver == null || gameOver.equals("It's a cat's game.")) return -1; //if both queens are trapped
        if(gameOver.equals(playerNames[0]+" is the winner.")) return 0;
        return 1;
    }


}
