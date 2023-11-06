package ru.mikheev.kirill.custombpm.service;

import java.util.Map;
import java.util.UUID;

public interface ProcessLifecycleService {

    void startNewProcess(String schemeName, Map<String, String> parameters);

    void processCallback(UUID processId, String branchCode, Map<String, String> parameters);
}
