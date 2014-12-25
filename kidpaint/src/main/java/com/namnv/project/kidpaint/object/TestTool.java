package com.namnv.project.kidpaint.object;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.namnv.project.kidpaint.ui.Painter;

/**
 * Created by 8psman on 10/28/2014.
 */
public class TestTool extends PaintTool{


    public TestTool(Painter painter){
        super(painter);

    }

    @Override
    public View.OnTouchListener getDrawListener() {
        return pencilDrawListener;
    }

    @Override
    public void draw(Canvas canvas) {

    }

    private final View.OnTouchListener pencilDrawListener = new View.OnTouchListener() {
        float last_x;
        float last_y;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    last_x = motionEvent.getX();
                    last_y = motionEvent.getY();
//                    Log.d(Application.TAG, last_x + " : " + last_y + ", " + painter.translateX + ":" + painter.translateY + ", " + painter.scaleFactor);
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:

                    break;

                case MotionEvent.ACTION_UP:

                    break;

                case MotionEvent.ACTION_POINTER_UP:

                    break;
            }
            return true;
        }
    };
}
