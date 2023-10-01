package ru.mikheev.kirill.custombpm.scheme.condition.parsing;

/**
 * Работает со строкой посимвольно и упрощает токенизацию
 */
public class StringScanner {

    private final String string; // Исходная строка
    private int pointer = 0; // Указатель на следующую позицию в строке

    public StringScanner(String string) {
        this.string = string;
    }

    public boolean hasNext() {
        return pointer < string.length();
    }

    public char next() {
        return string.charAt(pointer++);
    }

    public char peekNext() {
        return string.charAt(pointer);
    }

    public int getPointer() {
        return pointer;
    }

    public String getRepresentation(int startIndex, int endIndex) {
        return string.substring(startIndex, endIndex);
    }

    @Override
    public String toString() {
        if(pointer >= string.length()) {
            return "StringScanner{" +
                    "string='" + string + "{}'" +
                    ", pointer=" + pointer + '}';
        }
        return "StringScanner{" +
                "string='" + string.substring(0, pointer) + '{' + string.charAt(pointer) + '}' +
                string.substring(pointer + 1) + '\'' + ", pointer=" + pointer + '}';
    }
}
