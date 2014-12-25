package com.namnv.project.kidpaint.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.namnv.project.kidpaint.Application;
import com.namnv.project.kidpaint.object.PaintHolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by 8psman on 10/27/2014.
 */
public class PaintSavingAsyncTask extends AsyncTask<Void, Void, Boolean>{

    PaintHolder holder;
    OnSavingPaintResult resultListener;

    public PaintSavingAsyncTask(PaintHolder holder, OnSavingPaintResult resultListener){
        this.holder = holder;
        this.resultListener = resultListener;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        String directory = Application.getLocalPaintDirectory();
        String fileName = holder.name.toLowerCase();
        int index = holder.name.lastIndexOf(".jpg");
        if (index < 0) index = holder.name.lastIndexOf(".png");
        if (index < 0){
            fileName = fileName + Application.getInstance().getCurrentPaintExtension();
        }
        File file = new File(directory, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            holder.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean){
            resultListener.onSuccess();
        }else{
            resultListener.onError();
        }
    }

    public interface OnSavingPaintResult{
        void onSuccess();
        void onError();
    }
}
