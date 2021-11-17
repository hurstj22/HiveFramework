package com.example.hiveframework.hive.hiveActionMessage;
import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.Tile;

import java.util.ArrayList;

/**
 * This class is sent when the player or computer wants to move a piece
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 */
public class HiveUndoTurnAction extends GameAction {
    HiveGameState startTurn;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public HiveUndoTurnAction(GamePlayer player, HiveGameState turn) {

        super(player);

        startTurn = new HiveGameState(turn);
    }

    public HiveGameState getStartTurn() {return startTurn;}


}
