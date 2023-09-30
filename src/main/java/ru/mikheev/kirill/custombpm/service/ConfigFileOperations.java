package ru.mikheev.kirill.custombpm.service;

import java.io.IOException;
import java.io.InputStream;

public interface ConfigFileOperations {

    void uploadNewScheme(String schemeFileName, InputStream inputStream) throws IOException;
}
