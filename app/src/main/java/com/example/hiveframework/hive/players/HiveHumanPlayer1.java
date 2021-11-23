package com.example.hiveframework.hive.players;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.actionMessage.EndTurnAction;
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
import com.example.hiveframework.hive.hiveActionMessage.HiveUndoTurnAction;

import java.util.ArrayList;

import edu.up.cs301.game.R;

/**
 * This class holds all information for a human player
 * HiveHumanPlayer1 also displays the GUI and controls click/touch events
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 */
public class HiveHumanPlayer1 extends GameHumanPlayer implements View.OnTouchListener, View.OnClickListener {

    //Tag for logging
    private static final String TAG = "HiveHumanPlayer1";
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
    private ImageView rulesButton = null;
    private Button endTurnButton = null;
    private Button undoButton = null;

    //the gameState and activity that we are working with when passed in
    private HiveGameState hiveGame;
    private GameMainActivity myActivity = null;
    private ArrayList<ImageButton> imagesArray;
    private Tile currentTile = null;
    private Tile.PlayerPiece piece;

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
    HiveSelectAction selectActionFromHand = null;
    HiveUndoTurnAction undoTurnAction = null;

    //id numbers to hold the id nums of all images
    int beeId;
    int spiderId;
    int grasshopperId;
    int beetleId;
    int antId;

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
     * @param info the message
     */
    @Override
    public void receiveInfo(GameInfo info) {

        if (surfaceView == null) return;

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            this.flash(Color.RED, 500);
            selectedImageButton = null; //reset the selected image pointer if an illegal move was passed
        }
        else if (!(info instanceof HiveGameState))
            // if we do not have a HiveGameState, ignore
            return;
        else {
            //make a copy of the passed in gameState to pull data from
            hiveGame = new HiveGameState((HiveGameState) info);
            //Code for undoing a move, DOESN'T WORK YET
//            if(hiveGame.getUndoTurn() != null) {
//                HiveGameState undo = new HiveGameState(hiveGame.getUndoTurn());
//                undoTurnAction = new HiveUndoTurnAction(this, undo);
//            }

            //change the color of the turn banner to the player's color then change the text
            if(hiveGame.getWhoseTurn() == 0){ //first player
                currentTurnTextView.setTextColor(Color.RED);
            }
            else{
                currentTurnTextView.setTextColor(Color.BLUE);
            }
            currentTurnTextView.setText("" + this.allPlayerNames[hiveGame.getWhoseTurn()] + "'s Turn");

            //set placedPiece to false
            if(hiveGame.getPlacedPiece()){
                hiveGame.setSelectFlag(false);
            }
            hiveGame.setPlacedPiece(false);
            //Update the undoTurn variable in HiveGameState
            hiveGame.setUndoTurn(hiveGame);

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
                if (bug != null && selectedImageButton != null && selectActionFromHand != null) {
                    if (bug.getId() == selectedImageButton.getId() && hiveGame.getCurrentIdSelected() > 0 && hiveGame.getSelectFlag()) {
                        bug.setBackgroundColor(Color.GREEN); //highlight the background color to indicate selected tile
                    }
                    else{
                        bug.setBackgroundColor(Color.GRAY);
                    }
                }
            }
            hiveGame.setSelectFlag(false);
            surfaceView.setState(hiveGame); //update the surfaceView
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
        antP1Image.setImageResource(R.drawable.anthexnew);
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
        antP2Image.setImageResource(R.drawable.anthexnew);
        grasshopperP2Image = (ImageButton)activity.findViewById(R.id.grasshopperP2Image);
        grasshopperP2Image.setImageResource(R.drawable.grasshopperhexcropped);

        //rules display
        rulesButton = (ImageView)activity.findViewById(R.id.rulesButton);

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
     * @return the GUI's top view
     */
    @Override
    public View getTopView() {
        return myActivity.findViewById(R.id.hiveSurfaceView);
    }

    @Override
    /**
     * onTouch handles the surfaceView touches and maps the touch
     * to a corresponding index in the gameState gameBoard
     * sends move actions to place pieces or move them on the board
     */
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
            inX = motionEvent.getX();
            inY = motionEvent.getY();

            //convert coordinates to position in gameBoard
            int[] gameBoardPosition = surfaceView.mapPixelToSquare(inX, inY);
            newX = gameBoardPosition[0];
            newY = gameBoardPosition[1];

            if(selectedImageButton != null){ //the player has selected one of the pieces to "place" on the board
                HiveMoveAction moveActionFromHand = new HiveMoveAction(this, newX, newY);
                moveActionFromHand.setSelectedImageButton(selectedImageButton);
                selectedImageButton = null;
                //not sure if I can reset it since possibly it's just a pointer that gets assigned in moveAction... not sure about this one
                moveActionFromHand.setCurrentTile(currentTile); //set the tile that the action is working on moving
                game.sendAction(moveActionFromHand);
                return true;
            }
            else if(!hasTapped && selectedImageButton == null){ //selecting from the board so pass a selectAction with the x and y coords
                oldX = newX;
                oldY = newY;
                hasTapped = !hasTapped;
                game.sendAction(new HiveSelectAction(this, oldX, oldY));
                return true;
            }
            else if(hasTapped && oldX != -1){ //this is the second time tapping so you've selected a gameboard tile and now another gameboard tile
                hasTapped = !hasTapped; //reset hasTapped
                game.sendAction(new HiveMoveAction(this, newX, newY));
                return true;
            }
        }
        return false;
    }

    @Override
    /**
     * OnClick listener functionality
     * sends the appropriate action to the game framework
     * if the user clicks a button or imageButton for placing a piece
     * from the user's hand
     */
    public void onClick(View view) {
        EndTurnAction endTurnAction = new EndTurnAction(this);

        switch(view.getId()) {
            case R.id.playButton: //restarts the game with the same selected options
                myActivity.restartGame();
                break;
            case R.id.endTurnButton: //switches the current player's turn
                Log.d(TAG, "turn is" + hiveGame.getWhoseTurn());
                game.sendAction(endTurnAction);
                Log.d(TAG, "onClick: end turn action");
                break;
            case R.id.quitButton: //exits the game
                myActivity.finishAffinity();
                break;
            case R.id.rulesButton: //pull up the rules
                //yet to be coded
                break;
            case R.id.undoButton: //undo the last move by resetting the gameState to a previous one
                game.sendAction(undoTurnAction);
                //yet to be coded
                break;
        }
        if(view instanceof ImageButton){
            //one of the player's bug pieces from their hand was selected, thus update the imageButton object
            selectedImageButton = (ImageButton) view; //local game can access the id and perform the appropriate actions
            selectActionFromHand = new HiveSelectAction(this, selectedImageButton.getId()); //set up the action
            selectActionFromHand.setSelectedImageButton(selectedImageButton);

            //create the tile to pass in as selected tile with starting coords as -1 since it belongs in the player's hand
            if(hiveGame.getWhoseTurn() == 0){
                piece = Tile.PlayerPiece.W; //1st player
            }
            else{
                piece = Tile.PlayerPiece.B; //2nd player
            }
            currentTile = new Tile(-1,-1, piece, findBugType(selectedImageButton.getId()), selectedImageButton.getId()); //input -1's to know tile coming from board,
                                                                                                                                        // type is determined by function, and id is set from imageButton
            selectActionFromHand.setSelectedTile(currentTile); //assign the selected tile in the select action class to be referenced later
            //hiveGame.setSelectFlag(true); //for highlighting the background yellow SETTING THINGS IN THE hiveGame here DOESN'T WORK
            game.sendAction(selectActionFromHand); //then pass a select action
        }

    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    @Override
    protected void initAfterReady() {
        beeId = beeP1Image.getId();
        beetleId = beetleP1Image.getId();
        spiderId = spiderP1Image.getId();
        grasshopperId = grasshopperP1Image.getId();
        antId = antP1Image.getId();
    }

    /**
     *
     * @param imageId the Id of an ImageButton corresponding to a bug type
     * @return a type of Bug to be used to create a new Tile
     */
    public Tile.Bug findBugType(int imageId) {
        int check = 0; //break spot for debugging
        if(imageId == beeId) {
            return Tile.Bug.QUEEN_BEE;
        }
        else if(imageId == spiderId){
            return Tile.Bug.SPIDER;
        }
        else if(imageId == beetleId){
            return Tile.Bug.BEETLE;
        }
        else if(imageId == grasshopperId){
            return Tile.Bug.GRASSHOPPER;
        }
        else if(imageId == antId){
            return Tile.Bug.ANT;
        }
        return Tile.Bug.EMPTY; //if made it here returns empty you know it didn't find any match
    }

}
