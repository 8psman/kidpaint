package com.namnv.project.kidpaint.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.namnv.project.kidpaint.Application;
import com.namnv.project.kidpaint.R;
import com.namnv.project.kidpaint.object.PaintReference;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 8psman on 10/27/2014.
 */
public class GalleryGridAdapter extends ArrayAdapter<PaintReference>{

    LayoutInflater inflater;

    public GalleryGridAdapter(Context context, int resource) {
        super(context, resource);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PaintReference paintRef = getItem(position);
        ViewHolder holder;
        if (convertView == null){
            if (inflater == null){
                inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView =inflater.inflate(R.layout.gallery_item, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView)convertView.findViewById(R.id.gallery_item_image);
            holder.name = (TextView) convertView.findViewById(R.id.gallery_item_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(paintRef.name);
        ImageLoader.getInstance().displayImage(paintRef.path, holder.image, Application.getImageDisplayOptions());
        return  convertView;
    }

    class ViewHolder{
        ImageView image;
        TextView name;
    }

    List<PaintReference> refs = new ArrayList<PaintReference>();
    Filter filter;

    public void addPaint(PaintReference object) {
        add(object);
        refs.add(object);
    }

    public void clearPaint(){
        clear();
        refs.clear();
    }

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    FilterResults results = new FilterResults();
                    if (charSequence == null || charSequence.length() == 0){
                        results.count = GalleryGridAdapter.this.refs.size();
                        results.values = GalleryGridAdapter.this.refs;
                        return results;
                    }
                    List<PaintReference> rfs = new ArrayList<PaintReference>();
                    results.count = 0;
                    results.values = rfs;

                    for (int i=0; i< refs.size(); i++)
                        if (refs.get(i).name.toLowerCase().contains(charSequence.toString().toLowerCase())){
                            results.count ++;
                            rfs.add(refs.get(i));
                        }
                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    List<PaintReference> rfs = (List<PaintReference>) filterResults.values;
                    clear();
                    for (int i=0; i<rfs.size(); i++){
                        add(rfs.get(i));
                    }
                    notifyDataSetChanged();
                }
            };
        }
        return filter;
    }
}
