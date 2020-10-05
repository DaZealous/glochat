package net.glochat.dev.utils;

import java.text.DecimalFormat;


public class NumUtils {


    public static String numberFilter(int number) {
        if (number > 9999 && number <= 999999) {
            DecimalFormat df2 = new DecimalFormat("##.#");
            String format = df2.format((float)number / 10000);
            return format;
        } else if (number > 999999 && number < 99999999) {
            return number / 10000 + "";
        } else if (number > 99999999) {
            DecimalFormat df2 = new DecimalFormat("##.#");
            String format = df2.format((float)number / 100000000);
            return format;
        } else {
            return number + "";
        }
    }
}
