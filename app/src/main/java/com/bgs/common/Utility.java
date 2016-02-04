package com.bgs.common;

import android.util.Log;

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
