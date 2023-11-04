package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CallTask extends TaskStage {

    private final String endpoint;
    private List<InputParameter> inputParameters = new ArrayList<>();

    public CallTask(String code, String endpoint) {
        super(code);
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public List<InputParameter> getInputParameters() {
        return inputParameters;
    }

    @Override
    public void execute(UUID processId, String branchCode, TaskExecutor taskExecutor) {
        taskExecutor.executeCallTask(processId, branchCode, this);
    }

    public void addInputParameter(InputParameter inputParameter) {
        inputParameters.add(inputParameter);
    }
}
