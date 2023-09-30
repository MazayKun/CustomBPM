package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.InitParameter;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void execute(TaskExecutor taskExecutor) {
        taskExecutor.startNewProcess(this);
    }
}
