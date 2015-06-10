package com.treefrogapps.TaDo;


import android.content.Context;
import android.widget.Toast;

public class CustomToasts {

    public static void Toast(Context context, String toastMessage){

        Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show();
    }
}
