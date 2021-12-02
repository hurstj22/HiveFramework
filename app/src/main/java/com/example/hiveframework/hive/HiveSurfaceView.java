package com.example.hiveframework.hive;

import android.app.Activity;
import android.app.AlertDialog;
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

import java.util.ArrayList;

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

    private int radius = 70;
    private double halfWidth = Math.sqrt((radius*radius) - ((radius/2)*(radius/2)));
    private double gridWidth = halfWidth*2;
    private double c = Math.sqrt((radius*radius) - (halfWidth*halfWidth));
    private double gridHeight = (2*radius) - c;
    private double m = c / halfWidth;

    // the game's state
    protected HiveGameState state;
    private int potentialCounter;
    private ArrayList<Tile> potentialList;


    /**
     * Constructor for the HiveSurfaceView class.
     *
     * @param context - a reference to the activity this animation is run under
     */
    public HiveSurfaceView(Context context) {
        super(context);
    }

    /**
     * Contructor for surface view
     * @param context - a reference to the activity this animation is run under
     * @param attrs - attributes of surfaceView
     */
    public HiveSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        whitePaint = new Paint();
        whitePaint.setColor(Color.WHITE);
        whitePaint.setStyle(Paint.Style.STROKE);
        whitePaint.setStrokeWidth(5);

        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
        bluePaint.setStyle(Paint.Style.STROKE);
        bluePaint.setStrokeWidth(5);

        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.STROKE);
        redPaint.setStrokeWidth(5);

        yellowPaint = new Paint();
        yellowPaint.setColor(Color.YELLOW);
        yellowPaint.setStyle(Paint.Style.STROKE);
        yellowPaint.setStrokeWidth(5);
    }

    /**
     * Method used to draw hexagon on surfaceView
     * @param mCanvas - canvas tor draw on
     * @param x - xCord of hexagon
     * @param y - yCord of hexagon
     * @param radius - radius of hexagon
     * @param sides - how many sides, in our case 6
     * @param startAngle - where to start drawing
     * @param anticlockwise - to draw clockwise or counterclockwise
     * @param paint - paint to use
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

    /**
     * Method called that actually draws on the surface view
     * @param canvas - canvas of what we are drawing on
     */
    public void onDraw(Canvas canvas){
        //information used to draw hexagons correctly
        int startX = (int) halfWidth;
        int startY = 2*(int) c;
        int separation = (int) (2*halfWidth);
        int separationY = radius + (int) c;

        //draws the gameboard hexes
        if(state != null) {
            potentialCounter = 0;
            potentialList = new ArrayList<>(state.getPotentialMoves());
            for (int i = 0; i < state.getBoardSize(); i++) {
                for (int j = 0; j < state.getBoardSize(); j++) {
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
                            id = R.drawable.anthexnew;
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
                    else{
                        tileColor = whitePaint;
                    }
                    if (id != -1) { //draw a special tile
                        Bitmap image = BitmapFactory.decodeResource(getResources(), id); //create image using tile's id
                        Bitmap resizedImage = Bitmap.createScaledBitmap(image, 95, 95, true); //scales the image down to the right size

                        //Now draw all the tiles in the right spot, scaled
                        if (i % 2 == 0) { //even row
                            canvas.drawBitmap(resizedImage, 2 * startX + j * separation - 2*radius/3, startY + i * separationY - 2*radius/3, tileColor); //draw the image on the surface view in the correct location
                            drawPolygon(canvas, 2*(int) halfWidth + j * separation, startY + i * separationY, 90*radius/100, 6, 90, false, tileColor);
                        } else { //odd row
                            canvas.drawBitmap(resizedImage, startX + j * separation - 2*radius/3, startY + i * separationY - 2*radius/3, tileColor); //draw the image on the surface view in the correct location
                            drawPolygon(canvas, startX + j * separation, startY + i * separationY, 90*radius/100, 6, 90, false, tileColor);
                        }

                    }
                    if (i % 2 == 0) { //even row
                        drawPolygon(canvas, 2*(int) halfWidth + j * separation, startY + i * separationY, radius, 6, 90, false, whitePaint);
                    }
                    else { //odd row
                        drawPolygon(canvas, startX + j * separation, startY + i * separationY, radius, 6, 90, false, whitePaint);
                    }
                    //run through the potentialList and compare starting at the spot we left off on against i and j
                    if(potentialList != null && potentialCounter < potentialList.size()) {
                        for (int k = potentialCounter; k < potentialList.size(); k++) {
                            if (potentialList.get(k) != null) {
                                if (potentialList.get(k).getIndexX() == i && potentialList.get(k).getIndexY() == j) {
                                    tileColor = yellowPaint;
                                    if (i % 2 == 0) { //even row
                                        drawPolygon(canvas, 2 * (int) halfWidth + j * separation, startY + i * separationY, 80*radius/100, 6, 90, false, tileColor); //draw the yellow slightly smaller inside white tiles
                                        drawPolygon(canvas, 2 * (int) halfWidth + j * separation, startY + i * separationY, radius, 6, 90, false, whitePaint);
                                    } else { //odd row
                                        drawPolygon(canvas, startX + j * separation, startY + i * separationY, 80*radius/100, 6, 90, false, tileColor); //draw the yellow slightly smaller inside white tiles
                                        drawPolygon(canvas, startX + j * separation, startY + i * separationY, radius, 6, 90, false, whitePaint);
                                    }
                                }
                            }
                        }
                    }

                } //end of for loops
            }

            //draw the rules image if the user has clicked the button
            if(state.getRulesClicked()){
                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.rules); //create image using tile's id
                Bitmap resizedImage = Bitmap.createScaledBitmap(image, 1000, 800, true); //scales the image down to the right size

                canvas.drawBitmap(resizedImage, 2600, 2350, tileColor); //draw the rules image on the surface view
            }
        } //end of state if statement
    }

    /**
     * Maps where user clicks to row and column in gameBoard array
     * @param x - input x coordinate
     * @param y - input y coordinate
     * @return row, column
     */
    public int[] mapPixelToSquare(float x, float y) {

        // Find the row and column of the box that the point falls in.
        int row = (int) (y / gridHeight);
        int column;

        boolean rowIsOdd = row % 2 == 1;

        // Is the row an odd number?
        if (rowIsOdd) { // Yes: Offset x to match the indent of the row
            column = (int) (x / gridWidth);
        }
        else { // No: Calculate normally
            column = (int) ((x - halfWidth) / gridWidth);
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

    /**
     * Sets state to passed in state
     * @param state - incoming state
     */
    public void setState(HiveGameState state) {
        this.state = state;
    }

}