package ru.mikheev.kirill.custombpm.scheme;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;

public class SchemeParser {

    private final XmlMapper mapper;

    public SchemeParser() {
        mapper = new XmlMapper();
    }

    public void parseScheme(File schemeFile) {

    }
}