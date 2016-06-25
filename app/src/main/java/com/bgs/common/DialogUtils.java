package com.bgs.common;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.bgs.dheket.R;

/**
 * Created by zhufre on 6/23/2016.
 */
public class DialogUtils {
    public static Dialog LoadingSpinner(Context mContext){
        Dialog pd = new Dialog(mContext, android.R.style.Theme_Black);
        View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_dialog, null);
        pd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //pd.getWindow().setBackgroundDrawableResource(R.color.transparent);
        pd.setContentView(view);
        return pd;
    }
}
