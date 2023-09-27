package com.shamim.frremoteattendence.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class Encode_and_DecodeBase64Image
{
    public static String encodeBitmapImage(Bitmap bitmap) {
        if (bitmap == null) {
            // Handle the case where the bitmap is null
            Log.e("Encode_and_DecodeBase64Image", "Bitmap is null");
            return null; // Or return an error message or handle as needed
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Log.d("Encode_and_DecodeBase64Image", "byteArrayOutputStream: " + byteArrayOutputStream);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] bytesofImage = byteArrayOutputStream.toByteArray();
        return android.util.Base64.encodeToString(bytesofImage, Base64.DEFAULT);
    }



    public static Bitmap decodebase64Image(String encodebase64)
    {
        byte[] decodedData = Base64.decode(encodebase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedData, 0, decodedData.length);
    }


}
