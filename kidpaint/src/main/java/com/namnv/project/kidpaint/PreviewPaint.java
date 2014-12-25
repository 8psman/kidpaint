package com.namnv.project.kidpaint;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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

import java.util.Arrays;
import java.util.List;

public class PreviewPaint extends ActionBarActivity implements View.OnTouchListener{

    ZoomUtil zoomUtil;

    List<PaintReference> paintRefs;
    PaintReference paintRef;

    TextView tvInfo;
    int paintWidth;
    int paintHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Application.getInstance().getAppTheme());
        setContentView(R.layout.preview_paint);

        paintRefs = (List<PaintReference>)Application.getInstance().getTempObject(Application.PAINT_REFERENCES);
        paintRef = (PaintReference)Application.getInstance().getTempObject(Application.PAINT_TO_PREVIEW);

        tvInfo = (TextView) findViewById(R.id.preview_image_name);

         tvInfo.setText(paintRef.name);

        final ProgressBar loadingBar = (ProgressBar) findViewById(R.id.preview_image_loading);
        loadingBar.setVisibility(View.VISIBLE);

        ImageView imageView = (ImageView) findViewById(R.id.preview_image_view);
        ImageLoader.getInstance().displayImage(paintRef.path, imageView, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {}
            @Override
            public void onLoadingCancelled(String imageUri, View view) { }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                tvInfo.setText("Error loading paint!");
                loadingBar.setVisibility(View.GONE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
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
        // Inflate the menu; this adds items to the action bar if it is present.
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

    public void onSharePaint(){
//        if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.PHOTOS)){
//            PaintReference paintRef = this.paintRef;
//            new ImageLoaderAsyncTask(paintRef, new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
//                @Override
//                public void onResult(Bitmap bitmap) {
//                    if (bitmap != null){
//                        FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(PreviewPaint.this)
//                                .addPhotos(Arrays.asList(bitmap))
//                                .build();
//                        uiHelper.trackPendingDialogCall(shareDialog.present());
//                    }
//                }
//            }).execute();
//        }else{
//            DialogFactory.createMessageDialog(this, "Please install or update facebook application!").show();
//        }
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, (Uri.parse(paintRef.path)));
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

    public void onEditPaint(){
        final AlertDialog loadingDialog = DialogFactory.createLoadingDialog(this, null);
        loadingDialog.show();
        new ImageLoaderAsyncTask(paintRef, new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                if (bitmap != null){
                    PaintHolder info = new PaintHolder();
                    info.name   = paintRef.name;
                    info.width  = bitmap.getWidth();
                    info.height = bitmap.getHeight();
                    info.background = Color.WHITE;
                    info.bitmap = bitmap;
                    Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
                    Intent intent = new Intent(PreviewPaint.this, Paint.class);
                    startActivity(intent);
                    finish();
                }else{
                    DialogFactory.createMessageDialog(PreviewPaint.this, "Cannot load paint!").show();
                }
                loadingDialog.dismiss();
            }
        }).execute();
    }

    public void onRenamePaint(){
        AlertDialog alertDialog = DialogFactory.createRenameDialog(this, paintRef,
                new Runnable() {
                    @Override
                    public void run() {
                        Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                        tvInfo.setText(paintRef.name + " (" + paintWidth + "px : " + paintHeight +"px)");
                    }
                });
        alertDialog.show();
    }

    public void onDeletePaint(){
        AlertDialog alertDialog = DialogFactory.createDeleteDialog(this,
                new Runnable() {
                    @Override
                    public void run() {
                        PaintReference curRef = PreviewPaint.this.paintRef;
                        boolean isSuccess = Application.deletePaint(curRef);
                        if (isSuccess) {
                            Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                            finish();
                        } else {
                            AlertDialog errorDialog = DialogFactory.createMessageDialog(PreviewPaint.this,
                                    "Error on deleting file!");
                            errorDialog.show();
                        }
                    }
                });
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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
