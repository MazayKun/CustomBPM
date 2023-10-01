package ru.mikheev.kirill.custombpm.scheme.condition;

import ru.mikheev.kirill.custombpm.scheme.condition.operation.PredicateOperation;
import ru.mikheev.kirill.custombpm.scheme.condition.parsing.Token;
import ru.mikheev.kirill.custombpm.scheme.condition.parsing.Tokenizer;

import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ExpressionBuilder {

    private final Set<String> requiredParameters = new HashSet<>();
    private final Tokenizer tokenizer;

    private Token currentToken;

    public ExpressionBuilder(String expressionToParse) {
        if(isBlank(expressionToParse)) {
            throw new RuntimeException("Expression string is empty");
        }
        this.tokenizer = new Tokenizer(expressionToParse);
    }

    public ExpressionParsingResult build() {
        if (!tokenizer.hasNext()) throw new RuntimeException("Expression already created");
        PredicateOperation operationRepresentation = buildNewLevelOfExpression(false);
        return new ExpressionParsingResult(operationRepresentation, requiredParameters);
    }

    //TODO : Добавить логические выражения, состоящие только из констант (true, 1 ...)
    private PredicateOperation buildNewLevelOfExpression(boolean closingBracketIsRequired) {
        return null;
    }
}
