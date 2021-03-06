package com.example.hiveframework.hive.hiveActionMessage;

import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.GameFramework.players.GamePlayer;
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
public class HiveMoveAction extends GameAction {

    //Tag for logging
    private static final String TAG = "HiveMoveAction";
    private static final long serialVersionUID = -2242980258970485343L;

    // instance variables: the selected row and column
    private float xCoord;
    private float yCoord;
    private Tile currentTile; //the tile being moved
    private ImageButton selectedImageButton = null; //if null nothing selected, if not null this points to what is selected
    private boolean computerMove;
    private ArrayList<Tile> computerPotentialMoves;

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
        computerMove = false;
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

    public Tile getCurrentTile() {
        return currentTile;
    }

    public void setCurrentTile(Tile currentTile) {
        this.currentTile = currentTile;
    }

    public ImageButton getSelectedImageButton() {
        return selectedImageButton;
    }

    public void setSelectedImageButton(ImageButton selectedImageButton) {
        this.selectedImageButton = selectedImageButton;
    }

    public boolean isComputerMove() {
        return computerMove;
    }

    public void setComputerMove(boolean computerMove){
        this.computerMove = computerMove;
    }

    public ArrayList<Tile> getComputerPotentialMoves() {
        return computerPotentialMoves;
    }

    public void setComputerPotentialMoves(ArrayList<Tile> computerPotentialMoves) {
        this.computerPotentialMoves = new ArrayList<Tile>();
        for(int i = 0; i < computerPotentialMoves.size(); i++){
            this.computerPotentialMoves.add(computerPotentialMoves.get(i)); //removes from the incoming array and adds it to the new one held here
        }
    }
}
