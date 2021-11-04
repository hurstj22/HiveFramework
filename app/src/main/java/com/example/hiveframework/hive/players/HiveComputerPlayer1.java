package com.example.hiveframework.hive.players;

import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.players.GameComputerPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.HiveGameState;

import java.util.Random;

/**
 * Dumb ai class
 */
public class HiveComputerPlayer1 extends GameComputerPlayer {
    private HiveGameState hiveGame;
    private Random randPick = new Random();
    private float newX = -1;
    private float newY = -1; //store the coordinates that the player wants to move to, used in the touch event
    private ImageButton selectedImageButton = null; //if null nothing selected, if not null this points to what is selected
    //array list of buttons to easily loop through and highlight the selected one

    /**
     * constructor does nothing extra
     */
    public HiveComputerPlayer1(String name) {
        super(name);
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if(info == null){
            return;
        }
        if(info instanceof HiveGameState) {
            hiveGame = new HiveGameState((HiveGameState) info);
            if (this.playerNum != hiveGame.getWhoseTurn()) {
                return; //not you're turn
            }
        }
    }

    //setter and getters for instance variables
    public ImageButton getSelectedImageButton() {
        return selectedImageButton;
    }

    public float getNewX() {
        return newX;
    }

    public float getNewY() {
        return newY;
    }
}
