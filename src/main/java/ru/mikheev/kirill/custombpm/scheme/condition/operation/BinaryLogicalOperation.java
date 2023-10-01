package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import java.util.function.BiFunction;

public abstract class BinaryLogicalOperation implements PredicateOperation {

    protected final PredicateOperation leftOperand;
    protected PredicateOperation rightOperand;

    protected BinaryLogicalOperation(PredicateOperation leftOperand, PredicateOperation rightOperand) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    @Override
    public BinaryLogicalOperation appendBinaryOperation(Priority newOperationPriority,
                                                        BiFunction<PredicateOperation, PredicateOperation, BinaryLogicalOperation> newOperationConstructor,
                                                        PredicateOperation predicateOperation) {
        if (this.getPriority().compareTo(newOperationPriority) >= 0) {
            return newOperationConstructor.apply(this, predicateOperation);
        } else {
            rightOperand = newOperationConstructor.apply(rightOperand, predicateOperation);
            return this;
        }
    }

    protected abstract String getStringRepresentation();

    @Override
    public void toStringTree(StringBuilder result, String spacing) {
        leftOperand.toStringTree(result, spacing + SINGLE_SPACE);
        result.append('\n').append(spacing).append(getStringRepresentation());
        rightOperand.toStringTree(result, spacing + SINGLE_SPACE);
    }
}
