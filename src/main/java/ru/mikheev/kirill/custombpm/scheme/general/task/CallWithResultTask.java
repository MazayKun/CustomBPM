package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.scheme.general.OutputParameter;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CallWithResultTask extends TaskStage {

    private final String endpoint;
    private List<InputParameter> inputParameters = new ArrayList<>();
    private List<OutputParameter> outputParameters = new ArrayList<>();

    public CallWithResultTask(String code, String endpoint) {
        super(code);
        this.endpoint = endpoint;
    }

    @Override
    public void execute(UUID processId, String branchCode, TaskExecutor taskExecutor) {
        taskExecutor.executeCallWithResultTask(processId, branchCode, this);
    }

    public void addInputParameter(InputParameter inputParameter) {
        inputParameters.add(inputParameter);
    }

    public void addOutputParameter(OutputParameter outputParameter) {
        outputParameters.add(outputParameter);
    }
}
