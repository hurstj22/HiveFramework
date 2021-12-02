package com.example.hiveframework.hive.hiveActionMessage;

import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.Tile;

import java.util.ArrayList;

/**
 * This class is sent when the player or computer wants to pull up the rules
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 */
public class HiveRulesAction extends GameAction {

    //Tag for logging
    private static final String TAG = "HiveRulesAction";
    private static final long serialVersionUID = -2242980258970485343L;

    /**
     * constructor for HiveRulesAction
     *
     * @param player the player who created the action
     */
    public HiveRulesAction(GamePlayer player) {
        super(player);
    }


}
