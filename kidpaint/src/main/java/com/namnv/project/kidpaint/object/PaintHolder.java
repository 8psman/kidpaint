package com.namnv.project.kidpaint.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.namnv.project.kidpaint.Application;

import java.io.File;

/**
 * Created by 8psman on 11/1/2014.
 */
public class PaintHolder {

    public String name;
    public int background;
    public int width;
    public int height;
    public Bitmap bitmap;
    public Canvas canvas;
    public Paint paint;

    public String getFileName(){
        String fileName = name.toLowerCase();
        int index = name.lastIndexOf(".jpg");
        if (index < 0) index = name.lastIndexOf(".png");
        if (index < 0){
            fileName = fileName + Application.getInstance().getCurrentPaintExtension();
        }
        return fileName;
    }

    public String getFilePath(){
        String path = Application.getLocalPaintDirectory() + File.separator + getFileName();
        return path;
    }
}
