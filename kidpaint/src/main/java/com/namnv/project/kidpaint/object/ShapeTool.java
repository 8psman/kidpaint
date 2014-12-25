package com.namnv.project.kidpaint.object;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import com.namnv.project.kidpaint.ui.Painter;

/**
 * Created by 8psman on 10/28/2014.
 */
public class ShapeTool extends PaintTool{

    Path drawPath = new Path();

    Drawable icon;
    boolean isTouching;
    ShapeDrawer shapeDrawer;

    public ShapeTool(Painter painter){
        super(painter);
        icon = painter.getContext().getResources().getDrawable(android.R.drawable.ic_menu_edit);
        isTouching = false;
        shapeDrawer = lineShapeDrawer;
    }

    @Override
    public View.OnTouchListener getDrawListener() {
        return shapeDrawListener;
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
    PointF firstPoint = new PointF();
    PointF secondPoint = new PointF();
    private final View.OnTouchListener shapeDrawListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            touching.x = motionEvent.getX();
            touching.y = motionEvent.getY();
            PointF touch = painter.getRealPosition(motionEvent);
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isTouching = true;
                    firstPoint.set(touch);

//                    drawPath.moveTo(touch.x, touch.y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    secondPoint.set(touch);
                    shapeDrawer.drawShape(drawPath, firstPoint, secondPoint);
//                    drawPath.lineTo(touch.x, touch.y);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    secondPoint.set(touch);
                    shapeDrawer.drawShape(drawPath, firstPoint, secondPoint);
//                    drawPath.lineTo(touch.x, touch.y);
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

    public void setShapeDrawer(ShapeDrawer shapeDrawer){
        this.shapeDrawer = shapeDrawer;
    }

    public static int getPencilSizeFromPosition(int position){
        return (position + 1) * 5;
    }

    public static int getAlphaFromPosition(int position){
        int number = 100 - 10 * position;
        float percent = number / 100f;
        int alpha  = (int)(percent * 255);
        return alpha;
    }


    static public interface ShapeDrawer{
        void drawShape(Path path, PointF firstPoint, PointF secondPoint);
    }

    public static class LineShapeDrawer implements ShapeDrawer{
        @Override
        public void drawShape(Path path, PointF firstPoint, PointF secondPoint) {
            path.reset();
            path.moveTo(firstPoint.x, firstPoint.y);
            path.lineTo(secondPoint.x, secondPoint.y);
        }
    }

    public static class RectShapeDrawer implements ShapeDrawer{
        @Override
        public void drawShape(Path path, PointF firstPoint, PointF secondPoint) {
            path.reset();
            path.moveTo(firstPoint.x, firstPoint.y);
            path.lineTo(secondPoint.x, firstPoint.y);
            path.lineTo(secondPoint.x, secondPoint.y);
            path.lineTo(firstPoint.x, secondPoint.y);
            path.lineTo(firstPoint.x, firstPoint.y);
        }
    }

    public static class OvalShapeDrawer implements ShapeDrawer{
        @Override
        public void drawShape(Path path, PointF firstPoint, PointF secondPoint) {
            path.reset();
            path.addOval(new RectF(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y), Path.Direction.CW);
        }
    }

    public static class ThreeEdgeShapeDrawer implements ShapeDrawer{
        @Override
        public void drawShape(Path path, PointF firstPoint, PointF secondPoint) {
            path.reset();
            path.moveTo(firstPoint.x, secondPoint.y);
            path.lineTo((firstPoint.x + secondPoint.x) / 2, firstPoint.y);
            path.lineTo(secondPoint.x, secondPoint.y);
            path.lineTo(firstPoint.x, secondPoint.y);
        }
    }

    public static class FiveEdgeShapeDrawer implements ShapeDrawer{
        @Override
        public void drawShape(Path path, PointF firstPoint, PointF secondPoint) {
            path.reset();
            path.moveTo(firstPoint.x, secondPoint.y);
            path.lineTo(firstPoint.x, (firstPoint.y + secondPoint.y) / 2);
            path.lineTo((firstPoint.x + secondPoint.x)/2, firstPoint.y);
            path.lineTo(secondPoint.x, (firstPoint.y + secondPoint.y)/2);
            path.lineTo(secondPoint.x, secondPoint.y);
            path.lineTo(firstPoint.x, secondPoint.y);
        }
    }

    public static class SixEdgeShapeDrawer implements ShapeDrawer{
        @Override
        public void drawShape(Path path, PointF firstPoint, PointF secondPoint) {
            path.reset();
            path.moveTo(firstPoint.x, (firstPoint.y + secondPoint.y)/2);
            path.lineTo(firstPoint.x + (secondPoint.x - firstPoint.x)/3*1, firstPoint.y);
            path.lineTo(firstPoint.x + (secondPoint.x - firstPoint.x)/3*2, firstPoint.y);
            path.lineTo(secondPoint.x, (firstPoint.y + secondPoint.y) / 2);
            path.lineTo(firstPoint.x + (secondPoint.x - firstPoint.x)/3*2, secondPoint.y);
            path.lineTo(firstPoint.x + (secondPoint.x - firstPoint.x)/3*1, secondPoint.y);
            path.lineTo(firstPoint.x, (firstPoint.y + secondPoint.y)/2);
        }
    }

    public static OvalShapeDrawer ovalShapeDrawer = new OvalShapeDrawer();
    public static RectShapeDrawer rectShapeDrawer = new RectShapeDrawer();
    public static LineShapeDrawer lineShapeDrawer = new LineShapeDrawer();
    public static ThreeEdgeShapeDrawer threeEdgeShapeDrawer = new ThreeEdgeShapeDrawer();
    public static FiveEdgeShapeDrawer fiveEdgeShapeDrawer = new FiveEdgeShapeDrawer();
    public static SixEdgeShapeDrawer sixEdgeShapeDrawer = new SixEdgeShapeDrawer();
}

