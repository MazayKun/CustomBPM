package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.UUID;

public class FinishTask extends TaskStage {

    public FinishTask(String code) {
        super(code);
    }

    @Override
    public void execute(UUID processId, String branchCode, TaskExecutor taskExecutor) {
        taskExecutor.executeFinishTask(processId, branchCode, this);
    }
}
