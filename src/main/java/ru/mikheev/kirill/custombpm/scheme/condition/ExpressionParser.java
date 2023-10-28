package ru.mikheev.kirill.custombpm.scheme.condition;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ExpressionParser {

    public ExpressionParsingResult parse(String expression) {
        var expressionBuilder = new ExpressionBuilder(expression, Collections.EMPTY_SET);
        return expressionBuilder.build();
    }
}
