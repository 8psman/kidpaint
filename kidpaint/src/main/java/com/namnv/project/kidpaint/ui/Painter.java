package com.namnv.project.kidpaint.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.namnv.project.kidpaint.Application;
import com.namnv.project.kidpaint.Gallery;
import com.namnv.project.kidpaint.New;
import com.namnv.project.kidpaint.object.PaintHolder;
import com.namnv.project.kidpaint.object.PaintTool;
import com.namnv.project.kidpaint.task.PaintSavingAsyncTask;

/**
 * Created by 8psman on 10/28/2014.
 */
public class Painter extends View {

    public static final String KEY_PAINT_INFO = "paint_info";

    public PaintHolder paintHolder;
    Paint paint = new Paint();
    ZoomUtil zoomUtil;

    public boolean isEdited;

    public void initializePainter(){
        paintHolder = (PaintHolder) Application.getInstance().getTempObject(KEY_PAINT_INFO);
        // new paint
        if (paintHolder.bitmap == null){
            paintHolder.bitmap = Bitmap.createBitmap(paintHolder.width, paintHolder.height, Bitmap.Config.ARGB_8888);
            paintHolder.canvas = new Canvas(paintHolder.bitmap);
            paintHolder.canvas.drawColor(paintHolder.background);
        }else{
            paintHolder.canvas = new Canvas(paintHolder.bitmap);
        }

        paintHolder.paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        zoomUtil = new ZoomUtil();

        isEdited = false;

        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        canvas.concat(zoomUtil.getMatrix());

        canvas.drawBitmap(paintHolder.bitmap, 0, 0, paintHolder.paint);
        activeTool.draw(canvas);

        canvas.restore();

        activeTool.drawIcon(canvas);
    }

    private void onNewPaint(){
        Activity activity = (Activity) getContext();
        Intent intent = new Intent(activity, New.class);
        activity.startActivityForResult(intent, Gallery.NEW_INTENT_CODE);
    }

    public void savePaint(){
        final AlertDialog alert = DialogFactory.createLoadingDialog(getContext(), "Saving ...");
        new PaintSavingAsyncTask(paintHolder, new PaintSavingAsyncTask.OnSavingPaintResult() {
            @Override
            public void onSuccess() {
                alert.dismiss();
                isEdited = false;
                Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                AlertDialog savedDialog = DialogFactory.createSavedDialog(getContext(),
                // on back home
                new Runnable() {
                    @Override
                    public void run() {
                        ((Activity)getContext()).finish();
                    }
                },
                // on share
                new Runnable() {
                    @Override
                    public void run() {
                        ((com.namnv.project.kidpaint.Paint)getContext()).onSharePaint(paintHolder.getFilePath());
                    }
                },
                // on new paint
                new Runnable() {
                    @Override
                    public void run() {
                        onNewPaint();
                    }
                });
                savedDialog.show();
            }

            @Override
            public void onError() {
                alert.dismiss();
                DialogFactory.createMessageDialog(getContext(), "Error on saving paint!").show();
            }
        }).execute();
    }

    public void cancelPaint(){
        AlertDialog cancelDialog = DialogFactory.createCancelDialog(getContext(), isEdited,
                // on home
            new Runnable() {
                @Override
                public void run() {
                    ((Activity)getContext()).finish();
                }
            },
                // on new
            new Runnable() {
                @Override
                public void run() {
                    onNewPaint();
                }
        });
        cancelDialog.show();
    }

    public void clearPaint(){
        DialogFactory.createRequestDialog(getContext(), "Clear", "Do you want to clear?", new Runnable() {
            @Override
            public void run() {
                paintHolder.canvas.drawColor(paintHolder.background);
                invalidate();
            }
        }).show();
    }

    /**
     * start of paint tool's area ------------------------------------------------------------------
     */
    PaintTool activeTool;
    public void setActiveTool(PaintTool activeTool){
        this.activeTool = activeTool;
        setOnTouchListener(activeTool.getDrawListener());
    }
    /**
     * end of paint tool's area --------------------------------------------------------------------
     */

    public Painter(Context context) {
        super(context);
    }

    public Painter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Painter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);
    }

    public OnTouchListener zoomListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            zoomUtil.onTouch(view, event);
            invalidate();
            return true;
        }
    };

    public PointF getRealPosition(MotionEvent event){
        return zoomUtil.getRealPosition(event);
    }
}
