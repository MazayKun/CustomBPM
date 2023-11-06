package ru.mikheev.kirill.custombpm.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mikheev.kirill.custombpm.service.ConfigFileOperations;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequestMapping("/config")
@RestController
@RequiredArgsConstructor
@Validated
public class ConfigUploadController {

    private final RequestValidator requestValidator;
    private final ConfigFileOperations configFileOperations;

    @PostMapping("/upload-scheme")
    public void uploadNewScheme(@Valid @NotNull @RequestParam("file") MultipartFile file) throws IOException {
        requestValidator.validateSchemeExtension(file);
        try (InputStream fileInputStream = file.getInputStream()) {
            configFileOperations.uploadNewScheme(file.getOriginalFilename(), fileInputStream);
        }
    }
}
