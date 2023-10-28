package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import ru.mikheev.kirill.custombpm.scheme.condition.DataType;
import ru.mikheev.kirill.custombpm.scheme.condition.parsing.StringScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConstantArray implements InequalityOperationMember {

    private final DataType arrayValuesDataType;
    private final Object[] array;

    public ConstantArray(DataType arrayValuesDataType, String stringArrayRepresentation) {
        this.arrayValuesDataType = arrayValuesDataType;
        this.array = setStringRepresentationToArray(stringArrayRepresentation);
    }

    public ConstantArray(DataType arrayValuesDataType, Object[] array) {
        this.arrayValuesDataType = arrayValuesDataType;
        this.array = array;
    }

    @Override
    public Object[] getValue(Map<String, Object> dataMap) {
        return array;
    }

    @Override
    public DataType getDataType() {
        return arrayValuesDataType;
    }

    @Override
    public String toString() {
        return "ConstantArray{" + arrayValuesDataType + '(' + Arrays.toString(array) + ")}";
    }

    private Object[] setStringRepresentationToArray(String stringArrayRepresentation) {
        StringScanner stringScanner = new StringScanner(stringArrayRepresentation);
        List<Object> result = new ArrayList<>();
        final StringBuilder elementBuilder = new StringBuilder();
        int apostropheCounter = 0;
        char next;
        while (stringScanner.hasNext()) {
            next = stringScanner.next();
            if (next == ',' && apostropheCounter % 2 == 0) {
                result.add(arrayValuesDataType.valueFromString(elementBuilder.toString()));
                elementBuilder.setLength(0);
                continue;
            }
            if(next == '\'') { // TODO : Добавить возможность экранировать ' в строковых литералах
                apostropheCounter++;
                continue;
            }
            elementBuilder.append(next);
        }
        return result.toArray();
    }
}
