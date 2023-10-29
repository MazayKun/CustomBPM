package ru.mikheev.kirill.custombpm.scheme.condition;

import org.springframework.cglib.core.internal.Function;
import ru.mikheev.kirill.custombpm.scheme.condition.operation.*;
import ru.mikheev.kirill.custombpm.scheme.condition.parsing.Token;
import ru.mikheev.kirill.custombpm.scheme.condition.parsing.Tokenizer;
import ru.mikheev.kirill.custombpm.scheme.general.TaskParameter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class ExpressionBuilder {

    private final Map<String, DataType> parametersWithTypes;
    private final Set<String> requiredParameters = new HashSet<>();
    private final Tokenizer tokenizer;

    private Token currentToken;

    public ExpressionBuilder(String expressionToParse, Set<TaskParameter> schemeParameters) {
        if (isBlank(expressionToParse)) {
            throw new RuntimeException("Expression string is empty");
        }
        this.parametersWithTypes = schemeParameters.stream()
                .collect(Collectors.toMap(TaskParameter::getInnerName, this::getParameterDataType));
        this.tokenizer = new Tokenizer(expressionToParse);
    }

    public ExpressionParsingResult build() {
        if (!tokenizer.hasNext()) throw new RuntimeException("Expression already created");
        PredicateOperation operationRepresentation = buildNewLevelOfExpression(false);
        return new ExpressionParsingResult(operationRepresentation, requiredParameters);
    }

    private DataType getParameterDataType(TaskParameter parameter) {
        return DataType.fromTypeCode(parameter.getType());
    }

    //TODO : Добавить логические выражения, состоящие только из констант (true, 1 ...)
    private PredicateOperation buildNewLevelOfExpression(boolean closingBracketIsRequired) {
        PredicateOperation currentOperation = null;

        while (tokenizer.hasNext()) {
            currentToken = tokenizer.next();
            switch (currentToken.getType()) {
                case OPEN_BRACKET -> currentOperation = new Brackets(buildNewLevelOfExpression(true));
                case CLOSE_BRACKET -> {
                    if (!closingBracketIsRequired) {
                        throw validationError("Неправильная скобочная последжовательность");
                    }
                    return currentOperation;
                }
                case PARAMETER, CONSTANT -> {
                    if (currentOperation == null) {
                        currentOperation = fetchInequality();
                        break;
                    }
                    throw validationError("Ошибка при построении условий");
                }
                case BINARY_LOGICAL_OPERATION -> {
                    if (currentOperation == null) throw validationError("Ошибка при построенни логического выражения");
                    String operationName = tokenizer.getTokenRepresentation(currentToken);
                    PredicateOperation rightOperand = fetchOperand();
                    currentOperation = currentOperation.appendBinaryOperation(
                            PredicateOperation.Priority.getPriorityByOperationName(operationName),
                            getBinaryLogicalOperationConstructorByName(operationName),
                            rightOperand
                    );
                }
                case UNARY_LOGICAL_OPERATION -> {
                    var operationConstructor = getUnaryLogicalOperationConstructorByName(tokenizer.getTokenRepresentation(currentToken));
                    currentToken = tokenizer.next();
                    PredicateOperation innerRepresentation;
                    if (currentToken.getType() == Token.Type.OPEN_BRACKET) {
                        innerRepresentation = new Brackets(buildNewLevelOfExpression(true));
                    } else {
                        innerRepresentation = fetchInequality();
                    }
                    currentOperation = operationConstructor.apply(innerRepresentation);
                }
                case INEQUALITY_OPERATION -> throw validationError("Неправильбное использование операций сравнения");
                default -> throw validationError("Неизвестный токен");
            }

        }
        return currentOperation;
    }

    private PredicateOperation fetchOperand() {
        if (!tokenizer.hasNext()) throw validationError("не найден левый операнд для логической операции");
        currentToken = tokenizer.next();
        return switch (currentToken.getType()) {
            case PARAMETER, CONSTANT -> fetchInequality();
            case UNARY_LOGICAL_OPERATION -> {
                var operationConstructor = getUnaryLogicalOperationConstructorByName(tokenizer.getTokenRepresentation(currentToken));
                currentToken = tokenizer.next();
                if (currentToken.getType() == Token.Type.OPEN_BRACKET) {
                    yield operationConstructor.apply(new Brackets(buildNewLevelOfExpression(true)));
                }
                yield operationConstructor.apply(fetchInequality());
            }
            case OPEN_BRACKET -> new Brackets(buildNewLevelOfExpression(true));
            default -> throw validationError("Невозможно определить неравенство");
        };
    }

    private PredicateOperation fetchInequality() {
        Token leftOperandToken = currentToken;

        if (!tokenizer.hasNext())
            throw validationError("Не удалось построить неравенство, после первого параметра нет оператора");

        Token operationToken = tokenizer.next();
        if (operationToken.getType() != Token.Type.INEQUALITY_OPERATION)
            throw validationError("Не удалсоь построить неравенство, после первого параметра идет неподходящий токен");

        if (!tokenizer.hasNext())
            throw validationError("Не удалось построить неравенство, после оператора не последовало второго операнда");

        currentToken = tokenizer.next();
        Inequality constructedInequality;
        if (leftOperandToken.getType() == Token.Type.PARAMETER) {
            String parameterName = tokenizer.getTokenRepresentation(leftOperandToken);
            DataType parameterType = parametersWithTypes.get(parameterName);
            if (isNull(parameterType)) throw validationError("Не найден тип параметра в изначальной схеме");
            Parameter leftOperand = new Parameter(parameterType, parameterName);
            requiredParameters.add(parameterName);
            InequalityOperationMember rightOperand = convertTokenToOperationMember(currentToken, parameterType);
            constructedInequality = Inequality.createInequalityByType(tokenizer.getTokenRepresentation(operationToken), leftOperand, rightOperand);
        } else {
            if (currentToken.getType() != Token.Type.PARAMETER)
                throw validationError("В паре из двух операндов не хватает параметра, который бы позволил определить тип данных");
            String parameterName = tokenizer.getTokenRepresentation(currentToken);
            DataType parameterType = parametersWithTypes.get(parameterName);
            if (isNull(parameterType)) throw validationError("Не найден тип параметра в изначальной схеме");
            Parameter rightOperand = new Parameter(parameterType, parameterName);
            requiredParameters.add(parameterName);
            InequalityOperationMember leftOperand = convertTokenToOperationMember(leftOperandToken, parameterType);
            constructedInequality = Inequality.createInequalityByType(tokenizer.getTokenRepresentation(operationToken), leftOperand, rightOperand);
        }
        if (tokenizer.hasNext() && tokenizer.peekToken().getType() == Token.Type.INEQUALITY_OPERATION) {
            return new And(constructedInequality, fetchInequality());
        }
        return constructedInequality;
    }

    private InequalityOperationMember convertTokenToOperationMember(Token token, DataType dataType) {
        return switch (token.getType()) {
            case CONSTANT -> new Constant(dataType, tokenizer.getTokenRepresentation(token));
            case PARAMETER -> {
                String parameterName = tokenizer.getTokenRepresentation(token);
                requiredParameters.add(parameterName);
                yield new Parameter(dataType, parameterName);
            }
            case OPEN_BRACKET -> {
                List<Object> arrayContent = new ArrayList<>();
                Token lastToken;
                do {
                    if (!tokenizer.hasNext()) throw validationError("Неправильное построение массива констант");
                    lastToken = tokenizer.next();
                    if (lastToken.getType() != Token.Type.CONSTANT)
                        throw validationError("Неправильное построение массива констант");
                    arrayContent.add(dataType.valueFromString(tokenizer.getTokenRepresentation(lastToken)));
                } while (tokenizer.hasNext() && (lastToken = tokenizer.next()).getType() == Token.Type.COMMA);
                if (lastToken.getType() != Token.Type.CLOSE_BRACKET)
                    throw validationError("Неправильное построение массива констант");
                yield new ConstantArray(dataType, arrayContent.toArray());
            }
            default -> throw validationError("Неправильный токен для операнда");
        };
    }

    private BiFunction<PredicateOperation, PredicateOperation, BinaryLogicalOperation> getBinaryLogicalOperationConstructorByName(
            String binaryLogicalOperationName
    ) {
        if ("&&".equals(binaryLogicalOperationName)) {
            return And::new;
        }
        if ("||".equals(binaryLogicalOperationName)) {
            return Or::new;
        }
        throw new RuntimeException("Неизвестная бинарная логическая операция с именем {" + binaryLogicalOperationName + '}');
    }

    private Function<PredicateOperation, UnaryLogicalOperation> getUnaryLogicalOperationConstructorByName(String unaryLogicalOperationName) {
        if ("!".equals(unaryLogicalOperationName)) {
            return Not::new;
        }
        throw new RuntimeException("Неизвестная унарная логическая операция с именем {" + unaryLogicalOperationName + '}');
    }

    private RuntimeException validationError(String message) {
        return new RuntimeException(message + ' ' + tokenizer + "\nПри обработке токена " + currentToken);
    }

    private RuntimeException validationError(String message, Throwable cause) {
        return new RuntimeException(message + ' ' + tokenizer + "\nПри обработке токена " + currentToken, cause);
    }
}
