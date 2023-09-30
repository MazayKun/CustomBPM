package ru.mikheev.kirill.custombpm.scheme.primary;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Endpoint {
    @JacksonXmlText
    private String value;
}
