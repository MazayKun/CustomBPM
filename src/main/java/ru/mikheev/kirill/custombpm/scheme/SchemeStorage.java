package ru.mikheev.kirill.custombpm.scheme;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SchemeStorage {

    private final String schemesVolume;
    private final SchemeParser schemeParser;
    private final Map<String, Scheme> schemes = new HashMap<>();

    public SchemeStorage(SchemeParser schemeParser, @Value("${schemeVolume}") String schemesVolume) {
        this.schemeParser = schemeParser;
        this.schemesVolume = schemesVolume;
    }

    @PostConstruct
    private void initUploadedSchemes() {
        File schemesDir = new File(schemesVolume);
        if (!schemesDir.isDirectory()) {
            throw new RuntimeException(schemesVolume + " must be a directory");
        }
        if (schemesDir.exists()) {
            String schemeName;
            for (var schemeFile : schemesDir.listFiles()) {
                schemeName = constructSchemeName(schemeFile.getName());
                schemes.put(
                        schemeName,
                        schemeParser.parseScheme(schemeFile, schemeName)
                );
            }
        } else {
            if (!schemesDir.mkdir()) {
                throw new RuntimeException("Cannot create schemes directory");
            }
        }
        log.info("Old schemes initialized");
    }

    public Optional<Scheme> getSchemeByName(String schemeName) {
        return Optional.ofNullable(
                schemes.get(schemeName)
        );
    }

    public void addNewScheme(File schemeFile) {
        String schemeName = constructSchemeName(schemeFile.getName());
        schemes.put(
                schemeName,
                schemeParser.parseScheme(schemeFile, schemeName)
        );
    }

    private String constructSchemeName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
