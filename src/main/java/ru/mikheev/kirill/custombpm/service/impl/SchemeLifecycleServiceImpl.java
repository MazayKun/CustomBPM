package ru.mikheev.kirill.custombpm.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.scheme.SchemeStorage;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;
import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.link.TransitionType;
import ru.mikheev.kirill.custombpm.service.SchemeLifecycleService;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;
import ru.mikheev.kirill.custombpm.service.dto.StartProcessDto;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchemeLifecycleServiceImpl implements SchemeLifecycleService {

    private final ProcessService processService;
    private final SchemeStorage schemeStorage;
    private final TaskExecutor taskExecutor;

    @Override
    public void startNewProcess(String schemeName, Map<String, String> parameters) {
        Scheme scheme = schemeStorage.getSchemeByName(schemeName)
                .orElseThrow(() -> new RuntimeException(String.format("Scheme with name %s not found", schemeName)));
        StartProcessDto startProcessDto = processService.initNewProcess(scheme, parameters);
        TaskLink nextTaskLink = scheme.getStartTask().getNextTaskLink();
        if (nextTaskLink.getTransitionType() == TransitionType.EXPRESSION && !nextTaskLink.checkPredicateOperation(startProcessDto.getProcessVariables())) {
            log.warn("Condition for link from start to first task is false for process " + startProcessDto.getProcessId());
            return;
        }
        nextTaskLink.getTo().execute(
                startProcessDto.getProcessId(),
                startProcessDto.getBranchCode(),
                taskExecutor
        );
    }
}
