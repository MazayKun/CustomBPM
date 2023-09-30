package ru.mikheev.kirill.custombpm.scheme.general;

import java.util.Map;

public interface LogicalExpression {

    boolean calculateResult(Map<String, Object> data);
}
