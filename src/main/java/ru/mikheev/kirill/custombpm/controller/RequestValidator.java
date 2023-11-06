package ru.mikheev.kirill.custombpm.controller;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class RequestValidator {

    private static final String SCHEME_EXTENSION = "xml";

    public void validateSchemeExtension(MultipartFile file) {
        if (isEmpty(file.getOriginalFilename())) {
            throw new RuntimeException("Empty file name");
        }
        String fileExtension = file.getOriginalFilename().substring(
                file.getOriginalFilename().lastIndexOf('.') + 1
        );
        if (!SCHEME_EXTENSION.equalsIgnoreCase(fileExtension)) {
            throw new RuntimeException("Bad extension for scheme file " + fileExtension);
        }
    }
}
