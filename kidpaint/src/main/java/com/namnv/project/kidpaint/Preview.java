package com.namnv.project.kidpaint;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.namnv.project.kidpaint.object.PaintHolder;
import com.namnv.project.kidpaint.object.PaintReference;
import com.namnv.project.kidpaint.task.ImageLoaderAsyncTask;
import com.namnv.project.kidpaint.ui.DialogFactory;
import com.namnv.project.kidpaint.ui.MultiTouchPreviewFragment;
import com.namnv.project.kidpaint.ui.Painter;

import java.util.Arrays;
import java.util.List;

public class Preview extends ActionBarActivity {

    ViewPager viewPager;
    PagerTitleStrip indicator;
    PreviewPagerAdapter adapter;
    List<PaintReference> paintRefs;

    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Application.getInstance().getAppTheme());
        setContentView(R.layout.preview);

        paintRefs = (List<PaintReference>)Application.getInstance().getTempObject(Application.PAINT_REFERENCES);
        PaintReference paintRef = (PaintReference)Application.getInstance().getTempObject(Application.PAINT_TO_PREVIEW);

        viewPager = (ViewPager) findViewById(R.id.preview_pager_view);
        viewPager.setAdapter(adapter = new PreviewPagerAdapter(getSupportFragmentManager()));
        indicator = (PagerTitleStrip) findViewById(R.id.preview_pager_indicator);

        for (int i=0; i<paintRefs.size(); i++)
            if (paintRef == paintRefs.get(i)){
                viewPager.setCurrentItem(i);
                break;
            }

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);
    }

    public void setSwipeEnable(boolean isEnable){
        viewPager.requestDisallowInterceptTouchEvent(!isEnable);
    }

    public PaintReference getCurrentPaintReferences(){
        int curItem = viewPager.getCurrentItem();
        PaintReference curRef = paintRefs.get(curItem);
        return curRef;
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
        PaintReference paintRef = getCurrentPaintReferences();
//        if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.PHOTOS)){
//            PaintReference paintRef = getCurrentPaintReferences();
//            new ImageLoaderAsyncTask(paintRef, new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
//                @Override
//                public void onResult(Bitmap bitmap) {
//                    if (bitmap != null){
//                        FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(Preview.this)
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
        new ImageLoaderAsyncTask(getCurrentPaintReferences(), new ImageLoaderAsyncTask.OnImageLoadingResultListener() {
            @Override
            public void onResult(Bitmap bitmap) {
                if (bitmap != null){
                    PaintHolder info = new PaintHolder();
                    info.name   = getCurrentPaintReferences().name;
                    info.width  = bitmap.getWidth();
                    info.height = bitmap.getHeight();
                    info.background = Color.WHITE;
                    info.bitmap = bitmap;
                    Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
                    Intent intent = new Intent(Preview.this, Paint.class);
                    startActivity(intent);
                    finish();
                }else{
                    DialogFactory.createMessageDialog(Preview.this, "Cannot load paint!").show();
                }
                loadingDialog.dismiss();
            }
        }).execute();
    }

    public void onRenamePaint(){
        AlertDialog alertDialog = DialogFactory.createRenameDialog(this, getCurrentPaintReferences(),
                new Runnable() {
                    @Override
                    public void run() {
                        Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                        adapter.notifyDataSetChanged();
                    }
                });
        alertDialog.show();
    }

    public void onDeletePaint(){
        AlertDialog alertDialog = DialogFactory.createDeleteDialog(this,
                new Runnable() {
                    @Override
                    public void run() {
                        int curItem = viewPager.getCurrentItem();
                        PaintReference curRef = paintRefs.get(curItem);
                        boolean isSuccess = Application.deletePaint(curRef);
                        if (isSuccess) {
                            isNeedToUpdate = true;
                            paintRefs.remove(curItem);
                            adapter.notifyDataSetChanged();
                            isNeedToUpdate = false;
                            Application.getInstance().putTempObject(Gallery.KEY_NEED_UPDATE_CONTENT, true);
                        } else {
                            AlertDialog errorDialog = DialogFactory.createMessageDialog(Preview.this,
                                    "Error on deleting file!");
                            errorDialog.show();
                        }
                    }
                });
        alertDialog.show();
    }

    /**
     * ViewPager adapter
     */
    boolean isNeedToUpdate = false;

    class PreviewPagerAdapter extends FragmentStatePagerAdapter{

        public PreviewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            MultiTouchPreviewFragment fragment = new MultiTouchPreviewFragment();
            fragment.setPaintReferences(paintRefs.get(i));
            return fragment;
        }

        @Override
        public int getItemPosition(Object object) {
            Log.d(Application.TAG, object.toString());
            if (isNeedToUpdate){
                return PagerAdapter.POSITION_NONE;
            }else{
                return PagerAdapter.POSITION_UNCHANGED;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return paintRefs.get(position).name;
        }

        @Override
        public int getCount() {
            return paintRefs.size();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
            @Override
            public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
                Log.e("Activity", String.format("Error: %s", error.toString()));
            }

            @Override
            public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
                Log.i("Activity", "Success!");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
}
