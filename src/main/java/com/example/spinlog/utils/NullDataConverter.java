package com.example.spinlog.utils;

import java.util.List;

public class NullDataConverter {
    public static String convertString(String data) {
        return (data != null) ? data : "";
    }

    public static int convertInteger(Integer data) {
        return (data != null) ? data : 0;
    }

    public static float convertFloat(Float data) {
        return (data != null) ? data : 0;
    }

    public static <T> List<T> convertList(List<T> data) {
        return (data != null) ? data : List.of();
    }
}
