package com.namnv.project.kidpaint.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.namnv.project.kidpaint.R;

/**
 * Created by 8psman on 10/28/2014.
 */
public class TextSpinnerAdapter implements SpinnerAdapter{

    Context context;
    String[] contents;
    LayoutInflater inflater;

    public TextSpinnerAdapter(Context context, String[] contents){
        this.context = context;
        this.contents = contents;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private View getCustomView(int position, View view, ViewGroup viewGroup){
        if (view == null){
            view = inflater.inflate(R.layout.spinner_dropdown_view, viewGroup, false);

            View wrapper = view.findViewById(R.id.spinner_item_wrapper);
            StateListDrawable drawable = new StateListDrawable();
            drawable.addState(new int[]{android.R.attr.state_pressed}, context.getResources().getDrawable(R.color.action_button_pressed));
            drawable.addState(new int[]{-android.R.attr.state_pressed}, new ColorDrawable(context.getResources().getColor(R.color.theme_default)));
            wrapper.setBackgroundDrawable(drawable);

            ImageView imageView = (ImageView)view.findViewById(R.id.spinner_item_image);
            imageView.setVisibility(View.GONE);
        }

        TextView textView = (TextView) view.findViewById(R.id.spinner_item_text);
        textView.setText((String)getItem(position));
        return view;
    }

    @Override
    public View getDropDownView(int position, View view, ViewGroup viewGroup) {
        return getCustomView(position, view, viewGroup);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        return getCustomView(position, view, viewGroup);
    }

    @Override
    public int getCount() {
        return contents.length;
    }

    @Override
    public Object getItem(int position) {
        return contents[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }



    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }
}
