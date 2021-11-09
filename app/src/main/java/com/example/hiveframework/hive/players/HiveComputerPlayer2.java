package com.example.hiveframework.hive.players;

import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;

import java.util.Random;

/**
 * HiveComputerPlayer2 serves as our dumb computer player class
 * it randomly picks a move to carry out
 *
 * @author Isaac Reinhard
 * @author Kelly Ngyuen
 * @author Ali Sheehan
 * @author James Hurst
 * @version November 2021
 *
 */
public class HiveComputerPlayer2 extends GameComputerPlayer {
    private HiveGameState hiveGame;
    private Random randPick = new Random();

    /**
     * constructor does nothing extra
     */
    public HiveComputerPlayer2(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {

    }
}
