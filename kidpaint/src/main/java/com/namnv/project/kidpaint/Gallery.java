package com.namnv.project.kidpaint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.namnv.project.kidpaint.adapter.GalleryGridAdapter;
import com.namnv.project.kidpaint.object.PaintHolder;
import com.namnv.project.kidpaint.object.PaintReference;
import com.namnv.project.kidpaint.task.ImageLoaderAsyncTask;
import com.namnv.project.kidpaint.task.PaintListLoaderAsyncTask;
import com.namnv.project.kidpaint.ui.Painter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class Gallery extends ActionBarActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener{

    public static final int PICK_IMAGE_REQUEST_CODE = 1111;

    public static final int NEW_INTENT_CODE = 1112;

    public static final String KEY_NEED_UPDATE_CONTENT = "need_update_content";

    GalleryGridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery);

        GridView galleryGridView = (GridView) findViewById(R.id.gallery_grid_view);
        adapter = new GalleryGridAdapter(this, R.layout.gallery_item);

        galleryGridView.setAdapter(adapter);
        galleryGridView.setOnItemClickListener(this);

        Application.getInstance().putTempObject(KEY_NEED_UPDATE_CONTENT, false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery, menu);

        /** Cài đặt khung tìm kiếm */
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    /** Hành động chọn ảnh từ bộ nhớ */
    public void onNewPaintFromImage(){
        pickImageByIntent();
    }

    /** Gọi hành động chọn ảnh của hệ thống */
    public void pickImageByIntent(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select image"), PICK_IMAGE_REQUEST_CODE);
    }

    /**
     * Khi người dùng chọn ảnh xong
     * @param path Đường dẫn tới ảnh
     */
    public void onSelectedImage(String path){
        final PaintReference ref = new PaintReference();
        ref.name = new File(path).getName();
        ref.path = path;

        /** Hiển thị dialog đang tải */
        final Dialog progress = new ProgressDialog(this);
        progress.setCancelable(false);
        progress.show();

        /** Tải ảnh vào bộ nhớ */
        new ImageLoaderAsyncTask(ref, new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                progress.dismiss();
                if (bitmap != null){
                    /** Tải ảnh thành công */
                    onImageLoaded(ref.name, bitmap);
                }else{
                    /** Thông báo lỗi tải ảnh */
                    new AlertDialog.Builder(Gallery.this)
                            .setTitle("Lỗi")
                            .setMessage("Không tải được ảnh")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create().show();
                }
            }
        }).execute();
    }

    /**
     * Xử lí khi load ảnh thành công
     * @param name Tên ảnh
     * @param bitmap Dữ liệu ảnh
     */
    public void onImageLoaded(String name, Bitmap bitmap){
        PaintHolder info = new PaintHolder();
        info.name = name;
        info.bitmap = bitmap;
        info.width = bitmap.getWidth();
        info.height = bitmap.getHeight();
        info.background = Color.WHITE;
        Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
        Intent intent = new Intent(this, Paint.class);
        startActivity(intent);
    }

    /** Hành động tạo mới ảnh */
    public void onNewPaint(){
        /** Gọi activity tạo mới ảnh */
        Intent intent = new Intent(this, New.class);
        startActivityForResult(intent, NEW_INTENT_CODE);
    }

    /** Sự kiện lựa chọn menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_new_paint: /** Tạo mới ảnh */
                onNewPaint();
                break;
            case R.id.action_new_paint_from_image: /** Chọn ảnh từ bộ nhớ */
                onNewPaintFromImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            switch (requestCode){
                /** Kết quả trả về từ activity New */
                case NEW_INTENT_CODE:
                    Intent intent = new Intent(this, Paint.class);
                    startActivity(intent);
                    break;
                /** Kết quả trả về từ intent chọn ảnh */
                case PICK_IMAGE_REQUEST_CODE:
                    /** Lấy đường dẫn ảnh đã chọn */
                    Uri uri = data.getData();
                    Cursor cursor = getContentResolver().query(uri,
                            new String[] {MediaStore.Images.ImageColumns.DATA }, null, null, null);
                    cursor.moveToFirst();
                    String imagePath = cursor.getString(0);
                    cursor.close();
                    /** Xử lý đường dẫn ảnh đã chọn */
                    onSelectedImage(imagePath);
                    break;
            }
        }
    }

    /** Bắt sự kiện lựa chọn ảnh trong danh sách */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        /** Đưa đối tượng chứa thông tin ảnh vào bộ lưu trữ tạm thời */
        Application.getInstance().putTempObject(Application.PAINT_TO_PREVIEW, adapterView.getItemAtPosition(position));
        /** Gọi activity PreviewPaint */
        Intent intent = new Intent(this, PreviewPaint.class);
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /** Sự kiện kết quả tìm kiếm thay đổi */
    @Override
    public boolean onQueryTextChange(String newText) {
        /** Lọc kết quả hiển thị */
        adapter.getFilter().filter(newText);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /** Kiểm tra xem có cần load lại không */
        boolean needUpdateContent = (Boolean) Application.getInstance().getTempObject(KEY_NEED_UPDATE_CONTENT);
        if (needUpdateContent){
            /** Nếu cần load lại thì xóa cache ảnh thumbnail */
            ImageLoader.getInstance().clearMemoryCache();
            Application.getInstance().putTempObject(KEY_NEED_UPDATE_CONTENT, false);
        }
        /** Load lại danh sách ảnh */
        new PaintListLoaderAsyncTask(adapter, null).execute();
    }
}
