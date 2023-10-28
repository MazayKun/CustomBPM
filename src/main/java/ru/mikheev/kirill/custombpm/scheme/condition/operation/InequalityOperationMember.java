package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import ru.mikheev.kirill.custombpm.scheme.condition.DataType;

import java.util.Map;

public interface InequalityOperationMember {

    Object getValue(Map<String, Object> dataMap);

    DataType getDataType();
}
