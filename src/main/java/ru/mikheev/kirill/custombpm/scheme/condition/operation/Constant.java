package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import ru.mikheev.kirill.custombpm.scheme.condition.DataType;

import java.util.Map;

public class Constant implements InequalityOperationMember {

    private final DataType dataType;
    private final Object value;

    public Constant(DataType dataType, String stringRepresentation) {
        this.value = dataType.valueFromString(stringRepresentation);
        this.dataType = dataType;

    }

    @Override
    public Object getValue(Map<String, Object> dataMap) {
        return value;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }
}
