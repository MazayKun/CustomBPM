package ru.mikheev.kirill.custombpm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mikheev.kirill.custombpm.controller.request.ProcessCallbackRequest;
import ru.mikheev.kirill.custombpm.controller.request.StartProcessRequest;
import ru.mikheev.kirill.custombpm.service.ProcessLifecycleService;

@Slf4j
@RequestMapping("/process")
@RestController
@RequiredArgsConstructor
public class ProcessLifecycleController {

    private final ProcessLifecycleService processLifecycleService;

    @PostMapping("/start")
    public void startNewProcess(@RequestBody StartProcessRequest startProcessRequest) {
        processLifecycleService.startNewProcess(
                startProcessRequest.getSchemeName(),
                startProcessRequest.getParameters()
        );
    }

    @PostMapping("/callback")
    public void processCallback(@RequestBody ProcessCallbackRequest processCallbackRequest) {
        processLifecycleService.processCallback(
                processCallbackRequest.getProcessId(),
                processCallbackRequest.getBranchCode(),
                processCallbackRequest.getParameters()
        );
    }
}
