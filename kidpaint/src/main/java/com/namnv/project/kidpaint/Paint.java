package com.namnv.project.kidpaint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.namnv.project.kidpaint.adapter.ImageSpinnerAdapter;
import com.namnv.project.kidpaint.adapter.TextSpinnerAdapter;
import com.namnv.project.kidpaint.object.BucketTool;
import com.namnv.project.kidpaint.object.EraserTool;
import com.namnv.project.kidpaint.object.HandTool;
import com.namnv.project.kidpaint.object.PaintTool;
import com.namnv.project.kidpaint.object.PencilTool;
import com.namnv.project.kidpaint.object.ShapeTool;
import com.namnv.project.kidpaint.object.ToolBox;
import com.namnv.project.kidpaint.ui.DialogFactory;
import com.namnv.project.kidpaint.ui.Painter;

import java.util.Arrays;

import yuku.ambilwarna.AmbilWarnaDialog;

public class Paint extends ActionBarActivity implements View.OnClickListener{

    Painter painter;
    ToolBox toolBox;
    PaintTool currentTool;
    View currentToolView;

//    Spinner toolColorPicker;
    Spinner thicknessPicker;
    Spinner eraserSizePicker;
    Spinner pencilOpacitySpinner;

    Button colorChooser;

    View toolCrop;
    View toolClear;
    View toolSave;
    View toolCancel;

    ViewGroup toolWrapper;
    ImageView shapeToolView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, null);
        uiHelper.onCreate(savedInstanceState);

        setTheme(Application.getInstance().getAppTheme());
        setContentView(R.layout.paint);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.paint_actionbar);

        painter = (Painter) findViewById(R.id.painter);
        painter.initializePainter();

        toolBox = new ToolBox();

        toolWrapper = (ViewGroup) actionBar.getCustomView().findViewById(R.id.tool_wrapper);
//        toolColorPicker = (Spinner) actionBar.getCustomView().findViewById(R.id.spinner_color);
        thicknessPicker = (Spinner) actionBar.getCustomView().findViewById(R.id.spinner_thickness);
        eraserSizePicker = (Spinner) actionBar.getCustomView().findViewById(R.id.spinner_eraser_size);
        pencilOpacitySpinner = (Spinner) actionBar.getCustomView().findViewById(R.id.spinner_pencil_opacity);

        colorChooser = (Button) actionBar.getCustomView().findViewById(R.id.color_chooser);
        colorChooser.setBackgroundColor(toolBox.currentColor);

        toolCrop = actionBar.getCustomView().findViewById(R.id.tool_crop);
        toolClear = actionBar.getCustomView().findViewById(R.id.tool_clear);
        toolSave = actionBar.getCustomView().findViewById(R.id.tool_save);
        toolCancel = actionBar.getCustomView().findViewById(R.id.tool_cancel);

        colorChooser.setOnClickListener(this);
        toolCrop.setOnClickListener(this);
        toolClear.setOnClickListener(this);
        toolSave.setOnClickListener(this);
        toolCancel.setOnClickListener(this);

//        toolColorPicker.setAdapter(new ColorChooserAdapter(this));
        thicknessPicker.setAdapter(new ImageSpinnerAdapter(this, ToolBox.thicknessDrawable));
        eraserSizePicker.setAdapter(new ImageSpinnerAdapter(this, ToolBox.eraserSizeDrawable));
        pencilOpacitySpinner.setAdapter(new TextSpinnerAdapter(this, getResources().getStringArray(R.array.pencil_opacity)));

        toolWrapper.removeAllViews();

        toolBox.handTool    = new HandTool(painter);
        toolBox.pencilTool  = new PencilTool(painter);
        toolBox.eraserTool  = new EraserTool(painter);
        toolBox.bucketTool  = new BucketTool(painter);
        toolBox.shapeTool   = new ShapeTool(painter);
//        toolColorPicker.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
//                int colorRes = ToolBox.defaultDrawColor[position];
//                int color = getResources().getColor(colorRes);
//                int alpha = currentTool.getPaint().getAlpha();
//                currentTool.getPaint().setColor(color);
//                currentTool.getPaint().setAlpha(alpha);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        thicknessPicker.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                currentTool.getPaint().setStrokeWidth(PencilTool.getPencilSizeFromPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        eraserSizePicker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                currentTool.getPaint().setStrokeWidth(EraserTool.getEraserSizeFromPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pencilOpacitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                currentTool.getPaint().setAlpha(PencilTool.getAlphaFromPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        shapeToolView = (ImageView) findViewById(R.id.tool_shape);
        shapeToolView.setImageResource(R.drawable.ic_tool_shape_two);
        // use hand tool
        applyHandTool();
        currentToolView = findViewById(R.id.tool_hand);
        currentToolView.setSelected(true);
    }

    public void onColorChosen(int color){
        toolBox.currentColor = color;
        colorChooser.setBackgroundColor(color);
        int alpha = currentTool.getPaint().getAlpha();
        currentTool.getPaint().setColor(color);
        currentTool.getPaint().setAlpha(alpha);

        for (int i=0; i<toolBox.recentColors.size(); i++){
            if ( toolBox.recentColors.get(i) == color){
                toolBox.recentColors.remove(i);
                break;
            }
        }
        toolBox.recentColors.add(0, color);
        if (toolBox.recentColors.size() > 10)
            toolBox.recentColors.remove(10);
    }

    public void onChooseColor(){
        DialogFactory.createColorChooserDialog(this, toolBox.recentColors, new DialogFactory.ColorChosenListener() {
            @Override
            public void onChooseColor(int color) {
                onColorChosen(color);
            }

            @Override
            public void onPickColor() {
                showColorPicker();
            }
        }).show();
    }

    public void showColorPicker(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentTool.getPaint().getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                onColorChosen(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // cancel was selected by the user
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.paint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void applyTool(PaintTool tool){
        painter.setActiveTool(tool);
    }

    public void applyHandTool(){
        applyTool(toolBox.handTool);
        currentTool = toolBox.handTool;
//        toolWrapper.addView(toolCrop);
    }

    public void applyPencilTool(){
        applyTool(toolBox.pencilTool);
        currentTool = toolBox.pencilTool;
        toolWrapper.addView(pencilOpacitySpinner);
        toolWrapper.addView(thicknessPicker);
        toolWrapper.addView(colorChooser);

        currentTool.getPaint().setColor(toolBox.currentColor);
        currentTool.getPaint().setAlpha(PencilTool.getAlphaFromPosition(pencilOpacitySpinner.getSelectedItemPosition()));
        currentTool.getPaint().setStrokeWidth(PencilTool.getPencilSizeFromPosition(thicknessPicker.getSelectedItemPosition()));
    }

    public void applyShapeTool(){
        applyTool(toolBox.shapeTool);
        currentTool = toolBox.shapeTool;
        toolWrapper.addView(pencilOpacitySpinner);
        toolWrapper.addView(thicknessPicker);
        toolWrapper.addView(colorChooser);

        currentTool.getPaint().setColor(toolBox.currentColor);
        currentTool.getPaint().setAlpha(PencilTool.getAlphaFromPosition(pencilOpacitySpinner.getSelectedItemPosition()));
        currentTool.getPaint().setStrokeWidth(PencilTool.getPencilSizeFromPosition(thicknessPicker.getSelectedItemPosition()));
    }

    public void applyEraserTool(){
        applyTool(toolBox.eraserTool);
        currentTool = toolBox.eraserTool;
        toolWrapper.addView(eraserSizePicker);
        toolWrapper.addView(toolClear);
        int position = eraserSizePicker.getSelectedItemPosition();
        currentTool.getPaint().setStrokeWidth(EraserTool.getEraserSizeFromPosition(position));
    }

    public void applyBucketTool(){
        applyTool(toolBox.bucketTool);
        currentTool = toolBox.bucketTool;
//        toolWrapper.addView(toolColorPicker);
        toolWrapper.addView(colorChooser);
        currentTool.getPaint().setColor(toolBox.currentColor);
//        int color = getResources().getColor((Integer)toolColorPicker.getSelectedItem());
//        int opacityPos = pencilOpacitySpinner.getSelectedItemPosition();
//        currentTool.getPaint().setColor(color);
//        currentTool.getPaint().setAlpha(PencilTool.getAlphaFromPosition(opacityPos));
    }

    public void onToolSelected(View view){
        if (currentToolView != null)
            currentToolView.setSelected(false);
        currentToolView = view;
        currentToolView.setSelected(true);
        toolWrapper.removeAllViews();
        switch (view.getId()){
            case R.id.tool_hand:    applyHandTool();        break;
            case R.id.tool_pencil:  applyPencilTool();      break;
            case R.id.tool_eraser:  applyEraserTool();      break;
            case R.id.tool_bucket:  applyBucketTool();      break;
            case R.id.tool_shape:   showShapeToolDialog();  break;
        }
    }

    public void onShapeToolSelected(View view){
        if (shapeDialog != null)
            shapeDialog.dismiss();
        int id = view.getId();
        if (id == R.id.shape_tool_zero){
            toolBox.shapeTool.setShapeDrawer(ShapeTool.ovalShapeDrawer);
            shapeToolView.setImageResource(R.drawable.ic_tool_shape_zero);
        }else if (id == R.id.shape_tool_two){
            toolBox.shapeTool.setShapeDrawer(ShapeTool.lineShapeDrawer);
            shapeToolView.setImageResource(R.drawable.ic_tool_shape_two);
        }else if (id == R.id.shape_tool_three){
            toolBox.shapeTool.setShapeDrawer(ShapeTool.threeEdgeShapeDrawer);
            shapeToolView.setImageResource(R.drawable.ic_tool_shape_three);
        }else if (id == R.id.shape_tool_four){
            toolBox.shapeTool.setShapeDrawer(ShapeTool.rectShapeDrawer);
            shapeToolView.setImageResource(R.drawable.ic_tool_shape_four);
        }else if (id == R.id.shape_tool_five){
            toolBox.shapeTool.setShapeDrawer(ShapeTool.fiveEdgeShapeDrawer);
            shapeToolView.setImageResource(R.drawable.ic_tool_shape_five);
        }else if (id == R.id.shape_tool_six){
            toolBox.shapeTool.setShapeDrawer(ShapeTool.sixEdgeShapeDrawer);
            shapeToolView.setImageResource(R.drawable.ic_tool_shape_six);
        }

        applyShapeTool();
    }

    Dialog shapeDialog;
    private void showShapeToolDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.shape_tool_popup, null);
        shapeDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        shapeDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tool_clear:
                painter.clearPaint();
                break;
            case R.id.tool_save:
                painter.savePaint();
                break;
            case R.id.tool_cancel:
                painter.cancelPaint();
                break;
            case R.id.color_chooser:
                onChooseColor();
                break;
        }
    }

    private UiLifecycleHelper uiHelper;
    public void onSharePaint(Bitmap bitmap){
        if (FacebookDialog.canPresentShareDialog(this, FacebookDialog.ShareDialogFeature.PHOTOS)){
            FacebookDialog shareDialog = new FacebookDialog.PhotoShareDialogBuilder(Paint.this)
                    .addPhotos(Arrays.asList(bitmap))
                    .build();
            uiHelper.trackPendingDialogCall(shareDialog.present());
        }else{
            DialogFactory.createMessageDialog(this, "Please install or update facebook application!").show();
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

        if (resultCode == RESULT_OK){
            switch (requestCode){
                case Gallery.NEW_INTENT_CODE:
                    painter.initializePainter();
                    applyHandTool();
                    currentToolView.setSelected(false);
                    currentToolView = findViewById(R.id.tool_hand);
                    currentToolView.setSelected(true);
                    painter.invalidate();
                    break;
            }
        }
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
