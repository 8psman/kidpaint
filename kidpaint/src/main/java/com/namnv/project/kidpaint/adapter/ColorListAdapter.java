package com.namnv.project.kidpaint.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 8psman on 10/27/2014.
 */
public class ColorListAdapter extends ArrayAdapter<Integer>{

    List<Integer> colors;

    public ColorListAdapter(Context context, int resource, List<Integer> colors) {
        super(context, resource, colors);
        this.colors = colors;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int color = colors.get(position);
        if (convertView == null){
            convertView = new TextView(getContext());
        }

        convertView.setBackgroundColor(color);
        return  convertView;
    }

}
