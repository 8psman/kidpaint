package com.namnv.project.kidpaint.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.namnv.project.kidpaint.Application;
import com.namnv.project.kidpaint.R;
import com.namnv.project.kidpaint.object.PaintReference;

import java.util.List;

/**
 * Created by 8psman on 10/30/2014.
 */
public class DialogFactory {

    public static AlertDialog createLoadingDialog(Context context, String message){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_loading, null);
        // custom view
        View container = view.findViewById(R.id.dialog_container);
        container.setBackgroundResource(R.color.theme_default);
        TextView tvMessage = (TextView) view.findViewById(R.id.loading_message);
        if (message != null)
            tvMessage.setText(message);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .setCancelable(false)
                .create();
        return alertDialog;
    }

    public static AlertDialog createMessageDialog(Context context, String message){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_message, null);
        // custom view
        View container = view.findViewById(R.id.dialog_container);
        container.setBackgroundResource(R.color.theme_default);
        TextView tvMessage = (TextView) view.findViewById(R.id.dialog_message);
        tvMessage.setText(message);
        Button ok = (Button) view.findViewById(R.id.dialog_ok);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    public static AlertDialog createRenameDialog(Context context, final PaintReference ref, final Runnable onSuccess){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_rename, null);
        // custom view
        View container = view.findViewById(R.id.dialog_rename_container);
        final TextView message = (TextView) view.findViewById(R.id.message);
        final EditText name = (EditText)view.findViewById(R.id.rename_new_name);
        Button ok = (Button) view.findViewById(R.id.rename_paint_ok);
        Button cancel = (Button) view.findViewById(R.id.rename_paint_cancel);
        container.setBackgroundResource(R.color.theme_default);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        // set listener
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (name.getText().length() == 0){
                    message.setText("New name is missing!");
                }else{
                    String newName = name.getText().toString();
                    if (Application.isPaintExists(newName)){
                        message.setText("New name is already exists!");
                    }else{
                        boolean result = Application.renamePaint(ref, name.getText().toString());
                        if (!result){
                            message.setText("Cannot rename paint!");
                        }else{
                            ref.name = Application.getFullNameForPaint(newName);
                            ref.path = Application.getFilePathForPaint(newName);
                            alertDialog.dismiss();
                            onSuccess.run();
                        }
                    }
                }
            }
        });
        return alertDialog;
    }

    public static AlertDialog createDeleteDialog(Context context, final Runnable onOK){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_delete, null);
        // custom view
        View container = view.findViewById(R.id.dialog_rename_container);
        container.setBackgroundResource(R.color.theme_default);

        Button ok = (Button) view.findViewById(R.id.delete_paint_ok);
        Button cancel = (Button) view.findViewById(R.id.delete_paint_cancel);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        // set listener
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onOK.run();
            }
        });
        return alertDialog;
    }

    public static AlertDialog createSavedDialog(Context context, final Runnable onHome, final Runnable onShare, final Runnable onNew){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_saved, null);
        // custom view
        View container = view.findViewById(R.id.dialog_container);
        container.setBackgroundResource(R.color.theme_default);

        Button home = (Button) view.findViewById(R.id.dialog_action_home);
        Button share = (Button) view.findViewById(R.id.dialog_action_share);
        Button edit = (Button) view.findViewById(R.id.dialog_action_edit);
        Button btNew = (Button) view.findViewById(R.id.dialog_action_new);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onHome.run();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onShare.run();
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onNew.run();
            }
        });
        return alertDialog;
    }

    public static AlertDialog createCancelDialog(Context context, boolean isEdited, final Runnable onHome, final Runnable onNew){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_cancel, null);
        // custom view
        View container = view.findViewById(R.id.dialog_container);
        container.setBackgroundResource(R.color.theme_default);

        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        Button cancel = (Button) view.findViewById(R.id.dialog_action_cancel);
        Button home = (Button) view.findViewById(R.id.dialog_action_home);
        Button btNew = (Button) view.findViewById(R.id.dialog_action_new);

        if (isEdited){
            message.setText(R.string.cancel_message_unsaved);
        }else{
            message.setText(R.string.cancel_message_saved);
        }

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onHome.run();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        btNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onNew.run();
            }
        });
        return alertDialog;
    }

    public static AlertDialog createRequestDialog(Context context, String title, String message, final Runnable onOK){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_request, null);
        // custom view
        View container = view.findViewById(R.id.dialog_rename_container);
        container.setBackgroundResource(R.color.theme_default);

        Button ok = (Button) view.findViewById(R.id.dialog_request_ok);
        Button cancel = (Button) view.findViewById(R.id.dialog_request_cancel);
        TextView tvTitle = (TextView) view.findViewById(R.id.dialog_request_title);
        tvTitle.setText(title);
        TextView tvMessage = (TextView) view.findViewById(R.id.dialog_request_message);
        tvMessage.setText(message);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        // set listener
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                onOK.run();
            }
        });
        return alertDialog;
    }

    public static AlertDialog createColorChooserDialog(Context context, List<Integer> recentColors, final ColorChosenListener choosenListener){
        // inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_color_chooser, null);
        // custom view
        View container = view.findViewById(R.id.dialog_container);
        container.setBackgroundResource(R.color.theme_default);

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(null)
                .setView(view)
                .create();

        LinearLayout recentWrapper = (LinearLayout) view.findViewById(R.id.recent_color_list_wrapper);
        LinearLayout defaultWrapper = (LinearLayout) view.findViewById(R.id.default_color_list_wrapper);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                context.getResources().getDimensionPixelSize(R.dimen.color_button_size),
                context.getResources().getDimensionPixelSize(R.dimen.color_button_size)
        );
        int marin = context.getResources().getDimensionPixelSize(R.dimen.color_button_margin);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                int color = (Integer)view.getTag();
                choosenListener.onChooseColor(color);
            }
        };
        params.setMargins(marin, marin, marin, marin);
        for (int i=0; i<recentColors.size(); i++){
            Button button = new Button(context);
            button.setLayoutParams(params);
            button.setBackgroundColor(recentColors.get(i));
            button.setOnClickListener(listener);
            button.setTag(recentColors.get(i));
            recentWrapper.addView(button);
        }

        int[] dfColors = context.getResources().getIntArray(R.array.df_draw_color);
        for (int i=0; i<dfColors.length; i++){
            Button button = new Button(context);
            button.setLayoutParams(params);
            button.setBackgroundColor(dfColors[i]);
            button.setOnClickListener(listener);
            button.setTag(dfColors[i]);
            defaultWrapper.addView(button);
        }

        Button pickColor = (Button) view.findViewById(R.id.pick_color);
        pickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                choosenListener.onPickColor();
            }
        });
        return alertDialog;
    }

    public interface ColorChosenListener{
        void onChooseColor(int color);
        void onPickColor();
    }
}
