package ru.mikheev.kirill.custombpm.scheme.general.task;

import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.scheme.general.InitParameter;
import ru.mikheev.kirill.custombpm.scheme.general.InputParameter;
import ru.mikheev.kirill.custombpm.scheme.general.OutputParameter;
import ru.mikheev.kirill.custombpm.scheme.primary.*;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Service
public class TaskFactory {

    public StartTask newStartTask(Start start) {
        StartTask startTask = new StartTask(start.getCode());
        for (StartParameter parameter : start.getStartParameters()) {
            startTask.addInitParameter(
                    new InitParameter(
                            isEmpty(parameter.getName()) ? parameter.getInnerName() : parameter.getName(),
                            parameter.getInnerName(),
                            parameter.getType(),
                            parameter.getDefaultValue()
                    )
            );
        }
        return startTask;
    }

    public FinishTask newFinishTask(Finish finish) {
        return new FinishTask(finish.getCode());
    }

    public TaskStage newTask(Task taskInfo) {
        return switch (taskInfo.getType()) {
            case "call_with_result" -> newCallWithResultTask(taskInfo);
            case "call" -> newCallTask(taskInfo);
            case "gate" -> newGateTask(taskInfo);
            case "timer" -> newTimerTask(taskInfo);
            default -> throw new RuntimeException("Bad task type " + taskInfo.getType());
        };
    }

    private CallWithResultTask newCallWithResultTask(Task taskInfo) {
        var callWithResultTask = new CallWithResultTask(
                taskInfo.getCode(),
                taskInfo.getEndpoint().getValue()
        );
        for (Parameter inputParameter : taskInfo.getInputParameters()) {
            callWithResultTask.addInputParameter(new InputParameter(
                    inputParameter.getName(),
                    inputParameter.getInnerName(),
                    inputParameter.getType(),
                    inputParameter.getDefaultValue()
            ));
        }

        for (Parameter outputParameter : taskInfo.getOutputParameters()) {
            if (nonNull(outputParameter.getDefaultValue()))
                throw new RuntimeException("No default value allowed for output parameter");
            callWithResultTask.addOutputParameter(new OutputParameter(
                    outputParameter.getName(),
                    outputParameter.getInnerName(),
                    outputParameter.getType()
            ));
        }
        return callWithResultTask;
    }

    private CallTask newCallTask(Task taskInfo) {
        var callTask = new CallTask(taskInfo.getCode(), taskInfo.getEndpoint().getValue());
        for (Parameter inputParameter : taskInfo.getInputParameters()) {
            callTask.addInputParameter(new InputParameter(
                    inputParameter.getName(),
                    inputParameter.getInnerName(),
                    inputParameter.getType(),
                    inputParameter.getDefaultValue()
            ));
        }
        if (nonNull(taskInfo.getOutputParameters()) && !taskInfo.getOutputParameters().isEmpty())
            throw new RuntimeException("No output parameters allowed for call task");
        return callTask;
    }

    private GateTask newGateTask(Task taskInfo) {
        return new GateTask(taskInfo.getCode());
    }

    private TimerTask newTimerTask(Task taskInfo) {
        return new TimerTask(taskInfo.getCode(), 100L); // TODO : добавить тег
    }
}
