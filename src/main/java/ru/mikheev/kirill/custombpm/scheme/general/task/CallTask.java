package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

public class CallTask extends TaskStage {

    private final String endpoint;
    private List<InputParameter> inputParameters = new ArrayList<>();

    public CallTask(String code, String endpoint) {
        super(code);
        this.endpoint = endpoint;
    }

    @Override
    public void execute(TaskExecutor taskExecutor) {
        taskExecutor.executeCallTask(this);
    }

    public void addInputParameter(InputParameter inputParameter) {
        inputParameters.add(inputParameter);
    }
}
