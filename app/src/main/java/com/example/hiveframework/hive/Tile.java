package com.example.hiveframework.hive;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
/**
 * The tile class holds all information about a specific tile,
 * this is the base data structure for the entire game
 *  @author Isaac Reinhard
 *  @author Kelly Ngyuen
 *  @author Ali Sheehan
 *  @author James Hurst
 *  @version November 2021
 */
public class Tile {

    public enum Bug {
        EMPTY,
        QUEEN_BEE,
        BEETLE,
        ANT,
        GRASSHOPPER,
        SPIDER
    }

    public enum PlayerPiece {
        EMPTY,
        B,
        W
    }

    // Class Variables
    private Bug type;
    private Tile onTopOf;
    private PlayerPiece piece;
    private int indexX; //integer index in the arrayList of tiles
    private int indexY;
    private boolean visited; //variable for the DFS
    private int id; //holds image id

    //make helper method

    /**
     * Makes a default constructor for a tile
     * @param x
     * @param y
     */
    public Tile(int x, int y, PlayerPiece piece) {
        //default constructor could make a null tile
        type = Bug.EMPTY;
        onTopOf = null;
        indexX = x;
        indexY = y;
        this.piece = piece;
        visited = false;
        id = -1; //set the id in makeMove or some class related to the humanPlayer
    }

    /**
     * constructor to be used to create tiles during the select portion of makeMove in localGame
     * @param x the xIndex in the gameState
     * @param y the YIndex in the gameState
     * @param piece who this piece belongs to
     * @param type what type of Bug this piece is
     * @param id what imageResource this tile connects to
     */
    public Tile(int x, int y, PlayerPiece piece, Bug type, int id) {
        this.type = type;
        onTopOf = null;
        indexX = x; //will be -1 until gets onto the gameBoard
        indexY = y; //will be -1 ^
        this.piece = piece;
        visited = false;
        this.id = id;
    }

    /**
     * To be used in move to
     * make a piece have another beneath it.
     * @param bug which type of piece is getting copied
     * @param onTop
     */
    public Tile(Bug bug, Tile onTop, int x, int y, PlayerPiece pieceInstance) {
        type = bug;
        piece = pieceInstance;
        onTopOf = onTop;
        indexX = x;
        indexY = y;
        id = -1;
    }

    /**
     * Copy constructor
     * @param other
     * @return a new copied Tile object
     */
    public Tile(Tile other) {
        if (other == null) {

        } else {
            //Tile tile = new Tile(other.getIndexX(), other.getIndexY(), other.getPlayerPiece());
            this.indexX = other.getIndexX();
            this.indexY = other.getIndexY();
            this.piece = other.getPlayerPiece();
            this.onTopOf = new Tile(other.getOnTopOf());
            this.type = other.getType();
            this.visited = other.visited;
            this.id = other.id;
        }
    }

    /**
     * Sets bug type of Tile
     * @param bug - type of bug passed in
     */
    public void setType(Bug bug) {
        type = bug;
    }

    /**
     * Sets what tile is on top of a tile
     * @param tile - tile that is on top of other tile
     */
    public void setOnTopOf(Tile tile) {
        onTopOf = tile;
    }

    /**
     * Sets x index of tile in gameboard
     * @param x - incoming x
     */
    public void setIndexX(int x) {
        indexX = x;
    }

    /**
     * Sets x index of tile in gameboard
     * @param y - incoming y
     */
    public void setIndexY(int y ) {indexY = y; }

    /**
     * Returns x position of tile
     * @return
     */
    public int getIndexX() {
        return indexX;
    }

    /**
     * Returns y position of tile
     * @return
     */
    public int getIndexY() {
        return indexY;
    }

    /**
     * returns bug type of tile
     * @return
     */
    public Bug getType() {
        return type;
    }

    /**
     * returns tile that is on top of tile
     * @return
     */
    public Tile getOnTopOf() {
        return onTopOf;
    }

    /**
     * returns what player owns piece
     * @return
     */
    public PlayerPiece getPlayerPiece(){
        return piece;
    }

    /**
     * returns if tile has been visited for bfs
     * @return
     */
    public boolean getVisited() { return visited; }

    /**
     * sets if tile has been visited for bfs
     * @param v
     */
    public void setVisited(boolean v){
        visited = v;
    }

    /**
     * sets index of tile
     * @param idx - incoming index
     */
    public void setId(int idx){
        this.id = idx;
    }

    /**
     * Returns Id of tile 
     * @return
     */
    public int getId(){
        return id;
    }

    /**
     * Method for drawing hexagon.
     * @param mCanvas
     * @param x
     * @param y
     * @param radius
     * @param sides
     * @param startAngle
     * @param anticlockwise
     * @param paint
     */
    private void drawPolygon(Canvas mCanvas, float x, float y, float radius, float sides, float startAngle, boolean anticlockwise, Paint paint) {
        if (sides < 3) {
            return; }
        float a = ((float) Math.PI *2) / sides * (anticlockwise ? -1 : 1);
        mCanvas.save();
        mCanvas.translate(x, y);
        mCanvas.rotate(startAngle);
        Path path = new Path();
        path.moveTo(radius, 0);
        for(int i = 1; i < sides; i++) {
            path.lineTo(radius * (float) Math.cos(a * i), radius * (float) Math.sin(a * i));
        }
        path.close();
        mCanvas.drawPath(path, paint);
        mCanvas.restore();
    }

}
