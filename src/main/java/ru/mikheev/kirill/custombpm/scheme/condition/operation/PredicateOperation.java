package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import java.util.Map;
import java.util.function.BiFunction;

public interface PredicateOperation {

    String SINGLE_SPACE = ". ";

    boolean getResultByData(Map<String, Object> dataMap);

    Priority getPriority();

    BinaryLogicalOperation appendBinaryOperation(Priority newOperationPriority,
                                                 BiFunction<PredicateOperation, PredicateOperation, BinaryLogicalOperation> newOperationConstructor,
                                                 PredicateOperation predicateOperation);

    void toStringTree(StringBuilder result, String spacing);

    enum Priority {
        OR,
        AND,
        UNARY_LOGICAL,
        INEQUALITY;

        public static Priority getPriorityByOperationName(String logicalOperationName) {
            return switch (logicalOperationName) {
                case "&&" -> AND;
                case "||" -> OR;
                default -> throw new RuntimeException("Unsupported operation with name " + logicalOperationName);
            };
        }
    }
}
