package com.namnv.project.kidpaint.object;

import android.graphics.Canvas;
import android.view.View;

import com.namnv.project.kidpaint.ui.Painter;

/**
 * Created by 8psman on 10/28/2014.
 */
public class HandTool extends PaintTool{

    public HandTool(Painter painter) {
        super(painter);
    }

    @Override
    public View.OnTouchListener getDrawListener() {
        return painter.zoomListener;
    }

    @Override
    public void draw(Canvas canvas) {

    }
}
