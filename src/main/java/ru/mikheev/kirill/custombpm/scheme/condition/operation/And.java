package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import java.util.Map;

public class And extends BinaryLogicalOperation {

    public And(PredicateOperation leftOperand, PredicateOperation rightOperand) {
        super(leftOperand, rightOperand);
    }
    @Override
    protected String getStringRepresentation() {
        return "AND";
    }

    @Override
    public boolean getResultByData(Map<String, Object> dataMap) {
        return leftOperand.getResultByData(dataMap) && rightOperand.getResultByData(dataMap);
    }

    @Override
    public Priority getPriority() {
        return Priority.AND;
    }
}
