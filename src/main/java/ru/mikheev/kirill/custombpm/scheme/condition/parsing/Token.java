package ru.mikheev.kirill.custombpm.scheme.condition.parsing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Структурная единица критерия
 */
@Getter
@RequiredArgsConstructor
public class Token {

    private final Type type; // Тип токена
    private final int startIndex; // Позиция, с которой начинается токен в исходной строке
    private final int endIndex; // Позиция, на которой заканчивается токен в исходной строке

    @Override
    public String toString() {
        return "Token{" + type + '(' + startIndex + ", " + endIndex + ")}";
    }

    public enum Type {
        PARAMETER,
        CONSTANT,
        CONSTANT_ARRAY,
        BINARY_LOGICAL_OPERATION,
        UNARY_LOGICAL_OPERATION,
        INEQUALITY_OPERATION,
        OPEN_BRACKET,
        CLOSE_BRACKET
    }
}
