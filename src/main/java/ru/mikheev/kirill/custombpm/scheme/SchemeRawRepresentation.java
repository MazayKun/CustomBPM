package ru.mikheev.kirill.custombpm.scheme;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SchemeRawRepresentation {
    private Start start;
    private List<Task> tasks;
    private List<Link> links;
    private List<Finish> finishes;
}
