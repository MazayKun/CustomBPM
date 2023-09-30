package ru.mikheev.kirill.custombpm.scheme.general.task;

import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.scheme.general.OutputParameter;
import ru.mikheev.kirill.custombpm.service.TaskExecutor;

import java.util.ArrayList;
import java.util.List;

public class CallWithResultTask extends TaskStage {

    private final String endpoint;
    private List<InputParameter> inputParameters = new ArrayList<>();
    private List<OutputParameter> outputParameters = new ArrayList<>();

    public CallWithResultTask(String code, String endpoint) {
        super(code);
        this.endpoint = endpoint;
    }

    @Override
    public void execute(TaskExecutor taskExecutor) {
        taskExecutor.executeCallWithResultTask(this);
    }

    public void addInputParameter(InputParameter inputParameter) {
        inputParameters.add(inputParameter);
    }

    public void addOutputParameter(OutputParameter outputParameter) {
        outputParameters.add(outputParameter);
    }
}
