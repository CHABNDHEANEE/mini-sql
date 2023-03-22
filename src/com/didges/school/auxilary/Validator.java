package com.didges.school.auxilary;

import com.didges.school.ComparisonOperator;

import java.util.HashMap;
import java.util.Map;

import static com.didges.school.auxilary.Formatter.getKey;
import static com.didges.school.auxilary.Formatter.getValue;

public class Validator {
    private static final Map<String, Class> availableTypesAndArg = new HashMap<>();

    public Validator() {

    }

    public static void checkInsertedArg(String key, Object value) throws Exception {
        key = key.toLowerCase();
        fillAvailableTypesMap();
            if (!availableTypesAndArg.containsKey(key)) {
                throw new Exception();
            } else if (!availableTypesAndArg.get(key).equals(value.getClass())) {
                throw new Exception();
            }
    }

    private static void fillAvailableTypesMap() {
        availableTypesAndArg.put("'id'", Long.class);
        availableTypesAndArg.put("'lastname'", String.class);
        availableTypesAndArg.put("'age'", Long.class);
        availableTypesAndArg.put("'cost'", Double.class);
        availableTypesAndArg.put("'active'", Boolean.class);
    }

    public static boolean checkRowForWhere(Map<String, Object> row, String statement) throws Exception {
        boolean equal;
        if (statement.matches(".*(?i)and.*")) {
            String[] statementArr = statement.split(" (?i)and ");
            equal = checkRowForWhere(row, statementArr[0]) && checkRowForWhere(row, statementArr[1]);
        } else if (statement.matches(".*(?i)or.*")) {
            String[] statementArr = statement.split(" (?i)or ");
            equal = checkRowForWhere(row, statementArr[0]) || checkRowForWhere(row, statementArr[1]);
        } else {
            return checkWhereStatement(row, statement);
        }

        return equal;
    }

    public static boolean checkWhereStatement(Map<String, Object> row, String statement) throws Exception {
        String[] keyValue;
        String key;
        Object value;
        ComparisonOperator comparisonOperator = getComparisonOperator(statement);

        if (statement.contains("null")) {
            throw new Exception();
        }

        try {
            switch (comparisonOperator) {
                case LESSER:
                    keyValue = statement.split("<");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    if (value.getClass() == Long.class) {
                        return ((Number)value).longValue() > ((Number)row.get(key)).longValue();
                    } else if (value.getClass() == Double.class) {
                        return ((Number)value).doubleValue() > ((Number)row.get(key)).doubleValue();
                    }
                case BIGGER:
                    keyValue = statement.split(">");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    if (value.getClass() == Long.class) {
                        return ((Number)value).longValue() < ((Number)row.get(key)).longValue();
                    } else if (value.getClass() == Double.class) {
                        return ((Number)value).doubleValue() < ((Number)row.get(key)).doubleValue();
                    }
                case EQUALS:
                    keyValue = statement.split("=");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    return row.get(key).equals(value);
                case LESSER_EQUALS:
                    keyValue = statement.split("<=");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    if (value.getClass() == Long.class) {
                        return ((Number)value).longValue() >= ((Number)row.get(key)).longValue();
                    } else if (value.getClass() == Double.class) {
                        return ((Number)value).doubleValue() >= ((Number)row.get(key)).doubleValue();
                    }
                case BIGGER_EQUALS:
                    keyValue = statement.split(">=");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);

                    if (value.getClass() == Long.class) {
                        return ((Number)value).longValue() <= ((Number)row.get(key)).longValue();
                    } else if (value.getClass() == Double.class) {
                        return ((Number)value).doubleValue() <= ((Number)row.get(key)).doubleValue();
                    }

                case NOT_EQUALS:
                    keyValue = statement.split("!=");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    if (row.get(key) == null) {
                        return true;
                    }

                    return !row.get(key).equals(value);
                case LIKE:
                    keyValue = statement.split("like");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    return String.valueOf(row.get(key)).contains(String.format(String.valueOf(value)));
                case LIKE_RIGISTER_INSENSITIVE:
                    keyValue = statement.split("ilike");
                    key = getKey(keyValue[0]);
                    value = getValue(keyValue[1]);
                    checkDataType(comparisonOperator, row, key, value);
                    return String.valueOf(row.get(key)).toLowerCase().matches(String.valueOf(value).replaceAll("%", ".*"));
            }
        } catch (NullPointerException e) {
            return false;
        }

        return false;
    }

    public static void checkDataType(ComparisonOperator comparisonOperator, Map<String, Object> row, String key, Object value) throws Exception {
        if (row.containsKey(key)) {
            if (!row.get(key).getClass().equals(value.getClass())) {
                throw new Exception();
            }
        }

        switch (comparisonOperator) {
            case LIKE, LIKE_RIGISTER_INSENSITIVE -> {
                if (!value.getClass().equals(String.class)) {
                    throw new Exception();
                }
            }
            case BIGGER, LESSER, BIGGER_EQUALS, LESSER_EQUALS -> {
                if (!(value.getClass().equals(Long.class) || value.getClass().equals(Double.class))) {
                    throw new Exception();
                }
            }
        }
    }

    private static ComparisonOperator getComparisonOperator(String command) {
        if (command.matches(".*(?i)ilike.*")) {
            return ComparisonOperator.LIKE_RIGISTER_INSENSITIVE;
        } else if (command.matches(".*(?i)like.*")) {
            return ComparisonOperator.LIKE;
        } else if (command.contains("!=")) {
            return ComparisonOperator.NOT_EQUALS;
        } else if (command.contains(">=")) {
            return ComparisonOperator.BIGGER_EQUALS;
        } else if (command.contains("<=")) {
            return ComparisonOperator.LESSER_EQUALS;
        } else if (command.contains("=")) {
            return ComparisonOperator.EQUALS;
        } else if (command.contains(">")) {
            return ComparisonOperator.BIGGER;
        } else {
            return ComparisonOperator.LESSER;
        }
    }

    public static boolean isNumeric(String str) {
        return str.trim().matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    public static boolean checkIfWherePresented(String str) {
        return str.toLowerCase().contains("where");
    }
}
