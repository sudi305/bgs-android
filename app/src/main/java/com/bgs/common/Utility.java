package com.bgs.common;

import android.content.ContentResolver;
import android.provider.Settings;
import android.util.Log;

import com.bgs.dheket.App;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by SND on 29/01/2016.
 */
public class Utility {
    NumberFormat formatter;
    String doubleToString;
    double originNumber;

    public Utility(){

    }

    public static String getDeviceUniqueID(ContentResolver contentResolver){
        String device_unique_id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        return device_unique_id;
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            App.applicationHandler.post(runnable);
        } else {
            App.applicationHandler.postDelayed(runnable, delay);
        }
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public String changeFormatNumber(double originNumber){
        formatter = new DecimalFormat("#0.0");
        String doubleToString = String.valueOf(originNumber);
        String numberMod = "", setNumber = "", replace = "";
        double stringToDouble;
        String[] splitDouble = doubleToString.split("\\.");
        if (splitDouble.length!=0){
            numberMod = splitDouble[splitDouble.length-1];
        } else {
            setNumber = String.valueOf(originNumber);
        }
        if (numberMod.length()>3){
            formatter = new DecimalFormat("#0.000");
        } else if (numberMod.length()==2){
            formatter = new DecimalFormat("#0.00");
        } else if (numberMod.length()==1){
            if (numberMod.equals("0")){
                setNumber = String.valueOf(splitDouble[0]);
                return setNumber;
            } else {
                formatter = new DecimalFormat("#0.0");
            }
        }
        setNumber = String.valueOf(formatter.format(originNumber));
        replace = setNumber.replace(",", ".");
        stringToDouble = Double.parseDouble(replace);

        return String.valueOf(stringToDouble);
    }

}
