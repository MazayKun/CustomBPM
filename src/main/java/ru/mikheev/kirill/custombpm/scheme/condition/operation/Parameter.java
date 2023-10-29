package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import ru.mikheev.kirill.custombpm.common.DataType;

import java.util.Map;

import static java.util.Objects.isNull;

public class Parameter implements InequalityOperationMember {

    private final DataType dataType;
    private final String parameterName;

    public Parameter(DataType dataType, String parameterName) {
        this.dataType = dataType;
        this.parameterName = parameterName;
    }

    @Override
    public Object getValue(Map<String, Object> dataMap) {
        Object parameterValue = dataMap.get(parameterName);
        if (isNull(parameterValue)) {
            throw new RuntimeException("Не найден необходимый параметр для логического выражения " + parameterName);
        }
        return parameterValue;
    }

    @Override
    public DataType getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return "Parameter{" + parameterName + '}';
    }
}
