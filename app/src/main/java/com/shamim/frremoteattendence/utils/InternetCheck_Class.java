package com.shamim.frremoteattendence.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.View;

import com.shamim.frremoteattendence.interfaces.InternetCheck;


public class InternetCheck_Class
{
 public static void  openInternetDialog(InternetCheck internetCheck, Boolean trueFalse , Context context)
 {
     if (!isNetworkConnected(context))
     {
         AlertDialog.Builder builder =new AlertDialog.Builder(context);
         builder.setTitle("No Internet Connection");
         builder.setCancelable(false);
         builder.setMessage("Please turn on Internet connection to continue");

         builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 // Add your code to handle the "Retry" button click here
                 if (!trueFalse)
                 {
                     openInternetDialog(internetCheck,false,  context);

                 }
                 dialog.dismiss();
                 internetCheck.onRetry();

             }
         });


// Other dialog configuration code goes here
         builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialogInterface, int i) {
                 dialogInterface.dismiss();
                 Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                 homeIntent.addCategory(Intent.CATEGORY_HOME);
                 homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 context.startActivity(homeIntent);
                 ((Activity) context).finish();
             }
         });

         AlertDialog alertDialog = builder.create();
         alertDialog.show();

         }

 }
 public static Boolean isNetworkConnected(Context context) {
      ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
}



}
