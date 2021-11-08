package com.example.hiveframework.hive.players;

import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;

import java.util.Random;

/**
 * HiveComputerPlayer2 serves as our less dumb computer player class
 * it randomly picks a move to carry out that would bring it
 * closer to the other player's queen bee and tries to do it
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
