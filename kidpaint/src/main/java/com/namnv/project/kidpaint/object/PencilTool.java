package com.namnv.project.kidpaint.object;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.namnv.project.kidpaint.ui.Painter;

/**
 * Created by 8psman on 10/28/2014.
 */
public class PencilTool extends PaintTool{

    Path drawPath = new Path();

    Drawable icon;
    boolean isTouching;

    public PencilTool(Painter painter){
        super(painter);
        icon = painter.getContext().getResources().getDrawable(android.R.drawable.ic_menu_edit);
        isTouching = false;
    }

    @Override
    public View.OnTouchListener getDrawListener() {
        return pencilDrawListener;
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawPath(drawPath, drawPaint);

    }

    @Override
    public void drawIcon(Canvas canvas){
        if (isTouching){
            icon.setBounds( (int)touching.x, (int)touching.y - ICON_SIZE,
                            (int)touching.x - ICON_SIZE, (int)touching.y);
            icon.draw(canvas);
        }
    }

    PointF touching = new PointF();

    private final View.OnTouchListener pencilDrawListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            touching.x = motionEvent.getX();
            touching.y = motionEvent.getY();
            PointF touch = painter.getRealPosition(motionEvent);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isTouching = true;
                    drawPath.moveTo(touch.x, touch.y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touch.x, touch.y);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    drawPath.lineTo(touch.x, touch.y);
                    painter.paintHolder.canvas.drawPath(drawPath, drawPaint);
                    painter.isEdited = true;
                    drawPath.reset();
                    isTouching = false;
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    break;
            }
            painter.invalidate();
            return true;
        }
    };

    public static int getPencilSizeFromPosition(int position){
        return (position + 1) * 5;
    }

    public static int getAlphaFromPosition(int position){
        int number = 100 - 10 * position;
        float percent = number / 100f;
        int alpha  = (int)(percent * 255);
        return alpha;
    }
}
