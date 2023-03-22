package com.didges.school.auxilary;

import java.util.Collections;
import java.util.Map;

public class Formatter {
    public static Object getValue(String value) { //checking type and converting value
        if (Validator.isNumeric(value)) {
            value = value.trim();
            if (value.contains(".")) {
                return Double.parseDouble(value);
            } else {
                return Long.parseLong(value);
            }
        } else {
            if (value.contains("null")) {
                return null;
            } else if (value.matches("(?i)true") || value.matches("(?i)false")) {
                return Boolean.parseBoolean(value);
            } else {
                return "'" + value.replaceAll("[‘’']", "").strip() + "'";
            }
        }
    }

    public static String getKey(String key) {
        return "'" + key.replaceAll("[‘’']", "").strip().toLowerCase() + "'";
    }

    public static void clearRowFromNulls(Map<String, Object> row) {
        row.values().removeAll(Collections.singleton(null));
    }
}
