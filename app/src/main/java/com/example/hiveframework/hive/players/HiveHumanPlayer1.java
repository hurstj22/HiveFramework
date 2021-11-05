package com.example.hiveframework.hive.players;

import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.infoMessage.GameInfo;
import com.example.hiveframework.GameFramework.infoMessage.IllegalMoveInfo;
import com.example.hiveframework.GameFramework.infoMessage.NotYourTurnInfo;
import com.example.hiveframework.GameFramework.players.GameHumanPlayer;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.GameFramework.utilities.Logger;
import com.example.hiveframework.hive.HiveGameState;
import com.example.hiveframework.hive.HiveSurfaceView;
import com.example.hiveframework.hive.Tile;
import com.example.hiveframework.hive.hiveActionMessage.HiveMoveAction;
import com.example.hiveframework.hive.hiveActionMessage.HiveSelectAction;

import java.util.ArrayList;

import edu.up.cs301.game.R;

public class HiveHumanPlayer1 extends GameHumanPlayer implements View.OnTouchListener, View.OnClickListener {

    //Tag for logging
    private static final String TAG = "TTTHumanPlayer1";
    // the surface view
    private HiveSurfaceView surfaceView;
    private FrameLayout mainFrame; //the main frame which houses all the buttons on the sides
    // the ID for the layout to use
    private int layoutId;

    //These variables will reference widgets that will be modifed during play
    private TextView currentTurnTextView = null; //Who's turn it is
    private TextView playerOneTextView = null;
    private TextView playerTwoTextView = null;

    //TextViews that display how many of each bug a given player has
    private TextView beeP1Counter = null;
    private TextView spiderP1Counter = null;
    private TextView beetleP1Counter = null;
    private TextView antP1Counter = null;
    private TextView grasshopperP1Counter = null;
    private TextView beeP2Counter = null;

    private TextView spiderP2Counter = null;
    private TextView beetleP2Counter = null;
    private TextView antP2Counter = null;
    private TextView grasshopperP2Counter = null;

    //ImageButtons for each of the players bugs in their hand
    private ImageButton beeP1Image = null;
    private ImageButton spiderP1Image = null;
    private ImageButton beetleP1Image = null;
    private ImageButton antP1Image = null;
    private ImageButton grasshopperP1Image = null;
    private ImageButton beeP2Image = null;
    private ImageButton spiderP2Image = null;
    private ImageButton beetleP2Image = null;
    private ImageButton antP2Image = null;
    private ImageButton grasshopperP2Image = null;

    private Button playButton = null;
    private Button quitButton = null;
    private Button rulesButton = null;
    private Button endTurnButton = null;
    private Button undoButton = null;

    //the gameState and activity that we are working with when passed in
    private HiveGameState hiveGame;
    private GameMainActivity myActivity = null;
    private ArrayList<ImageButton> imagesArray;

    //human player needs to highlight what button was tapped
    //remember if a tap happened before
    private boolean hasTapped = false;
    private float inX = -1;
    private float inY = -1;
    private float newX = -1;
    private float newY = -1; //store the coordinates that the player wants to move to, used in the touch event
    private float oldX = -1;
    private float oldY = -1;
    private ImageButton selectedImageButton = null; //if null nothing selected, if not null this points to what is selected
    //array list of buttons to easily loop through and highlight the selected one

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
        imagesArray = new ArrayList<ImageButton>();
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
            //make a copy of the passed in gameState to pull data from
            hiveGame = new HiveGameState((HiveGameState) info);

            //change the color of the turn banner to the player's color then change the text
            if(this.playerNum == 0){ //first player
                currentTurnTextView.setTextColor(Color.RED);
            }
            else{
                currentTurnTextView.setTextColor(Color.BLUE);
            }
            currentTurnTextView.setText("" + this.allPlayerNames[playerNum] + "'s Turn");

            //update player 1's piece counters
            beeP1Counter.setText("" + hiveGame.getPiecesRemain()[0][0]);
            spiderP1Counter.setText("" + hiveGame.getPiecesRemain()[0][1]);
            beetleP1Counter.setText("" + hiveGame.getPiecesRemain()[0][2]);
            grasshopperP1Counter.setText("" + hiveGame.getPiecesRemain()[0][3]);
            antP1Counter.setText("" + hiveGame.getPiecesRemain()[0][4]);

            //update player 2's piece counters
            beeP2Counter.setText("" + hiveGame.getPiecesRemain()[1][0]);
            spiderP2Counter.setText("" + hiveGame.getPiecesRemain()[1][1]);
            beetleP2Counter.setText("" + hiveGame.getPiecesRemain()[1][2]);
            grasshopperP2Counter.setText("" + hiveGame.getPiecesRemain()[1][3]);
            antP2Counter.setText("" + hiveGame.getPiecesRemain()[1][4]);

            for (ImageButton bug: imagesArray) {
                if (bug != null && selectedImageButton != null) {
                    if (bug.getId() == selectedImageButton.getId()) {
                        bug.setBackgroundColor(Color.YELLOW); //highlight the background color to indicate selected tile
                    }
                    else{
                        bug.setBackgroundColor(Color.GRAY);
                    }
                }
            }

            surfaceView.setState(hiveGame);
            surfaceView.invalidate();
            Logger.log(TAG, "receiving");
        }
    }

    /**
     * sets the current player as the activity's GUI
     */
    public void setAsGui(GameMainActivity activity) {
        //remember the activity coming in
        myActivity = activity;

        // Load the layout resource for the new configuration
        activity.setContentView(layoutId);

        //Initialize the widget reference member variables declared at the top
        playButton = (Button)activity.findViewById(R.id.playButton);
        quitButton = (Button)activity.findViewById(R.id.quitButton);
        rulesButton = (Button)activity.findViewById(R.id.rulesButton);
        endTurnButton = (Button)activity.findViewById(R.id.endTurnButton);
        undoButton = (Button)activity.findViewById(R.id.undoButton);

        currentTurnTextView = (TextView)activity.findViewById(R.id.currentTurnTextView); //Who's turn it is
        playerOneTextView = (TextView)activity.findViewById(R.id.playerOneTextView);
        playerTwoTextView = (TextView)activity.findViewById(R.id.playerTwoTextView);

        beeP1Counter = (TextView)activity.findViewById(R.id.beeP1Counter);
        spiderP1Counter = (TextView)activity.findViewById(R.id.spiderP1Counter);
        beetleP1Counter = (TextView)activity.findViewById(R.id.beetleP1Counter);
        antP1Counter = (TextView)activity.findViewById(R.id.antP1Counter);
        grasshopperP1Counter = (TextView)activity.findViewById(R.id.grasshopperP1Counter);
        beeP2Counter = (TextView)activity.findViewById(R.id.beeP2Counter);
        spiderP2Counter = (TextView)activity.findViewById(R.id.spiderP2Counter);
        beetleP2Counter = (TextView)activity.findViewById(R.id.beetleP2Counter);
        antP2Counter = (TextView)activity.findViewById(R.id.antP2Counter);
        grasshopperP2Counter = (TextView)activity.findViewById(R.id.grasshopperP2Counter);

        //Player 1's imageButton initializations
        beeP1Image = (ImageButton)activity.findViewById(R.id.beeP1Image);
        beeP1Image.setImageResource(R.drawable.bensonbeehexcropped);
        spiderP1Image = (ImageButton)activity.findViewById(R.id.spiderP1Image);
        spiderP1Image.setImageResource(R.drawable.spiderhexcropped);
        beetleP1Image = (ImageButton)activity.findViewById(R.id.beetleP1Image);
        beetleP1Image.setImageResource(R.drawable.beetlehexcropped);
        antP1Image = (ImageButton)activity.findViewById(R.id.antP1Image);
        antP1Image.setImageResource(R.drawable.anthexcropped);
        grasshopperP1Image = (ImageButton)activity.findViewById(R.id.grasshopperP1Image);
        grasshopperP1Image.setImageResource(R.drawable.grasshopperhexcropped);

        //Player 2's imageButton initializations
        beeP2Image = (ImageButton)activity.findViewById(R.id.beeP2Image);
        beeP2Image.setImageResource(R.drawable.bensonbeehexcropped);
        spiderP2Image = (ImageButton)activity.findViewById(R.id.spiderP2Image);
        spiderP2Image.setImageResource(R.drawable.spiderhexcropped);
        beetleP2Image = (ImageButton)activity.findViewById(R.id.beetleP2Image);
        beetleP2Image.setImageResource(R.drawable.beetlehexcropped);
        antP2Image = (ImageButton)activity.findViewById(R.id.antP2Image);
        antP2Image.setImageResource(R.drawable.anthexcropped);
        grasshopperP2Image = (ImageButton)activity.findViewById(R.id.grasshopperP2Image);
        grasshopperP2Image.setImageResource(R.drawable.grasshopperhexcropped);

        // set the surfaceView instance variable
        surfaceView = (HiveSurfaceView)myActivity.findViewById(R.id.hiveSurfaceView);
        mainFrame = (FrameLayout)myActivity.findViewById(R.id.mainFrameLayout);
        Logger.log("set listener","OnTouch");
        surfaceView.setOnTouchListener(this);
        Logger.log("set listener","OnClick");
        mainFrame.setOnClickListener(this);

        //set the onClickListeners for all the buttons
        beeP1Image.setOnClickListener(this);
        spiderP1Image.setOnClickListener(this);
        beetleP1Image.setOnClickListener(this);
        antP1Image.setOnClickListener(this);
        grasshopperP1Image.setOnClickListener(this);
        beeP2Image.setOnClickListener(this);
        spiderP2Image.setOnClickListener(this);
        beetleP2Image.setOnClickListener(this);
        antP2Image.setOnClickListener(this);
        grasshopperP2Image.setOnClickListener(this);

        //add all buttons to the array to be able to iterate through them
        imagesArray.add(beeP1Image);
        imagesArray.add(spiderP1Image);
        imagesArray.add(beetleP1Image);
        imagesArray.add(antP1Image);
        imagesArray.add(grasshopperP1Image);
        imagesArray.add(beeP2Image);
        imagesArray.add(spiderP2Image);
        imagesArray.add(beetleP2Image);
        imagesArray.add(antP2Image);
        imagesArray.add(grasshopperP2Image);

        playButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);
        rulesButton.setOnClickListener(this);
        endTurnButton.setOnClickListener(this);
        undoButton.setOnClickListener(this);
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
        if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            inX = motionEvent.getX();
            inY = motionEvent.getY();

            //convert coordinates to position in gameBoard
            int[] gameBoardPosition = surfaceView.mapPixelToSquare(inX, inY);
            newX = gameBoardPosition[1];
            newY = gameBoardPosition[0]; 

            if(selectedImageButton != null){ //the player has selected one of the pieces to "place" on the board
                setSelectedImageButton(null); //reset the selected image since either nothing is going to happen or the pieces will move
                game.sendAction(new HiveMoveAction(this, newX, newY));
            }
            else if(!hasTapped){ //selecting from the board so pass a selectAction with the x and y coords
                oldX = newX;
                oldY = newY;
                hasTapped = !hasTapped;
                game.sendAction(new HiveSelectAction(this, oldX, oldY));
            }
            else if(hasTapped && oldX != -1){ //this is the second time tapping so you've selected a gameboard tile and now another gameboard tile
                game.sendAction(new HiveMoveAction(this, newX, newY));
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        //HiveMoveAction moveAction = new HiveMoveAction(this);
        HiveSelectAction selectAction = new HiveSelectAction(this);

        switch(view.getId()) {
            case R.id.playButton: //restarts the game with the same selected options
                myActivity.restartGame();
                break;
            case R.id.endTurnButton: //switches the current player's turn
                hiveGame.setWhoseTurn(1 - hiveGame.getWhoseTurn());
                break;
            case R.id.quitButton: //exits the game
                myActivity.finishAffinity();
                break;
            case R.id.rulesButton: //pull up the rules
                //yet to be coded
                break;
            case R.id.undoButton: //undo the last move by resetting the gameState to a previous one
                //yet to be coded
                break;
        }
        if(view instanceof ImageButton){
            //one of the player's bug pieces from their hand was selected, thus update the imageButton object
            selectedImageButton = (ImageButton) view; //local game can access the id and perform the appropriate actions
            game.sendAction(selectAction); //then pass a select action
        }

    }

    //setter and getters for instance variables
    public ImageButton getSelectedImageButton() {
        return selectedImageButton;
    }
    public void setSelectedImageButton(ImageButton selectedImageButton) {
        this.selectedImageButton = selectedImageButton;
    }

    public float getNewX() {
        return newX;
    }

    public float getNewY() {
        return newY;
    }

    public ArrayList<ImageButton> getImagesArray() {
        return imagesArray;
    }

    public HiveSurfaceView getSurfaceView() {
        return surfaceView;
    }

    /**
     *
     * @param imageId
     * @param player
     * @return
     */
    public Tile.Bug findBugType(int imageId, HiveHumanPlayer1 player) {
        ArrayList<ImageButton> imagesArray = player.getImagesArray();

        switch (imageId) {
            //case player.getActivity().findViewById(R.id.antP1Image).getId():
            //return Tile.Bug.ANT;
        }
        return Tile.Bug.EMPTY; //if returns empty you know it didn't find any match
    }

}
