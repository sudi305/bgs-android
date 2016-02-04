package com.bgs.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.bgs.dheket.R;

/**
 * Created by SND on 21/01/2016.
 */
public class Dialogs extends DialogFragment {
    static int message=0;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dshow = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        if (message==1)dshow.setMessage("cek");
        dshow.setTitle("Alert");
        dshow.setView(inflater.inflate(R.layout.dialogconc, null))

                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        //TODO


                    }
                })
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Dialogs.this.getDialog().cancel();
                    }
                });

        return dshow.create();
    }

    public static Dialogs newInstance(int msg){
        message = msg;
        Dialogs arg = new Dialogs();
        return arg;
    }
}
