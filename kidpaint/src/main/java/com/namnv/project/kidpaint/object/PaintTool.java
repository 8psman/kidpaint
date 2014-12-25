package com.namnv.project.kidpaint.object;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.namnv.project.kidpaint.ui.Painter;

/**
 * Created by 8psman on 10/28/2014.
 */
public abstract class PaintTool{

    protected static final int ICON_SIZE = 40;

    Painter painter;

    Paint drawPaint = getDefaultPaint();

    public static Paint getDefaultPaint(){
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5f);

        paint.setAntiAlias(true);
        paint.setStrokeWidth(50);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        return paint;
    }

    public PaintTool(Painter painter){
        this.painter = painter;
    }

    public abstract View.OnTouchListener getDrawListener();

    public abstract void draw(Canvas canvas);

    public void drawIcon(Canvas canvas){

    }

    public Paint getPaint(){
        return drawPaint;
    }
}
