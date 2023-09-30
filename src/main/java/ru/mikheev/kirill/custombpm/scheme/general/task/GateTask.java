package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.service.TaskExecutor;

// TODO доделать эту часть
public class GateTask extends TaskStage {

    public GateTask(String code) {
        super(code);
    }

    @Override
    public void execute(TaskExecutor taskExecutor) {
        taskExecutor.executeGateTask(this);
    }
}
