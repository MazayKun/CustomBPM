package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.InitParameter;
import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StartTask extends TaskStage {

    private List<InitParameter> initParameters = new ArrayList<>();

    public StartTask(String code) {
        super(code);
    }

    public void addInitParameter(InitParameter initParameter) {
        initParameters.add(initParameter);
    }

    public List<InitParameter> getInitParameters() {
        return initParameters;
    }

    public TaskLink getNextTaskLink() {
        return nextTaskLink;
    }

    @Override
    public void execute(UUID processId, String branchCode, TaskExecutor taskExecutor) {
        throw new UnsupportedOperationException("Start task has no instruction for execution");
    }
}
