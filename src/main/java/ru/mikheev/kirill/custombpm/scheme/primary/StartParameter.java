package ru.mikheev.kirill.custombpm.scheme.primary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StartParameter {
    private String name;
    private String innerName;
    private String type;
    @JacksonXmlText
    private String defaultValue;
}
