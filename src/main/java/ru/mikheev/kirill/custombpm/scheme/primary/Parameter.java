package ru.mikheev.kirill.custombpm.scheme.primary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Parameter {
    private String name;
    private String innerName;
    @JacksonXmlText
    private String defaultValue;
}
