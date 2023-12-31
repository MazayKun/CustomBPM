package ru.mikheev.kirill.custombpm.scheme.raw;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Link {
    private String code;
    private String from;
    private String to;
    private Condition condition;
}
