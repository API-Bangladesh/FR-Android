package com.shamim.frremoteattendence.alert

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.widget.Button
import com.shamim.frremoteattendence.R


class CustomDialog_notification(context: Context) : Dialog(context) {
   var close_button:Button?=null
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog_notification)
        close_button=findViewById(R.id.close_button)
        // Set up any interaction or logic for your dialog here
        // For example, you can set text or perform actions on buttons

        // Close the dialog when the close button is clicked
        close_button!!.setOnClickListener {
            dismiss()
        }
    }
}
