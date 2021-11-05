package com.example.hiveframework.hive.players;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.infoMessage.IllegalMoveInfo;
import com.example.hiveframework.GameFramework.infoMessage.NotYourTurnInfo;
import com.example.hiveframework.GameFramework.players.GameHumanPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.utilities.Logger;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.HiveSurfaceView;

import edu.up.cs301.game.R;

public class HiveHumanPlayer1 extends GameHumanPlayer implements View.OnTouchListener {

    //Tag for logging
    private static final String TAG = "TTTHumanPlayer1";

    // the surface view
    private HiveSurfaceView surfaceView;

    // the ID for the layout to use
    private int layoutId;

    /**
     * constructor
     *
     * @param name
     * 		the player's name
     * @param layoutId
     *      the id of the layout to use
     */
    public HiveHumanPlayer1(String name, int layoutId) {
        super(name);
        this.layoutId = layoutId;
    }

    /**
     * Callback method, called when player gets a message
     *
     * @param info
     * 		the message
     */
    @Override
    public void receiveInfo(GameInfo info) {

        if (surfaceView == null) return;

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            surfaceView.flash(Color.RED, 50);
        }
        else if (!(info instanceof HiveGameState))
            // if we do not have a HiveGameState, ignore
            return;
        else {
            surfaceView.setState((HiveGameState)info);
            surfaceView.invalidate();
            Logger.log(TAG, "receiving");
        }
    }


    /**
     * sets the current player as the activity's GUI
     */
    public void setAsGui(GameMainActivity activity) {

        // Load the layout resource for the new configuration
        activity.setContentView(layoutId);

        // set the surfaceView instance variable
        surfaceView = (HiveSurfaceView)myActivity.findViewById(R.id.hiveSurfaceView);
        Logger.log("set listener","OnTouch");
        surfaceView.setOnTouchListener(this);
    }

    /**
     * returns the GUI's top view
     *
     * @return
     * 		the GUI's top view
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.hiveSurfaceView);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
