package ru.mikheev.kirill.custombpm.scheme.condition.operation;

import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

public class Inequality implements PredicateOperation {

    private final InequalityOperationMember leftOperand;
    private final InequalityOperationMember rightOperand;
    private final InequalityCalculator resultCalculator;
    private final String operationRepresentation;

    private Inequality(
            InequalityOperationMember leftOperand,
            InequalityOperationMember rightOperand,
            InequalityCalculator resultCalculator,
            String operationRepresentation) {
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.resultCalculator = resultCalculator;
        this.operationRepresentation = operationRepresentation;
    }

    public static Inequality createInequalityByType(
            String operation,
            InequalityOperationMember leftOperand,
            InequalityOperationMember rightOperand
    ) {
        switch (operation) {
            case ">":
                return new Inequality(leftOperand, rightOperand, Inequality::greaterThan, operation);
            case "<":
                return new Inequality(leftOperand, rightOperand, Inequality::lessThan, operation);
            case "=", "==":
                return new Inequality(leftOperand, rightOperand, Inequality::equal, operation);
            case ">=":
                return new Inequality(leftOperand, rightOperand, Inequality::greaterThanOrEqual, operation);
            case "<=":
                return new Inequality(leftOperand, rightOperand, Inequality::lessThanOrEqual, operation);
            case "!=":
                return new Inequality(leftOperand, rightOperand, Inequality::notEqual, operation);
            default: {
                String operationInUpperCase = operation.toUpperCase(Locale.ROOT);
                if ("IN".equals(operationInUpperCase)) {
                    return new Inequality(leftOperand, rightOperand, Inequality::in, "IN");
                }
                StringBuilder operationRepresentationBuilder = new StringBuilder();
                for (char currChar : operationInUpperCase.toCharArray()) {
                    if (currChar != ' ') operationRepresentationBuilder.append(currChar);
                }
                if ("NOTIN".equals(operationRepresentationBuilder.toString())) {
                    return new Inequality(leftOperand, rightOperand, Inequality::notIn, "NOT IN");
                }
                throw new RuntimeException("Неподдерживаемая операция");
            }
        }
    }

    private static int compare(
            InequalityOperationMember leftOperand,
            InequalityOperationMember rightOperand,
            Map<String, Object> dataMap) {
        return leftOperand.getDataType().compare(
                leftOperand.getValue(dataMap),
                rightOperand.getValue(dataMap)
        );
    }

    private static boolean lessThan(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return compare(leftOperand, rightOperand, dataMap) < 0;
    }

    private static boolean greaterThan(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return compare(leftOperand, rightOperand, dataMap) > 0;
    }

    private static boolean lessThanOrEqual(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return compare(leftOperand, rightOperand, dataMap) <= 0;
    }

    private static boolean greaterThanOrEqual(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return compare(leftOperand, rightOperand, dataMap) >= 0;
    }

    private static boolean equal(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return compare(leftOperand, rightOperand, dataMap) == 0;
    }

    private static boolean notEqual(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return compare(leftOperand, rightOperand, dataMap) != 0;
    }

    private static boolean in(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        if (rightOperand instanceof ConstantArray array) {
            Object modelValue = leftOperand.getValue(dataMap);
            for (Object arrayElement : array.getValue(dataMap)) {
                if (array.getDataType().compare(modelValue, arrayElement) == 0) {
                    return true;
                }
            }
            return false;

        }
        throw new RuntimeException("IN");
    }

    private static boolean notIn(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap) {
        return !in(leftOperand, rightOperand, dataMap);
    }

    @Override
    public boolean getResultByData(Map<String, Object> dataMap) {
        return resultCalculator.calculate(leftOperand, rightOperand, dataMap);
    }

    @Override
    public Priority getPriority() {
        return Priority.INEQUALITY;
    }

    @Override
    public BinaryLogicalOperation appendBinaryOperation(
            Priority newOperationPriority,
            BiFunction<PredicateOperation, PredicateOperation, BinaryLogicalOperation> newOperationConstructor,
            PredicateOperation predicateOperation) {
        return newOperationConstructor.apply(this, predicateOperation);
    }

    @Override
    public void toStringTree(StringBuilder result, String spacing) {
        result
                .append('\n').append(spacing).append(SINGLE_SPACE).append(leftOperand)
                .append('\n').append(spacing).append(operationRepresentation)
                .append('\n').append(spacing).append(SINGLE_SPACE).append(rightOperand);
    }

    @FunctionalInterface
    private interface InequalityCalculator {
        boolean calculate(InequalityOperationMember leftOperand, InequalityOperationMember rightOperand, Map<String, Object> dataMap);
    }
}
