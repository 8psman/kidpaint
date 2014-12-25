package com.namnv.project.kidpaint;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

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

import yuku.ambilwarna.AmbilWarnaDialog;

public class Paint extends ActionBarActivity implements View.OnClickListener{

    /** Giao diện vẽ */
    Painter painter;

    /** Hộp chứa công cụ */
    ToolBox toolBox;

    /** Công cụ vẽ hiện tại */
    PaintTool currentTool;

    /** Button ứng với công cụ vẽ hiện tại */
    View currentToolView;

    /** Các đối tượng giao diện để thay đổi thuộc tính công cụ vẽ */
    Spinner thicknessPicker; // Lựa chọn kích thước bút
    Spinner eraserSizePicker; // Lựa chọn kích thước tẩy
    Spinner pencilOpacitySpinner; // Lựa chọn độ mờ của bút chì

    Button colorChooser; // Lựa chọn màu

    /** Các button tương ứng với các hành động xử lý ảnh */
    View toolCrop;
    View toolClear;  // Tẩy toàn bộ ảnh đang vẽ
    View toolSave;   // Lưa ảnh đang vẽ
    View toolCancel; // Hủy ảnh hiện tại


    ViewGroup toolWrapper;      // Giao diện chứa công cụ vẽ

    ImageView shapeToolView;    // Lựa chọn công cụ vẽ hình

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.paint);

        /** Cài đặt custom actionbar */
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.paint_actionbar);

        /** Cài đặt giao diện */
        painter = (Painter) findViewById(R.id.painter);
        painter.initializePainter();

        /** Khởi tạo hộp chứa công cụ */
        toolBox = new ToolBox();

        /** Khởi tạo các công cụ vẽ */
        toolBox.handTool    = new HandTool(painter);
        toolBox.pencilTool  = new PencilTool(painter);
        toolBox.eraserTool  = new EraserTool(painter);
        toolBox.bucketTool  = new BucketTool(painter);
        toolBox.shapeTool   = new ShapeTool(painter);

        toolWrapper = (ViewGroup) actionBar.getCustomView().findViewById(R.id.tool_wrapper);
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

        thicknessPicker.setAdapter(new ImageSpinnerAdapter(this, ToolBox.thicknessDrawable));
        eraserSizePicker.setAdapter(new ImageSpinnerAdapter(this, ToolBox.eraserSizeDrawable));
        pencilOpacitySpinner.setAdapter(new TextSpinnerAdapter(this, getResources().getStringArray(R.array.pencil_opacity)));

        toolWrapper.removeAllViews();

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

        /** Mặc định sử dụng công cụ HandTool đầu tiên*/
        applyHandTool();
        currentToolView = findViewById(R.id.tool_hand);
        currentToolView.setSelected(true);
    }

    /** Sử lý sự kiện sau khi người dùng chọn màu */
    public void onColorChosen(int color){
        /** Thiết lập màu hiện tại cho hộp công cụ */
        toolBox.currentColor = color;
        colorChooser.setBackgroundColor(color);

        /** Thiết lập màu mới cho công cụ vẽ hiện tại */
        int alpha = currentTool.getPaint().getAlpha();
        currentTool.getPaint().setColor(color);
        currentTool.getPaint().setAlpha(alpha);

        /** Bổ sung màu mới vào danh sách màu gần đây */
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

    /** Xử lý hành động người dùng chọn màu */
    public void onChooseColor(){
        /** Gọi dialog chọn màu */
        DialogFactory.createColorChooserDialog(this, toolBox.recentColors, new DialogFactory.ColorChosenListener() {
            @Override
            public void onChooseColor(int color) {
                /** Người dùng chọn màu trong danh sách */
                onColorChosen(color);
            }

            @Override
            public void onPickColor() {
                /** Người dùng muốn chọn màu khác, hiển thị bảng màu */
                showColorPicker();
            }
        }).show();
    }

    /** Hiển thị bảng màu */
    public void showColorPicker(){
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, currentTool.getPaint().getColor(), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                /** Người dùng chọn màu */
                onColorChosen(color);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                /** Người dùng không chọn màu */
            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paint, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Sử dụng công cụ */
    public void applyTool(PaintTool tool){
        painter.setActiveTool(tool);
    }

    /** Sử dụng công cụ bằng tay Hand Tool */
    public void applyHandTool(){
        applyTool(toolBox.handTool);
        currentTool = toolBox.handTool;
    }

    /** Sử dụng công cụ bút chì */
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

    /** Sử dụng công cụ vẽ hình */
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

    /** Sử dụng công cụ xóa */
    public void applyEraserTool(){
        applyTool(toolBox.eraserTool);
        currentTool = toolBox.eraserTool;
        toolWrapper.addView(eraserSizePicker);
        toolWrapper.addView(toolClear);
        int position = eraserSizePicker.getSelectedItemPosition();
        currentTool.getPaint().setStrokeWidth(EraserTool.getEraserSizeFromPosition(position));
    }

    /** Sử dụng công cụ đổ màu */
    public void applyBucketTool(){
        applyTool(toolBox.bucketTool);
        currentTool = toolBox.bucketTool;
        toolWrapper.addView(colorChooser);
        currentTool.getPaint().setColor(toolBox.currentColor);
    }

    /** Bắt sự kiện chọn công cụ vẽ */
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

    /** Bắt sự kiện chọn công cụ vẽ hình */
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
    /** Hiển thị dialog chọn hình cần vẽ */
    private void showShapeToolDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.shape_tool_popup, null);
        shapeDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        shapeDialog.show();
    }

    /** Bắt các sự kiện liên quan đến ảnh */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tool_clear:
                /** Xóa ảnh */
                painter.clearPaint();
                break;
            case R.id.tool_save:
                /** Lưu ảnh */
                painter.savePaint();
                break;
            case R.id.tool_cancel:
                /** Hủy ảnh */
                painter.cancelPaint();
                break;
            case R.id.color_chooser:
                /** Lựa chọn color */
                onChooseColor();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /** Kết quả trả về từ acitivity New */
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

    public void onSharePaint(String path){
        String filePath = "file://" + path;
        Log.d(Application.TAG, "Share paint: " + filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/jpeg");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, (Uri.parse(filePath)));
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
    }

}
