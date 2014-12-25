package com.namnv.project.kidpaint;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
    Spinner sizeSpinner;
    View customView;
    EditText width;
    EditText height;
    TextView message;
//    Spinner backgroundSpinner;

    int[] backgroundColor = new int[]{
            R.color.df_background_color_1,
            R.color.df_background_color_2,
            R.color.df_background_color_3,
            R.color.df_background_color_4,
            R.color.df_background_color_5,
            R.color.df_background_color_6,
            R.color.df_background_color_7,
            R.color.df_background_color_8,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new);

       ViewGroup container = (ViewGroup) findViewById(R.id.dialog_new_container);
        container.setBackgroundResource(Application.getInstance().getThemeInfo().background);

        name = (EditText) findViewById(R.id.new_paint_name);
        sizeSpinner = (Spinner) findViewById(R.id.new_paint_size);
        customView = findViewById(R.id.new_paint_custom);
        width = (EditText) findViewById(R.id.new_paint_width);
        height = (EditText) findViewById(R.id.new_paint_height);
        message = (TextView) findViewById(R.id.new_paint_message);

//        backgroundSpinner = (Spinner) findViewById(R.id.new_paint_background);

        Button ok = (Button) findViewById(R.id.new_paint_ok);
        Button cancel = (Button) findViewById(R.id.new_paint_cancel);

        sizeSpinner.setAdapter(new TextSpinnerAdapter(this, getResources().getStringArray(R.array.paint_size_preset)));
//        backgroundSpinner.setAdapter(new ImageSpinnerAdapter(this, backgroundColor));
        sizeSpinner.setOnItemSelectedListener(this);
//        backgroundSpinner.setOnItemSelectedListener(this);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_paint, menu);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.new_paint_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;

            case R.id.new_paint_ok:
                PaintHolder info = new PaintHolder();
                info.name = name.getText().toString();
                // check name
                if (info.name.length() == 0){
                    message.setText("Name is missing!");
                    return;
                }

                // check size
                int sizePosition = sizeSpinner.getSelectedItemPosition();
                // use preset
                if (sizePosition < sizeSpinner.getCount()-1){
                    String strSize = (String)sizeSpinner.getSelectedItem();
                    String[] strDimension = strSize.split("x");
                    info.width  = Integer.parseInt(strDimension[0].trim());
                    info.height = Integer.parseInt(strDimension[1].trim());

                }else{ // use custom
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

                // check background
//                int backgroundPosition = backgroundSpinner.getSelectedItemPosition();
//                int paintBackgroundResource = backgroundColor[backgroundPosition];
//                info.background = getResources().getColor(paintBackgroundResource);
                info.background = Color.WHITE;
                info.bitmap = null;
                Application.getInstance().putTempObject(Painter.KEY_PAINT_INFO, info);
                setResult(RESULT_OK);
                finish();

                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        switch (adapterView.getId()){
            case R.id.new_paint_size:
                if (position == adapterView.getCount() - 1){
                    customView.setVisibility(View.VISIBLE);
                }else{
                    customView.setVisibility(View.GONE);
                }
                break;

//            case R.id.new_paint_background:

//                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
