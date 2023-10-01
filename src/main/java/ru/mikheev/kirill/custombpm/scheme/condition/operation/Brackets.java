package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import java.util.Map;

public class Brackets extends UnaryLogicalOperation {

    public Brackets(PredicateOperation innerOperand) {
        super(innerOperand);
    }

    @Override
    public boolean getResultByData(Map<String, Object> dataMap) {
        return innerOperand.getResultByData(dataMap);
    }

    @Override
    public Priority getPriority() {
        return Priority.UNARY_LOGICAL;
    }

    @Override
    public void toStringTree(StringBuilder result, String spacing) {
        result.append('\n').append(spacing).append('(');
        innerOperand.toStringTree(result, spacing + SINGLE_SPACE);
        result.append('\n').append(spacing).append(')');
    }
}
