package com.namnv.project.kidpaint.task;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.namnv.project.kidpaint.Application;
import com.namnv.project.kidpaint.adapter.GalleryGridAdapter;
import com.namnv.project.kidpaint.object.PaintReference;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 8psman on 10/27/2014.
 */
public class PaintListLoaderAsyncTask extends AsyncTask<Void, Void, List<PaintReference>>{

    GalleryGridAdapter adapter;
    Runnable onFinish;
    public PaintListLoaderAsyncTask(GalleryGridAdapter adapter, Runnable onFinish){
        this.adapter = adapter;
        this.onFinish = onFinish;
    }
    @Override
    protected List<PaintReference> doInBackground(Void... voids) {
        String directory = Application.getLocalPaintDirectory();
        File file = new File(directory);
        File[] paints = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return true;
            }
        });
        List<PaintReference> refs = new ArrayList<PaintReference>();
        if (paints != null){
            for (File paint : paints){
                PaintReference ref = new PaintReference();
                ref.name = paint.getName();
                ref.path = "file://" + paint.getAbsolutePath();
                Log.d(Application.TAG, "Path: " + ref.path);
                refs.add(ref);
            }
        }
        return refs;
    }

    @Override
    protected void onPostExecute(List<PaintReference> refs) {
        super.onPostExecute(refs);
        Application.getInstance().putTempObject(Application.PAINT_REFERENCES, refs);
        if (adapter != null){
            adapter.clearPaint();
            for (PaintReference ref : refs)
                adapter.addPaint(ref);
            adapter.notifyDataSetChanged();
        }
        if (onFinish != null)
            onFinish.run();
    }
}
