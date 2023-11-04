package ru.mikheev.kirill.custombpm.service;

import java.util.Map;

public interface SchemeLifecycleService {

    void startNewProcess(String schemeName, Map<String, String> parameters);
}
