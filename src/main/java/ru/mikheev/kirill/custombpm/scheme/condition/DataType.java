package ru.mikheev.kirill.custombpm.scheme.condition;

import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum DataType {
    DATE(DataType::compareLocalDate, DataType::toLocalDate),
    NUMERIC(DataType::compareNumeric, DataType::toNumeric),
    STRING(DataType::compareString, DataType::toString),
    BOOLEAN(DataType::compareBoolean, DataType::toBoolean);

    private final BiFunction<Object, Object, Integer> comparator;
    private final Function<String, Object> fromStringConverter;

    DataType(BiFunction<Object, Object, Integer> comparator, Function<String, Object> fromStringConverter) {
        this.comparator = comparator;
        this.fromStringConverter = fromStringConverter;
    }

    public int compare(Object leftMember, Object rightMember) {
        try {
            return comparator.apply(leftMember, rightMember);
        } catch (Exception e) {
            throw new RuntimeException("Conflict data types during comparison memebers with type " + this.name() +
                    '(' + leftMember + ", " + rightMember + ')', e);
        }
    }

    public Object fromString(String stringValue) {
        try {
            return fromStringConverter.apply(stringValue);
        } catch (Exception e) {
            throw new RuntimeException("Error during conversion String to type " + this.name() + '(' + stringValue + ')', e);
        }
    }

    private static Integer compareLocalDate(Object leftOperand, Object rightOperand) {
        return ((LocalDate)leftOperand).compareTo((LocalDate)rightOperand);
    }

    private static Integer compareNumeric(Object leftOperand, Object rightOperand) {
        return ((Long)leftOperand).compareTo((Long)rightOperand);
    }

    private static Integer compareString(Object leftOperand, Object rightOperand) {
        return ((String)leftOperand).compareTo((String)rightOperand);
    }

    private static Integer compareBoolean(Object leftOperand, Object rightOperand) {
        return ((Boolean)leftOperand).compareTo((Boolean)rightOperand);
    }

    private static Object toLocalDate(String stringValue) {
        return LocalDate.parse(stringValue);
    }

    private static Object toNumeric(String stringValue) {
        return Long.valueOf(stringValue);
    }

    private static Object toString(String stringValue) {
        return stringValue;
    }

    private static Object toBoolean(String stringValue) {
        return Boolean.parseBoolean(stringValue);
    }
}