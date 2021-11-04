package com.example.hiveframework.hive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.hiveframework.GameFramework.GameMainActivity;
import com.example.hiveframework.GameFramework.utilities.FlashSurfaceView;
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
    private Activity myActivity;

    // the game's state
    protected HiveGameState state;

    /**
     * Constructor for the TTTSurfaceView class.
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
        int radius = 50;
        int separation = 2* radius + 5;
        //draws the gameboard hexes
        if(state != null) {
            for (int i = 0; i < state.getBoardSize(); i++) {
                for (int j = 0; j < state.getBoardSize() * 2; j++) {
                    if (i % 2 == 0) { //even row
                        drawPolygon(canvas, 2 * startX + j * separation, startY + i * separation, radius, 6, 90, false, whitePaint);
                    } else { //odd row
                        drawPolygon(canvas, startX + j * separation, startY + i * separation, radius, 6, 90, false, whitePaint);
                    }
                }
            }
        }

        //loop through all pieces in their locations and ask piece to draw itself
        //use the id from Tile to draw the appropriate bitmap
        for (int i = 0; i < state.getBoardSize(); i++) {
            for (int j = 0; j < state.getBoardSize() * 2; j++) {
                int id = state.getGameBoard().get(i).get(j).getId();
                if (id != -1) {

                    Bitmap map = BitmapFactory.decodeResource(myActivity.getResources(), id);
                    canvas.drawBitmap(map, map, startY + i * separation);
                }
            }
        }
    }

    public void setState(HiveGameState state){
            this.state = state;
        }
    }
}