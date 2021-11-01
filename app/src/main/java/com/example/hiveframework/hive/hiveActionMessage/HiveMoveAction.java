package com.example.hiveframework.hive.hiveActionMessage;

import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.GameFramework.players.GamePlayer;

public class HiveMoveAction extends GameAction {

    //Tag for logging
    private static final String TAG = "HiveMoveAction";
    private static final long serialVersionUID = -2242980258970485343L;

    // instance variables: the selected row and column
    private int row;
    private int col;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public HiveMoveAction(GamePlayer player) {
        super(player);
    }

    /**
     * Constructor for HiveMoveAction
     * <p>
     * //@param source the player making the move
     *
     * @param row the row of the square selected (0-2)
     * @param col the column of the square selected
     */
    public HiveMoveAction(GamePlayer player, int row, int col) {
        // invoke superclass constructor to set the player
        super(player);
    }

    /**
     * get the object's row
     *
     * @return the row selected
     */
    public int getRow() {
        return row;
    }

    /**
     * get the object's column
     *
     * @return the column selected
     */
    public int getCol() {
        return col;
    }

}
