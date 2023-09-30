package ru.mikheev.kirill.custombpm.scheme.primary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
public class Start {
    private String code;
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "startParameter")
    private List<StartParameter> startParameters;
}
