package com.shamim.frremoteattendence.alert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shamim.frremoteattendence.R;


public class CustomDialog
{
    private AlertDialog isDialog;

    private final Activity mActivity;


    public CustomDialog(Activity activity)
    {
        mActivity = activity;
    }

    public void startLoading(String textTitle) {
        // Set View
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_layout, null);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textView= dialogView.findViewById(R.id.dialog_textView);
        textView.setText(textTitle);
                // Set Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(dialogView);
        builder.setCancelable(false);
        isDialog = builder.create();
        if (!mActivity.isFinishing())
        {
            isDialog.show();

        }
    }

    public void dismiss() {
        if (isDialog != null && isDialog.isShowing()) {
            isDialog.dismiss();
        }
    }


}
