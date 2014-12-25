package com.namnv.project.kidpaint.object;

import android.graphics.Color;

import com.namnv.project.kidpaint.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 8psman on 10/29/2014.
 */
public class ToolBox {

    public static int[] thicknessDrawable = new int[]{
            R.drawable.ic_tool_thick_1,
            R.drawable.ic_tool_thick_2,
            R.drawable.ic_tool_thick_3,
            R.drawable.ic_tool_thick_4,
            R.drawable.ic_tool_thick_5,
    };

    public static int[] defaultDrawColor = new int[]{
            R.color.df_draw_color_1,
            R.color.df_draw_color_2,
            R.color.df_draw_color_3,
            R.color.df_draw_color_4,
            R.color.df_draw_color_5,
            R.color.df_draw_color_6,
            R.color.df_draw_color_7,
            R.color.df_draw_color_8,
    };

    public static int[] eraserSizeDrawable = new int[]{
            R.drawable.ic_tool_eraser_size_1,
            R.drawable.ic_tool_eraser_size_2,
            R.drawable.ic_tool_eraser_size_3,
            R.drawable.ic_tool_eraser_size_4,
            R.drawable.ic_tool_eraser_size_5,
    };

    List<PaintTool> toolList = new ArrayList<PaintTool>();

    public HandTool    handTool;
    public PencilTool  pencilTool;
    public EraserTool  eraserTool;
    public BucketTool   bucketTool;
    public ShapeTool    shapeTool;

    public int currentColor =Color.BLACK;

    public ToolBox(){
        recentColors.add(Color.BLACK);
    }


    public List<Integer> recentColors = new ArrayList<Integer>();
}
