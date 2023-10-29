package ru.mikheev.kirill.custombpm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.scheme.SchemeStorage;
import ru.mikheev.kirill.custombpm.service.ConfigFileOperations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class ConfigFileService implements ConfigFileOperations {

    private final SchemeStorage schemeStorage;
    private final String schemeVolume;

    public ConfigFileService(SchemeStorage schemeStorage, @Value("${schemeVolume}") String schemeVolume) {
        this.schemeStorage = schemeStorage;
        this.schemeVolume = schemeVolume;
    }

    public void uploadNewScheme(String schemeFileName, InputStream inputStream) throws IOException {
        File schemeFile = new File(schemeVolume, schemeFileName);
        if (!schemeFile.createNewFile()) {
            throw new RuntimeException("Cannot create file " + schemeFileName);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(schemeFile)) {
            inputStream.transferTo(fileOutputStream);
        }
        schemeStorage.addNewScheme(schemeFile);
    }
}
