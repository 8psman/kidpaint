package com.namnv.project.kidpaint.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.namnv.project.kidpaint.task.FloodFillThread;
import com.namnv.project.kidpaint.ui.Painter;

/**
 * Created by 8psman on 10/28/2014.
 */
public class BucketTool extends PaintTool{

    public BucketTool(Painter painter){
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
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            PointF touch = painter.getRealPosition(motionEvent);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:

                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    final Point point = new Point((int)touch.x, (int)touch.y);
                    final Bitmap bitmap = painter.paintHolder.bitmap;
                    if (point.x >=0 && point.x < bitmap.getWidth() && point.y >=0 && point.y < bitmap.getHeight()){
                        // start flood fill

                        new FloodFillThread(null, new Runnable() {
                            @Override
                            public void run() {
                                painter.invalidate();
                                painter.isEdited = true;
                            }
                        },
                        bitmap, point, bitmap.getPixel(point.x, point.y), drawPaint.getColor()).run();
                    }

                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
            }

            return true;
        }
    };
}
