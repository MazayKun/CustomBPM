package ru.mikheev.kirill.custombpm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mikheev.kirill.custombpm.service.ConfigFileOperations;
import ru.mikheev.kirill.custombpm.service.impl.ConfigFileService;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RequestMapping("/config")
@RestController
@RequiredArgsConstructor
public class ConfigUploadController {

    private final ConfigFileOperations configFileOperations;

    @PostMapping("/upload-scheme")
    public void uploadNewScheme(@RequestParam("file") MultipartFile file) {
        try(InputStream fileInputStream = file.getInputStream()) {
            configFileOperations.uploadNewScheme(file.getOriginalFilename(), fileInputStream);
        } catch (IOException e) {
            log.error("Error during uploading config file", e);
        }
    }
}
