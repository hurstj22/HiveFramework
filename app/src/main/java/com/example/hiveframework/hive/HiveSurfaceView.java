package com.example.hiveframework.hive;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.SurfaceView;

import com.example.hiveframework.GameFramework.utilities.FlashSurfaceView;

public class HiveSurfaceView extends FlashSurfaceView {

    private Paint redPaint;
    private Paint bluePaint;

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
        redPaint = new Paint();
        redPaint.setColor(Color.RED);
        bluePaint = new Paint();
        bluePaint.setColor(Color.BLUE);
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
        drawPolygon(canvas, 330, 330, 40, 6, 0, false, redPaint);
        drawPolygon(canvas, 390, 300, 40, 6, 0, false, bluePaint);

    }

    public void setState(HiveGameState state) {
        this.state = state;
    }
}