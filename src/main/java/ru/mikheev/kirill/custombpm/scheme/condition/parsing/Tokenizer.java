package ru.mikheev.kirill.custombpm.scheme.condition.parsing;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Преобразует строку, содержащую критерий перехода, в набор токенов, из которых можно построить готовое выражение
 */
public class Tokenizer {

    private final StringBuilder tokenBuilder = new StringBuilder();

    private final StringScanner stringScanner;
    private Token tokenBuffer = null; // Буффер для уже считанного, но не возвращенного токена

    public Tokenizer(String stringToTokenize) {
        this.stringScanner = new StringScanner(stringToTokenize);
    }

    public boolean hasNext() {
        if(nonNull(tokenBuffer)) return true;
        clearSpaces();
        return stringScanner.hasNext();
    }

    public Token next() {
        if(isNull(tokenBuffer)) return fetchToken();
        var result = tokenBuffer;
        tokenBuffer = null;
        return result;
    }

    public Token peekToken() {
        if(nonNull(tokenBuffer)) return tokenBuffer;
        tokenBuffer = fetchToken();
        return tokenBuffer;
    }

    public String getTokenRepresentation(Token token) {
        return stringScanner.getRepresentation(token.getStartIndex(), token.getEndIndex());
    }

    @Override
    public String toString() {
        return stringScanner.toString();
    }

    private void clearSpaces() {
        while(stringScanner.hasNext() && stringScanner.peekNext() == ' ') stringScanner.next();
    }

    private Token fetchToken() {
        clearSpaces();
        if (!stringScanner.hasNext()) throw new RuntimeException("В строке кончились токены");
        char nextChar = stringScanner.next();
        return switch (nextChar) {
            case '(' -> fetchTokenStartedWithOpenBracket();
            case ')' -> fetchCloseBracket();
            case '&', '|' -> fetchSimpleLogicalToken(nextChar);
            case '!' -> fetchTokenStartingWithExclamationMark();
            case '>', '<', '=' -> fetchInequalityOperation();
            default -> fetchLatinCharsToken(nextChar);
        };
    }

    private Token fetchLatinCharsToken(char firstChar) {
        if (firstChar >= '0' && firstChar <= '9' || firstChar == '\'') return fetchConstant(firstChar);
        if (checkIfCharIsNotLatin(firstChar)) throw validationError("Impossible to build token with current symbol");

        int startIndex = stringScanner.getPointer() - 1;
        tokenBuilder.setLength(0);
        tokenBuilder.append(firstChar);

        char nextChar;
        while (stringScanner.hasNext() && (nextChar = stringScanner.next()) != ' ') tokenBuilder.append(nextChar);

        String resultString = tokenBuilder.toString();
        if("TRUE".equalsIgnoreCase(resultString) || "FALSE".equalsIgnoreCase(resultString)) {
            return new Token(Token.Type.CONSTANT, startIndex, stringScanner.getPointer());
        }
        if ("IN".equalsIgnoreCase(resultString)) {
            return new Token(Token.Type.INEQUALITY_OPERATION, startIndex, stringScanner.getPointer());
        }
        if ("NOT".equalsIgnoreCase(resultString)) {
            clearSpaces();
            while (stringScanner.hasNext() && (nextChar = stringScanner.next()) != ' ') tokenBuilder.append(nextChar);
            if ("NOTIN".equalsIgnoreCase(tokenBuilder.toString())) {
                return new Token(Token.Type.INEQUALITY_OPERATION, startIndex, stringScanner.getPointer());
            } else {
                throw validationError("Cannot properly resolve NOT IN");
            }
        }
        return new Token(Token.Type.PARAMETER, startIndex, stringScanner.getPointer());
    }

    private Token fetchTokenStartedWithOpenBracket() {
        int bracketPosition = stringScanner.getPointer() - 1;
        clearSpaces();
        if (!stringScanner.hasNext()) return fetchOpenBracket(bracketPosition);
        char nextChar = stringScanner.peekNext();
        if (nextChar == '\'' || (nextChar >= '0' && nextChar <= '9')) {
            Token probableToken = fetchConstant( stringScanner.next());
            clearSpaces();
            nextChar = stringScanner.peekNext();
            if(nextChar == ',' || nextChar == ')') {
                return fetchConstantArray(bracketPosition);
            }
            if(tokenBuffer != null) throw new RuntimeException("Буффер не был очищен перед заполненеим");
            tokenBuffer = probableToken;
        }
        return fetchOpenBracket(bracketPosition);
    }

    private Token fetchConstantArray(int bracketPosition) {
        int startIndex = bracketPosition + 1;
        int apostropheCounter = 0;
        boolean currConstantRequireApostrophe = false;
        boolean closingBracketWasNotFound = true;
        char nextChar;
        while (stringScanner.hasNext() && closingBracketWasNotFound) {
            nextChar = stringScanner.next();
            switch (nextChar) {
                case ')' : {
                    closingBracketWasNotFound = false;
                    break;
                }
                case '\'' : apostropheCounter++; currConstantRequireApostrophe = !currConstantRequireApostrophe; break;
                case ' ' : break;
                case ',' : {
                    if(apostropheCounter % 2 == 1) throw validationError("Некорректное количество апострофов");
                    break;
                }
                default : {
                    if(currConstantRequireApostrophe && apostropheCounter % 2 == 0) throw validationError("Недопустимый символ при объявлении массива");
                }
            }
        }
        if(apostropheCounter % 2 == 1) throw validationError("Некорректное количество апострофов");
        if(closingBracketWasNotFound) throw validationError("Не было найдено закрывающей скобки для массива");
        return new Token(Token.Type.CONSTANT_ARRAY, startIndex, stringScanner.getPointer() - 1);
    }

    private Token fetchConstant(char firstChar) {
        int startIndex = stringScanner.getPointer() - 1;
        boolean closingApostropheRequired = firstChar == '\'';
        char nextChar;
        while (stringScanner.hasNext() && (nextChar = stringScanner.peekNext()) != ' ') {
            if(nextChar == ',') break;
            stringScanner.next();
            if(nextChar == '\'') {
                closingApostropheRequired = !closingApostropheRequired;
                break;
            }
        }
        if(closingApostropheRequired) throw validationError("Wrong apostrophe count in constant declaration");
        return new Token(Token.Type.CONSTANT, startIndex, stringScanner.getPointer());
    }

    private Token fetchInequalityOperation() {
        int startIndex = stringScanner.getPointer() - 1;
        if(stringScanner.peekNext() == '=') stringScanner.next();
        return new Token(Token.Type.INEQUALITY_OPERATION, startIndex, stringScanner.getPointer());
    }

    private Token fetchTokenStartingWithExclamationMark() {
        if(stringScanner.peekNext() == '=') return fetchInequalityOperation();
        return new Token(Token.Type.UNARY_LOGICAL_OPERATION, stringScanner.getPointer() - 1, stringScanner.getPointer());
    }

    private Token fetchOpenBracket(int bracketPosition) {
        return new Token(Token.Type.OPEN_BRACKET, bracketPosition, bracketPosition + 1);
    }

    private Token fetchCloseBracket() {
        return new Token(Token.Type.CLOSE_BRACKET, stringScanner.getPointer() - 1, stringScanner.getPointer());
    }

    private Token fetchSimpleLogicalToken(char logicalTokenFirstChar) {
        if (!stringScanner.hasNext() || stringScanner.next() != logicalTokenFirstChar) throw validationError("Cannot parse logical token");
        return new Token(Token.Type.BINARY_LOGICAL_OPERATION, stringScanner.getPointer() - 2, stringScanner.getPointer());
    }

    private boolean checkIfCharIsNotLatin(char charToCheck) {
        return !(
                charToCheck >= 'a' && charToCheck <= 'z' || charToCheck >= 'A' && charToCheck <= 'Z'
        );
    }

    private RuntimeException validationError(String errorMessage) {
        return new RuntimeException(errorMessage + ' ' + stringScanner);
    }
}
