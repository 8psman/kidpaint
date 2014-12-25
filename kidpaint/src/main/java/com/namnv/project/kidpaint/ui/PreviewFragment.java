package com.namnv.project.kidpaint.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.namnv.project.kidpaint.R;
import com.namnv.project.kidpaint.object.PaintReference;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by 8psman on 10/27/2014.
 */
public class PreviewFragment extends Fragment{

    PaintReference paintRef;

    public void setPaintReferences(PaintReference ref){
        this.paintRef = ref;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preview, container, false);

        if (paintRef == null){
            paintRef = new PaintReference();
            if (savedInstanceState != null){
                paintRef.path = savedInstanceState.getString("paint_ref");
                paintRef.name = savedInstanceState.getString("paint_name");
            }
        }

        ImageView imageView = (ImageView) view.findViewById(R.id.preview_image_view);
        ImageLoader.getInstance().displayImage(paintRef.path, imageView);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("paint_ref", paintRef.path);
        outState.putString("paint_name", paintRef.name);
    }
}
