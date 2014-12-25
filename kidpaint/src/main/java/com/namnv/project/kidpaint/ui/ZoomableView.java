package com.namnv.project.kidpaint.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.support.v4.view.ScaleGestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class ZoomableView extends View{

	private final float MIN_ZOOM = 0.1f;
	private final float MAX_ZOOM = 5f;
	
	private final int MODE_NONE = 0;
	private final int MODE_DRAG = 1;
	private final int MODE_ZOOM	= 2;
	
	//-- for scaling and panning
	ScaleGestureDetector scaleDetector;
	public float scaleFactor = 1f;
	int mode;
	
	float startX = 0f;
	float startY = 0f;
	
	public float translateX;
    public float translateY;

	// - end for scaling and panning

	float viewWidth;
    float viewHeight;

	public ZoomableView(Context context){
		super(context);
		initialize();
	}
	
	public ZoomableView(Context context, AttributeSet attr){
		super(context, attr);
		initialize();
	}

    public ZoomableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();


    }


    public void initialize(){
        scaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
		ScaleGestureDetectorCompat.setQuickScaleEnabled(scaleDetector, true);

	}

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        viewWidth = xNew;
        viewHeight = yNew;
        translateX = viewWidth/2 - paintWidth/2;
        translateY = viewHeight/2 - paintHeight/2;
        pivot_x = 0;
        pivot_y = 0;
    }

    float paintWidth = 500;
    float paintHeight = 500;

    float pivot_x;
    float pivot_y;
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);

		canvas.scale(scaleFactor, scaleFactor, pivot_x, pivot_y);

		canvas.translate(translateX / scaleFactor, translateY / scaleFactor);
//        canvas.translate(getWidth()/2 - 500/2, getHeight()/2 - 500/2);
	}
	
	class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
		@Override
        public boolean onScale(ScaleGestureDetector detector) {
			scaleFactor *= detector.getScaleFactor();
			scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));

            return true;
        }
	}

	boolean checkUp;
	int startP;

    public OnTouchListener zoomListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    //The first finger has been pressed. The only action that the user can take now is to pan/drag so let's
                    //set the mode to DRAG
                    mode = MODE_DRAG;
                    startX = event.getX();
                    startY = event.getY();
                    startP = event.getPointerId(0);
                    checkUp = true;
                    break;

                case MotionEvent.ACTION_MOVE:
                    //We don't need to set the mode at this point because the mode is already set to DRAG
                    if (mode != MODE_NONE){
                        if (mode == MODE_DRAG){
                            translateX += event.getX() - startX ;
                            translateY += event.getY() - startY ;
                        }
                        startX = event.getX();
                        startY = event.getY();
                    }

                    Log.d("EIGHTPSMAN", "Action moving: " + event.getPointerCount());
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    //The second finger has been placed on the screen and so we need to set the mode to ZOOM
                    mode = MODE_ZOOM;

                    Log.d("EIGHTPSMAN", "Action pointer down");
                    break;

                case MotionEvent.ACTION_UP:
                    //All fingers are off the screen and so we're neither dragging nor zooming.
                    mode = MODE_NONE;

                    Log.d("EIGHTPSMAN", "Action up: " + event.getPointerCount());
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    //The second finger is off the screen and so we're back to dragging.
                    mode = MODE_NONE;

                    Log.d("EIGHTPSMAN", "Action pointer up: " + event.getPointerCount());
                    break;

            }

            scaleDetector.onTouchEvent(event);
            invalidate();

            return true;
        }
    };

    public PointF getRealPosition(MotionEvent event){
        PointF point = new PointF();
        point.set(
                (event.getX() - translateX) / scaleFactor,
                (event.getY() - translateY) / scaleFactor
        );
        return point;
    }

    public float getRealX(float x){
        return (x - translateX) / scaleFactor;
    }

    public float getRealY(float y){
        return (y - translateY) / scaleFactor;
    }

}
