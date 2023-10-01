package ru.mikheev.kirill.custombpm.scheme.condition;

import ru.mikheev.kirill.custombpm.scheme.condition.operation.PredicateOperation;

import java.util.Set;

public record ExpressionParsingResult(
        PredicateOperation predicateOperation,
        Set<String> requiredParameters) {
}
