package ru.mikheev.kirill.custombpm.scheme;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Link {
    private String from;
    private String to;
    private Condition condition;
}
