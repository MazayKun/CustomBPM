package ru.mikheev.kirill.custombpm.common;

import java.time.LocalDate;
import java.util.Locale;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum DataType {
    DATE(DataType::compareLocalDate, DataType::toLocalDate, LocalDate.class),
    NUMERIC(DataType::compareNumeric, DataType::toNumeric, Long.class),
    STRING(DataType::compareString, DataType::toString, String.class),
    BOOLEAN(DataType::compareBoolean, DataType::toBoolean, Boolean.class);

    private final BiFunction<Object, Object, Integer> comparator;
    private final Function<String, Object> fromStringConverter;
    private final Class<?> typeClass;

    DataType(
            BiFunction<Object, Object, Integer> comparator,
            Function<String, Object> fromStringConverter,
            Class<?> typeClass) {
        this.comparator = comparator;
        this.fromStringConverter = fromStringConverter;
        this.typeClass = typeClass;
    }

    public Class<?> getTypeClass() {
        return typeClass;
    }

    public static DataType fromTypeCode(String typeCode) {
        try {
            return valueOf(typeCode.toUpperCase(Locale.ROOT));
        } catch (Exception e) {
            throw new RuntimeException("Bad data type code '" + typeCode + '\'');
        }
    }

    private static Integer compareLocalDate(Object leftOperand, Object rightOperand) {
        return ((LocalDate) leftOperand).compareTo((LocalDate) rightOperand);
    }

    private static Integer compareNumeric(Object leftOperand, Object rightOperand) {
        return ((Long) leftOperand).compareTo((Long) rightOperand);
    }

    private static Integer compareString(Object leftOperand, Object rightOperand) {
        return ((String) leftOperand).compareTo((String) rightOperand);
    }

    private static Integer compareBoolean(Object leftOperand, Object rightOperand) {
        return ((Boolean) leftOperand).compareTo((Boolean) rightOperand);
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

    public int compare(Object leftMember, Object rightMember) {
        try {
            return comparator.apply(leftMember, rightMember);
        } catch (Exception e) {
            throw new RuntimeException("Conflict data types during comparison memebers with type " + this.name() +
                    '(' + leftMember + ", " + rightMember + ')', e);
        }
    }

    public Object valueFromString(String stringValue) {
        try {
            return fromStringConverter.apply(stringValue);
        } catch (Exception e) {
            throw new RuntimeException("Error during conversion String to type " + this.name() + '(' + stringValue + ')', e);
        }
    }
}
