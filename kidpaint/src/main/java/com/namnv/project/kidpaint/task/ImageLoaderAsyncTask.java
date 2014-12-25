package com.namnv.project.kidpaint.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.namnv.project.kidpaint.object.PaintReference;

/**
 * Created by 8psman on 10/27/2014.
 */
public class ImageLoaderAsyncTask extends AsyncTask<Void, Void, Bitmap>{

    PaintReference paintRef;
    OnImageLoadingResultListener listener;
    public ImageLoaderAsyncTask(PaintReference paintRef, OnImageLoadingResultListener listener){
        this.paintRef = paintRef;
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        String path = paintRef.path;
        String prefix = "file://";
        if (path.startsWith(prefix))
            path = path.substring(prefix.length());
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inMutable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, opt);
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        listener.onResult(bitmap);
    }

    public interface OnImageLoadingResultListener{
        void onResult(Bitmap bitmap);
    }

}
