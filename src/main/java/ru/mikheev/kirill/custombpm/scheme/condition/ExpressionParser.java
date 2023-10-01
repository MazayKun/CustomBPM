package ru.mikheev.kirill.custombpm.scheme.condition;

import org.springframework.stereotype.Service;

@Service
public class ExpressionParser {

    public ExpressionParsingResult parse(String expression) {
        var expressionBuilder = new ExpressionBuilder(expression);
        return expressionBuilder.build();
    }
}
