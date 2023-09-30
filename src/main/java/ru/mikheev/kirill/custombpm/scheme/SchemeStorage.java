package ru.mikheev.kirill.custombpm.scheme;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;

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
        if(!schemesDir.isDirectory()) {
            throw new RuntimeException(schemesVolume + " must be a directory");
        }
        if(schemesDir.exists()) {
            for(var schemeFile : schemesDir.listFiles()) {
                schemes.put(
                        constructSchemeName(schemeFile.getName()),
                        schemeParser.parseScheme(schemeFile)
                );
            }
        }else{
            if(!schemesDir.mkdir()) {
                throw new RuntimeException("Cannot create schemes directory");
            }
        }
        System.out.println("ready");
    }

    public Scheme getSchemeByName(String schemeName) {
        Scheme scheme = schemes.get(schemeName);
        if(isNull(scheme)) {
            throw new RuntimeException("Scheme not found");
        }
        return scheme;
    }

    public void addNewScheme(File schemeFile) {

    }

    private String constructSchemeName(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.') - 1);
    }
}
