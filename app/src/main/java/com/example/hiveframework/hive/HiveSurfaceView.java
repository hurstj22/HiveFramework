package com.example.hiveframework.hive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.utilities.FlashSurfaceView;

import edu.up.cs301.game.R;

/**
 * this is the custom HiveSurface view which holds the display board
 *
 * @author Isaac Reinhard
 * @author Kelly Ngyuen
 * @author Ali Sheehan
 * @author James Hurst
 * @version October 2021
 */

public class HiveSurfaceView extends FlashSurfaceView {

    //the paints used in the UI
    private Paint whitePaint; //default empty tiles
    private Paint bluePaint; //player 2's border
    private Paint redPaint; //player 1's border
    private Paint yellowPaint; //potential movement's available
    private Paint tileColor;
    private Activity myActivity; //never being set :(
    int id; //id number of the resources to draw

    private int radius = 50;
    private double halfWidth = Math.sqrt((radius*radius) - ((radius/2)*(radius/2)));
    private double gridWidth = halfWidth*2;
    private double c = Math.sqrt((radius*radius) - (halfWidth*halfWidth));
    private double gridHeight = (2*radius) - c;
    private double m = c / halfWidth;

    // the game's state
    protected HiveGameState state;
    private int potentialCounter;
    /**
     * Constructor for the HiveSurfaceView class.
     *
     * @param context - a reference to the activity this animation is run under
     */
    public HiveSurfaceView(Context context) {
        super(context);
    }

    public HiveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.STROKE);
        whitePaint.setStrokeWidth(10);

        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setStrokeWidth(10);

        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(10);

        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStyle(Paint.Style.STROKE);
        yellowPaint.setStrokeWidth(10);
    }

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

    public void onDraw(Canvas canvas){
        int startX = 50;
        int startY = 100;
        int separation = 2*radius + 5;
        //draws the gameboard hexes
        if(state != null) {
            potentialCounter = 0;
            for (int i = 0; i < state.getBoardSize(); i++) {
                for (int j = 0; j < state.getBoardSize() * 2; j++) {
                    //loop through all pieces in their locations and ask piece to draw itself
                    //use the id to draw the appropriate bitmap
                    switch (state.getGameBoard().get(i).get(j).getType()) {
                        case EMPTY:
                            id = -1;
                            //drawEmpty(canvas, i, j);
                            break;
                        case QUEEN_BEE:
                            int testInt = 5;
                            id = R.drawable.bensonbeehexcropped;
                            //drawBee(canvas, i, j);
                            break;
                        case GRASSHOPPER:
                            id = R.drawable.grasshopperhexcropped;
                            //drawGrasshopper(i, j);
                            break;
                        case SPIDER:
                            id = R.drawable.spiderhexcropped;
                            //drawSpider(i, j);
                            break;
                        case BEETLE:
                            id = R.drawable.beetlehexcropped;
                            //drawBeetle(i, j);
                            break;
                        case ANT:
                            id = R.drawable.anthexcropped;
                            //drawAnt(i, j);
                            break;
                    }
                    //assign the right color to draw the border based on who owns the tile
                    if(state.getGameBoard().get(i).get(j).getPlayerPiece() == Tile.PlayerPiece.W){
                        tileColor = redPaint;
                    }
                    else if(state.getGameBoard().get(i).get(j).getPlayerPiece() == Tile.PlayerPiece.B){
                        tileColor = bluePaint;
                    }

                    if (id != -1) { //draw a special tile
                        Bitmap image = BitmapFactory.decodeResource(getResources(), id); //create image using tile's id
                        Bitmap resizedImage = Bitmap.createScaledBitmap(image, 90, 90, true); //scales the image down to the right size

                        //Now draw all the tiles in the right spot, scaled
                        if (i % 2 == 0) { //even row
                            canvas.drawBitmap(resizedImage, 2 * startX + j * separation - radius, startY + i * separation - radius, whitePaint); //draw the image on the surface view in the correct location
                            drawPolygon(canvas, 2 * startX + j * separation, startY + i * separation, radius, 6, 90, false, tileColor);
                        } else { //odd row
                            canvas.drawBitmap(resizedImage, startX + j * separation - radius, startY + i * separation - radius, whitePaint); //draw the image on the surface view in the correct location
                            drawPolygon(canvas, startX + j * separation, startY + i * separation, radius, 6, 90, false, tileColor);
                        }

                    }
                    //draw potentials in yellow outline from the potentialsArrayList
                    else if((state.getPotentialMoves() != null) && (potentialCounter < state.getPotentialMoves().size())) { //make sure there's no null pointer exceptions thrown
                            if (state.getPotentialMoves().get(potentialCounter) != null) {
                                Tile potentialTile = new Tile(state.getPotentialMoves().get(potentialCounter));
                                if (potentialTile.getIndexX() == i && potentialTile.getIndexY() == j) {
                                    potentialCounter++; //every iteration iterate counter that keeps track of where we are in the potentialArray
                                    tileColor = yellowPaint;
                                    if (i % 2 == 0) { //even row
                                        drawPolygon(canvas, 2 * startX + j * separation, startY + i * separation, radius, 6, 90, false, tileColor);
                                    } else { //odd row
                                        drawPolygon(canvas, startX + j * separation, startY + i * separation, radius, 6, 90, false, tileColor);
                                    }
                                }
                            }
                    }
                    else{ //draw an empty tile
                        if (i % 2 == 0) { //even row
                            drawPolygon(canvas, 2 * startX + j * separation, startY + i * separation, radius, 6, 90, false, whitePaint);
                        } else { //odd row
                            drawPolygon(canvas, startX + j * separation, startY + i * separation, radius, 6, 90, false, whitePaint);
                        }
                    }
                } //end of for loops
            }

        } //end of state if statement
        potentialCounter = 0;
    }

    /**
     * Maps where user clicks to row and column in gameBoard array
     * @param x - input x coordinate
     * @param y - input y coordinate
     * @return column, row
     */
    public int[] mapPixelToSquare(float x, float y) {

        // Find the row and column of the box that the point falls in.
        int row = (int) (y / gridHeight);
        int column;

        boolean rowIsOdd = row % 2 == 1;

        // Is the row an odd number?
        if (rowIsOdd) { // Yes: Offset x to match the indent of the row
            column = (int) ((x - halfWidth) / gridWidth);
        } else { // No: Calculate normally
            column = (int) (x / gridWidth);
        }

        // Work out the position of the point relative to the box it is in
        double relY = y - (row * gridHeight);
        double relX;

        if (rowIsOdd) {
            relX = (x - (column * gridWidth)) - halfWidth;
        } else {
            relX = x - (column * gridWidth);
        }

        // Work out if the point is above either of the hexagon's top edges
        if (relY < (-m * relX) + c){ // LEFT edge
            row--;
            if (!rowIsOdd) {
                column--;
            }
        }
        else if (relY < (m * relX) - c) { // RIGHT edge
            row--;
            if (rowIsOdd) {
                column++;
            }
        }

        int returnArray[] = new int[2];
        returnArray[0] = row;
        returnArray[1] = column;
        return returnArray;
    }

    public void setState(HiveGameState state) {
        this.state = state;
    }

}