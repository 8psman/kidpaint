package com.namnv.project.kidpaint.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.namnv.project.kidpaint.R;
import com.namnv.project.kidpaint.object.PaintReference;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 8psman on 10/27/2014.
 */
public class GalleryGridAdapter extends ArrayAdapter<PaintReference>{

    LayoutInflater inflater;

    DisplayImageOptions options;

    public GalleryGridAdapter(Context context, int resource) {
        super(context, resource);

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
//                .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
//                .showImageOnFail(R.drawable.ic_error) // resource or drawable
                .resetViewBeforeLoading(true)  // default
                .delayBeforeLoading(0)
                .cacheInMemory(true) // default
                .cacheOnDisk(false) // default
//                .preProcessor(...)
//        .postProcessor(...)
//        .extraForDownloader(...)
//        .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
//                .decodingOptions(...)
                .displayer(new SimpleBitmapDisplayer()) // default
                .handler(new Handler()) // default
                .build();
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
        ImageLoader.getInstance().displayImage(paintRef.path, holder.image, options);
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
