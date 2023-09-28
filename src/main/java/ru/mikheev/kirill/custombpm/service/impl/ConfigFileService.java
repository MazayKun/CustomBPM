package ru.mikheev.kirill.custombpm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.service.ConfigFileOperations;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class ConfigFileService implements ConfigFileOperations {

    private final String schemeVolume;

    public ConfigFileService(@Value("${schemeVolume}") String schemeVolume) {
        this.schemeVolume = schemeVolume;
        File file = new File(schemeVolume);
        file.mkdir();
    }

    public void uploadNewScheme(String fileName, InputStream inputStream) throws IOException {
        File file = new File(schemeVolume, fileName);
        System.out.println(file.createNewFile());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)){
            inputStream.transferTo(fileOutputStream);
        }
    }
}
