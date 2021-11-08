package com.example.hiveframework.hive.hiveActionMessage;

import android.widget.ImageButton;

import com.example.hiveframework.GameFramework.actionMessage.GameAction;
import com.example.hiveframework.GameFramework.players.GamePlayer;
import com.example.hiveframework.hive.Tile;

public class HiveSelectAction extends GameAction {

    //Tag for logging
    private static final String TAG = "HiveMoveAction";
    private static final long serialVersionUID = -2242980258970485343L;

    // instance variables: the selected row and column
    private float xCoord;
    private float yCoord;
    private int id;
    private Tile selectedTile = null; //the tile created because of selection or the tile selected
    private ImageButton selectedImageButton = null; //if null nothing selected, if not null this points to what is selected

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public HiveSelectAction(GamePlayer player) {
        super(player);
        id = -1;
    }

    /**
     * Constructor for HiveSelectAction
     * <p>
     * //@param source the player making the move
     *
     * @param xCoord x coordinate coming in from the onTouch
     * @param yCoord y coordinate coming in from the onTouch
     */
    public HiveSelectAction(GamePlayer player, float xCoord, float yCoord) {
        // invoke superclass constructor to set the player
        super(player);
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        id = -1; //Didn't select from player's hand
    }

    /**
     * Constructor for HiveSelectAction
     * <p>
     * //@param source the player making the move
     *
     * @param id the id number of the button pressed
     */
    public HiveSelectAction(GamePlayer player, int id) {
        // invoke superclass constructor to set the player
        super(player);
        this.id = id;
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

    /**
     * get the object's id that was selected
     *
     * @return the id of a selected image
     */
    public int getId() {
        return id;
    }

    public Tile getSelectedTile() {
        return selectedTile;
    }

    public void setSelectedTile(Tile selectedTile) {
        this.selectedTile = selectedTile;
    }

    public ImageButton getSelectedImageButton() {
        return selectedImageButton;
    }

    public void setSelectedImageButton(ImageButton selectedImageButton) {
        this.selectedImageButton = selectedImageButton;
    }
}
