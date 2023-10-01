package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import java.util.function.BiFunction;

public abstract class UnaryLogicalOperation implements PredicateOperation {

    protected final PredicateOperation innerOperand;

    protected UnaryLogicalOperation(PredicateOperation predicateOperation) {
        this.innerOperand = predicateOperation;
    }

    @Override
    public BinaryLogicalOperation appendBinaryOperation(
            Priority newOperationPriority,
            BiFunction<PredicateOperation, PredicateOperation, BinaryLogicalOperation> newOperationConstructor,
            PredicateOperation predicateOperation) {
        return newOperationConstructor.apply(this, predicateOperation);
    }
}
