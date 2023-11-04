package ru.mikheev.kirill.custombpm.scheme.primary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Task {
    private String description;
    private String code;
    private String type;
    private Endpoint endpoint; // TODO Вынести в отдельный конфиг
    @JacksonXmlElementWrapper(localName = "input")
    private List<Parameter> inputParameters;
    @JacksonXmlElementWrapper(localName = "output")
    private List<Parameter> outputParameters;
}
