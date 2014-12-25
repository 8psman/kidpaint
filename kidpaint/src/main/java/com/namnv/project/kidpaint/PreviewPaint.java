package com.namnv.project.kidpaint;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.namnv.project.kidpaint.object.PaintHolder;
import com.namnv.project.kidpaint.object.PaintReference;
import com.namnv.project.kidpaint.task.ImageLoaderAsyncTask;
import com.namnv.project.kidpaint.ui.DialogFactory;
import com.namnv.project.kidpaint.ui.Painter;
import com.namnv.project.kidpaint.ui.ZoomUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class PreviewPaint extends ActionBarActivity implements View.OnTouchListener{

    /** Đối tượng hỗ trợ phóng to thu nhỏ ảnh */
    ZoomUtil zoomUtil;

    /** Đối tượng chứa thông tin ảnh */
    PaintReference paintRef;

    /** Hiển thị thông tin ảnh */
    TextView tvInfo;

    /** Kích thước ảnh */
    int paintWidth;
    int paintHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.preview_paint);

        /** Lấy thông tin ảnh cần hiển thị từ bộ nhớ tạm thời */
        paintRef = (PaintReference)Application.getInstance().getTempObject(Application.PAINT_TO_PREVIEW);

        /** Nếu painRef == null hay không có thông tin ảnh cần hiển thị thì trở về Gallery */
        if (paintRef == null){
            finish();
            return;
        }

        /** Cài đặt giao diện */
        tvInfo = (TextView) findViewById(R.id.preview_image_name);
        tvInfo.setText(paintRef.name);

        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.preview_image_loading);
        loadingBar.setVisibility(View.VISIBLE);

        /** Hiển thị ảnh */
        ImageView imageView = (ImageView) findViewById(R.id.preview_image_view);
        ImageLoader.getInstance().displayImage(paintRef.path, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {}
            @Override
            public void onLoadingCancelled(String imageUri, View view) { }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                /** Lỗi tải ảnh */
                tvInfo.setText("Error loading paint!");
                loadingBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                /** Tải ảnh thành công */
                paintWidth = loadedImage.getWidth();
                paintHeight = loadedImage.getHeight();
                tvInfo.setText(paintRef.name + " (" + paintWidth + "px : " + paintHeight +"px)");
                loadingBar.setVisibility(View.GONE);
            }
        });
        imageView.setOnTouchListener(this);

        zoomUtil = new ZoomUtil();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_edit_paint:
                onEditPaint();
                break;
            case R.id.action_rename_paint:
                onRenamePaint();
                break;
            case R.id.action_delete_paint:
                onDeletePaint();
                break;
            case R.id.action_share_paint:
                onSharePaint();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Hành động chia sẻ ảnh */
    public void onSharePaint(){
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, (Uri.parse(paintRef.path)));
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

    /** Hành động chỉnh sửa ảnh */
    public void onEditPaint(){
        /** Hiển thị dialog loading */
        final AlertDialog loadingDialog = DialogFactory.createLoadingDialog(this, null);
        loadingDialog.show();

        /** Tải dữ liệu ảnh */
        new ImageLoaderAsyncTask(paintRef, new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                /** Tải dữ liệu ảnh thành công */
                if (bitmap != null){
                    /** Khởi tạo đối tượng chứa thông tin ảnh cần chỉnh sửa */
                    PaintHolder info = new PaintHolder();
                    info.name   = paintRef.name;
                    info.width  = bitmap.getWidth();
                    info.height = bitmap.getHeight();
                    info.background = Color.WHITE;
                    info.bitmap = bitmap;

                    /** Đưa đối tượng chứa thông tin ảnh vào bộ nhớ tạm thời */
                    Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
                    /** Gọi Activity vẽ ảnh */
                    Intent intent = new Intent(PreviewPaint.this, Paint.class);
                    startActivity(intent);
                    finish();
                }else{
                    /** Tải dữ liệu ảnh thất bại */
                    DialogFactory.createMessageDialog(PreviewPaint.this, "Cannot load paint!").show();
                }
                /** Ẩn dialog loading */
                loadingDialog.dismiss();
            }
        }).execute();
    }

    /** Hành động đổi tên ảnh */
    public void onRenamePaint(){
        /** Hiển thị dialog xác nhận */
        AlertDialog alertDialog = DialogFactory.createRenameDialog(this, paintRef,
                new Runnable() {
                    @Override
                    public void run() {
                        /** Sau khi đổi tên */
                        /** Thông báo để Gallery cập nhật lại danh sách */
                        Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                        /** Cập nhật thông tin ảnh đang hiển thị */
                        tvInfo.setText(paintRef.name + " (" + paintWidth + "px : " + paintHeight +"px)");
                    }
                });
        alertDialog.show();
    }

    /** Hành động xóa ảnh */
    public void onDeletePaint(){
        AlertDialog alertDialog = DialogFactory.createDeleteDialog(this,
                new Runnable() {
                    @Override
                    public void run() {
                        PaintReference curRef = PreviewPaint.this.paintRef;
                        boolean isSuccess = Application.deletePaint(curRef);
                        if (isSuccess) {
                            /** Thành công */
                            Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                            finish();
                        } else {
                            /** Thất bại */
                            AlertDialog errorDialog = DialogFactory.createMessageDialog(PreviewPaint.this,
                                    "Error on deleting file!");
                            errorDialog.show();
                        }
                    }
                });
        alertDialog.show();
    }

    /** Bắt sự kiện chạm màn hình để phóng to thu nhỏ */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        if (view.getScaleType() != ImageView.ScaleType.MATRIX){
            zoomUtil.getMatrix().set(view.getImageMatrix());
            view.setScaleType(ImageView.ScaleType.MATRIX);
        }
        zoomUtil.onTouch(v, event);
        view.setImageMatrix(zoomUtil.getMatrix());
        return true;
    }
}
