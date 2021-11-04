package com.example.hiveframework.hive.hiveActionMessage;

import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.GameFramework.players.GamePlayer;

public class HiveMoveAction extends GameAction {

    //Tag for logging
    private static final String TAG = "HiveMoveAction";
    private static final long serialVersionUID = -2242980258970485343L;

    // instance variables: the selected row and column
    private float xCoord;
    private float yCoord;

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
     * @param xCoord x coordinate coming in from the onTouch
     * @param yCoord y coordinate coming in from the onTouch
     */
    public HiveMoveAction(GamePlayer player, float xCoord, float yCoord) {
        // invoke superclass constructor to set the player
        super(player);
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    /**
     * get the object's x
     *
     * @return the row selected
     */
    public float getX() {
        return xCoord;
    }

    /**
     * get the object's y
     *
     * @return the column selected
     */
    public float getY() {
        return yCoord;
    }

}
