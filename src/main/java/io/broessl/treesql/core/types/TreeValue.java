package io.broessl.treesql.core.types;

import java.math.BigDecimal;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.broessl.treesql.core.eval.stack.Stackable;

public abstract sealed class TreeValue implements Stackable
        permits TreeContextualPrimitive, TreePrimitive {

    public static TreeNumber parseNumber(String text) {
        return new TreeNumber(new BigDecimal(text));
    }

    public static TreeString parseString(String text) {
        if (text.length() >= 2 && text.startsWith("'") && text.endsWith("'")) {
            String parsedString = text.substring(1, text.length() - 1).replaceAll("''", "''");
            return new TreeString(parsedString);
        }
        throw new IllegalArgumentException("String must be enclosed in single quotes");
    }

    public static TreeBool parseBoolean(String text) {
        if ("TRUE".equals(text))
            return new TreeBool(true);
        if ("FALSE".equals(text))
            return new TreeBool(false);
        throw new IllegalArgumentException("Boolean must be either TRUE or FALSE, got: " + text.toUpperCase());
    }

    public static TreeNull parseNull(String text) {
        if ("NULL".equals(text)) {
            return TreeNull.INSTANCE;
        }
        throw new IllegalArgumentException("Null must be the keyword NULL, got: " + text);
    }

    public static TreeRangedLiteral parseRangedLiteral(String text) {
        return new TreeRangedLiteral(text);
    }

    static ObjectMapper objectMapper = new ObjectMapper();

    public static TreeRangedJSONPointer parseRangedJSONPointer(String jsonText) {
        String value;
        try {
            value = objectMapper.readTree(jsonText).textValue();
            if (value == null) {
                throw new IllegalArgumentException(jsonText + " is not a valid JSON Pointer");
            }
            return new TreeRangedJSONPointer(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(jsonText + " is not a valid JSON Pointer");
        }
    }

    public static TreeValueAt parseValueAt(String text) {
        if (text.startsWith("@")) {
            return new TreeValueAt(text);
        }
        throw new IllegalArgumentException("ValueAt must start with '@', got: " + text);
    }

    public static TreeFullPath parsePathVariable(String text) {
       if (text.toLowerCase(Locale.ENGLISH).equals(text)) {
            return new TreeFullPath(text);
        }
        throw new IllegalArgumentException("Path variable must be all lowercase, got: " + text);
    }

}
