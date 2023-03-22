package com.didges.school;

import com.didges.school.auxilary.Validator;

import java.util.*;

import static com.didges.school.auxilary.Formatter.*;
import static com.didges.school.auxilary.Validator.checkInsertedArg;
import static com.didges.school.auxilary.Validator.checkRowForWhere;

public class RequestHandler {
    private final List<Map<String, Object>> collection = new ArrayList<>();
    private final Map<String, Object> rowStructure = new LinkedHashMap<>();

    public RequestHandler() {
        resetRowStructure();
    }

    public List<Map<String, Object>> processRequest(String request) throws Exception {
        String command = request.trim().substring(0, 6);

        return switch (command.toLowerCase()) {
            case "insert" -> processInsert(request);
            case "update" -> processUpdate(request);
            case "delete" -> processDelete(request);
            case "select" -> processSelect(request);
            default -> throw new Exception("The command doesn't exist");
        };
    }

    private void resetRowStructure() {
        rowStructure.put("id", null);
        rowStructure.put("lastname", null);
        rowStructure.put("cost", null);
        rowStructure.put("age", null);
        rowStructure.put("active", null);
    }

    private List<Map<String, Object>> processInsert(String request) throws Exception {
        Map<String, Object> row = new LinkedHashMap<>(rowStructure);

        String[] stringArr = request.split(" (?i)values ");
        String[] argsArr = stringArr[1].split(",");

        updateRow(argsArr, row);

        clearRowFromNulls(row);

        collection.add(row);
        List<Map<String, Object>> insertedCollection = new ArrayList<>();
        insertedCollection.add(row);
        return insertedCollection;
    }

    private List<Map<String, Object>> processUpdate(String request) throws Exception {
        List<Map<String, Object>> changedCollection = new ArrayList<>();
        if (Validator.checkIfWherePresented(request)) {
            String[] requestArr = request.split(" (?i)where ");
            String searchRequest = requestArr[1];
            request = requestArr[0];

            String[] stringArr = request.split(" (?i)values ");
            String[] argsArr = stringArr[1].split(",");

            for (Map<String, Object> rowOfCollection : collection) {
                if (checkRowForWhere(rowOfCollection, searchRequest)) {
                    changedCollection.add(rowOfCollection);
                    updateCollection(argsArr, rowOfCollection);
                }
                clearRowFromNulls(rowOfCollection);
            }
        } else {
            String[] stringArr = request.split(" (?i)values ");
            String[] argsArr = stringArr[1].split(",");

            for (Map<String, Object> rowOfCollection : collection) {
                updateCollection(argsArr, rowOfCollection);
            }

            changedCollection.addAll(collection);
        }
        return changedCollection;
    }

    private void updateCollection(String[] argsArr, Map<String, Object> rowOfCollection) throws Exception {
        Map<String, Object> row = new LinkedHashMap<>(rowStructure);
        updateRow(argsArr, rowOfCollection);
        row.putAll(rowOfCollection);
        clearRowFromNulls(row);
        rowOfCollection.clear();
        rowOfCollection.putAll(row);
    }

    private void updateRow(String[] argsArr, Map<String, Object> rowOfCollection) throws Exception {
        for (String arg : argsArr) {
            String[] keyValue = arg.split("=");

            String key = getKey(keyValue[0]);
            Object value = getValue(keyValue[1]);

            checkInsertedArg(key, value);

            rowOfCollection.put(key, value);
        }
    }

    private List<Map<String, Object>> processDelete(String request) throws Exception {
        List<Map<String, Object>> deletedRows = new ArrayList<>();
        if (Validator.checkIfWherePresented(request)) {
            String[] requestArr = request.split(" (?i)where ");
            String searchRequest = requestArr[1];

            for (Map<String, Object> rowOfCollection : collection) {
                if (checkRowForWhere(rowOfCollection, searchRequest)) {
                    deletedRows.add(rowOfCollection);
                }
            }

            collection.removeAll(deletedRows);
        } else {
            deletedRows.addAll(collection);
            collection.clear();
        }
        return deletedRows;
    }

    private List<Map<String, Object>> processSelect(String request) throws Exception {
        List<Map<String, Object>> resultCollection = new ArrayList<>();

        if (Validator.checkIfWherePresented(request)) {
            String[] requestArr = request.split(" (?i)where ");
            String searchRequest = requestArr[1];

            for (Map<String, Object> rowOfCollection : collection) {
                if (checkRowForWhere(rowOfCollection, searchRequest)) {
                    resultCollection.add(rowOfCollection);
                }
            }
        } else {
            resultCollection = collection;
        }
        return resultCollection;
    }
}
