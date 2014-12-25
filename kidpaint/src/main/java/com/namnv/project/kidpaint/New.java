package com.namnv.project.kidpaint;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.namnv.project.kidpaint.adapter.TextSpinnerAdapter;
import com.namnv.project.kidpaint.object.PaintHolder;
import com.namnv.project.kidpaint.ui.Painter;

public class New extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    EditText name;
    Spinner  sizeSpinner;
    View     customView;
    EditText width;
    EditText height;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new);

        /** Lấy ra các đối tượng trong layout */
        name        = (EditText) findViewById(R.id.new_paint_name);
        sizeSpinner = (Spinner) findViewById(R.id.new_paint_size);
        customView  = findViewById(R.id.new_paint_custom);
        width       = (EditText) findViewById(R.id.new_paint_width);
        height      = (EditText) findViewById(R.id.new_paint_height);
        message     = (TextView) findViewById(R.id.new_paint_message);

        /** Cài đặt spinner chọn kích thước */
        sizeSpinner.setAdapter(new TextSpinnerAdapter(this, getResources().getStringArray(R.array.paint_size_preset)));
        sizeSpinner.setOnItemSelectedListener(this);

        /** Cài đặt sự kiện cho các button */
        Button ok = (Button) findViewById(R.id.new_paint_ok);
        Button cancel = (Button) findViewById(R.id.new_paint_cancel);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /** Lắng nghe sự kiện click button */
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            /** Click button cancel */
            case R.id.new_paint_cancel:
                setResult(RESULT_CANCELED);
                finish(); // Trở về Gallery
                break;
            /** Click button ok */
            case R.id.new_paint_ok:
                /** Tạo đối tượng lưu trữ thông tin ảnh */
                PaintHolder info = new PaintHolder();
                info.name = name.getText().toString();
                /** Kiểm tra tên */
                if (info.name.length() == 0){
                    message.setText("Name is missing!");
                    return;
                }

                /** Kiểm tra kích thước ảnh */
                int sizePosition = sizeSpinner.getSelectedItemPosition();
                /** Mặc định */
                if (sizePosition < sizeSpinner.getCount()-1){
                    String strSize = (String)sizeSpinner.getSelectedItem();
                    String[] strDimension = strSize.split("x");
                    info.width  = Integer.parseInt(strDimension[0].trim());
                    info.height = Integer.parseInt(strDimension[1].trim());

                }else{ /** Tùy chọn */
                    String strWidth  = width.getText().toString();
                    String strHeight = height.getText().toString();
                    if (strWidth.length() == 0 || strHeight.length() == 0){
                        message.setText("Size is missing!");
                        return;
                    }
                    try{
                        info.width  = Integer.parseInt(strWidth);
                        info.height = Integer.parseInt(strHeight);
                        if (info.width * info.height > 2048 * 2048){
                            message.setText("Size is too large!");
                            return;
                        }
                        if (info.width * info.height == 0){
                            message.setText("Width and height must be greater then zero!");
                            return;
                        }
                    }catch (Exception ex){
                        message.setText("Invalid width and height!");
                        return;
                    }
                }

                info.background = Color.WHITE;
                info.bitmap = null;

                /** Đưa đối tượng thông tin ảnh vào bộ nhớ tạm thời */
                Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
                /** Trở về Gallery */
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    /** Sự kiện chọn kích thước */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()){
            case R.id.new_paint_size:
                if (position == adapterView.getCount() - 1){
                    /** Hiển thị khung nhập kích thước */
                    customView.setVisibility(View.VISIBLE);
                }else{
                    /** Ẩn khung nhập kích thước */
                    customView.setVisibility(View.GONE);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
