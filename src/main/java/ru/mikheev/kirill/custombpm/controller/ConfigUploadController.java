package ru.mikheev.kirill.custombpm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.mikheev.kirill.custombpm.service.ConfigFileOperations;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    public void uploadNewScheme(@Valid @NotNull @RequestParam("file") MultipartFile file) {
        requestValidator.validateSchemeExtension(file);
        try (InputStream fileInputStream = file.getInputStream()) {
            configFileOperations.uploadNewScheme(file.getOriginalFilename(), fileInputStream);
        } catch (IOException e) {
            log.error("Error during uploading config file", e);
        }
    }
}
