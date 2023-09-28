package ru.mikheev.kirill.custombpm.scheme;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Condition {
    private String type;
    private String conditionExpression;
}
