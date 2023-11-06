package ru.mikheev.kirill.custombpm.scheme.general.task;

import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.common.DataType;
import ru.mikheev.kirill.custombpm.scheme.general.InitParameter;
import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.scheme.general.OutputParameter;
import ru.mikheev.kirill.custombpm.scheme.raw.*;

import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class TaskFactory {

    public StartTask newStartTask(Start start, Map<String, DataType> parametersTypes) {
        StartTask startTask = new StartTask(start.getCode());
        DataType currType;
        for (StartParameter parameter : start.getStartParameters()) {
            currType = parametersTypes.get(parameter.getInnerName());
            if (isNull(currType)) throw new RuntimeException("No type definition for init parameter " + parameter.getName());
            startTask.addInitParameter(
                    new InitParameter(
                            isEmpty(parameter.getName()) ? parameter.getInnerName() : parameter.getName(),
                            parameter.getInnerName(),
                            currType,
                            parameter.getDefaultValue()
                    )
            );
        }
        return startTask;
    }

    public FinishTask newFinishTask(Finish finish) {
        return new FinishTask(finish.getCode());
    }

    public TaskStage newTask(Task taskInfo, Map<String, DataType> parametersTypes) {
        return switch (taskInfo.getType()) {
            case "call_with_result" -> newCallWithResultTask(taskInfo, parametersTypes);
            case "call" -> newCallTask(taskInfo, parametersTypes);
            case "singleton_gate" -> newSingletonGateTask(taskInfo);
            case "parallel_gate" -> newParallelGateTask(taskInfo);
            case "timer" -> newTimerTask(taskInfo);
            default -> throw new RuntimeException("Bad task type " + taskInfo.getType());
        };
    }

    private CallWithResultTask newCallWithResultTask(Task taskInfo, Map<String, DataType> parametersTypes) {
        var callWithResultTask = new CallWithResultTask(
                taskInfo.getCode(),
                taskInfo.getEndpoint().getValue()
        );
        DataType currType;
        for (Parameter inputParameter : taskInfo.getInputParameters()) {
            currType = parametersTypes.get(inputParameter.getInnerName());
            if (isNull(currType)) throw new RuntimeException("No type definition for init parameter " + inputParameter.getInnerName());
            callWithResultTask.addInputParameter(new InputParameter(
                    inputParameter.getName(),
                    inputParameter.getInnerName(),
                    currType,
                    inputParameter.getDefaultValue()
            ));
        }

        for (Parameter outputParameter : taskInfo.getOutputParameters()) {
            if (nonNull(outputParameter.getDefaultValue()))
                throw new RuntimeException("No default value allowed for output parameter");
            currType = parametersTypes.get(outputParameter.getInnerName());
            if (isNull(currType)) throw new RuntimeException("No type definition for init parameter " + outputParameter.getInnerName());
            callWithResultTask.addOutputParameter(new OutputParameter(
                    outputParameter.getName(),
                    outputParameter.getInnerName(),
                    currType
            ));
        }
        return callWithResultTask;
    }

    private CallTask newCallTask(Task taskInfo, Map<String, DataType> parametersTypes) {
        var callTask = new CallTask(taskInfo.getCode(), taskInfo.getEndpoint().getValue());
        DataType currType;
        for (Parameter inputParameter : taskInfo.getInputParameters()) {
            currType = parametersTypes.get(inputParameter.getInnerName());
            if (isNull(currType)) throw new RuntimeException("No type definition for init parameter " + inputParameter.getInnerName());
            callTask.addInputParameter(new InputParameter(
                    inputParameter.getName(),
                    inputParameter.getInnerName(),
                    currType,
                    inputParameter.getDefaultValue()
            ));
        }
        if (nonNull(taskInfo.getOutputParameters()) && !taskInfo.getOutputParameters().isEmpty())
            throw new RuntimeException("No output parameters allowed for call task");
        return callTask;
    }

    private GateTask newParallelGateTask(Task taskInfo) {
        return new GateTask(GateType.PARALLEL, taskInfo.getCode());
    }

    private GateTask newSingletonGateTask(Task taskInfo) {
        return new GateTask(GateType.SINGLETON, taskInfo.getCode());
    }

    private TimerTask newTimerTask(Task taskInfo) {
        return new TimerTask(taskInfo.getCode(), 100L); // TODO : добавить тег
    }
}
