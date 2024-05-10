package com.fde.imageeditlibrary.editimage.utils;

public class Utils {
    public static int ToInt(Object ojb) {
        if (ojb == null) {
            return 0;
        } else {
            return ToDouble(ojb).intValue();
        }
    }

    public static Double ToDouble(Object ojb) {
        if (ojb == null) {
            return 0.0;
        } else {
            return Double.valueOf(ToString(ojb));
        }
    }

    public static String ToString(Object ojb) {
        if (ojb == null) {
            return "";
        } else {
            return String.valueOf(ojb).trim();
        }
    }
}
