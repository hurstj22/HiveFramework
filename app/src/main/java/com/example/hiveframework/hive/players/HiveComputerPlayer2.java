package com.example.hiveframework.hive.players;

import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;

import java.util.Random;

/**
 * Less dumb ai class
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
