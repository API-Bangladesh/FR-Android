package com.shamim.frremoteattendence.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class Encode_and_DecodeBase64Image
{
    public static String encodeBitmapImage(Bitmap bitmap) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
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
