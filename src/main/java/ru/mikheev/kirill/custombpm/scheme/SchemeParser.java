package ru.mikheev.kirill.custombpm.scheme;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.common.DataType;
import ru.mikheev.kirill.custombpm.scheme.condition.ExpressionBuilder;
import ru.mikheev.kirill.custombpm.scheme.condition.operation.PredicateOperation;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;
import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.task.*;
import ru.mikheev.kirill.custombpm.scheme.primary.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Service
public class SchemeParser {

    private final XmlMapper mapper;
    private final TaskFactory taskFactory;

    public SchemeParser(TaskFactory taskFactory) {
        mapper = new XmlMapper();
        this.taskFactory = taskFactory;
    }

    public Scheme parseScheme(File schemeFile, String schemeName) {
        SchemeRawRepresentation rawRepresentation;
        try {
            rawRepresentation = mapper.readValue(schemeFile, SchemeRawRepresentation.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return constructGeneralScheme(rawRepresentation, schemeName);
    }

    // TODO: Распилить этот метод на несколько
    private Scheme constructGeneralScheme(SchemeRawRepresentation rawScheme, String schemeName) {
        Map<String, DataType> parametersDataTypes = rawScheme.getParameterTypes().stream()
                .collect(Collectors.toMap(ParameterType::getName, this::convertStringToDataType));
        Map<String, List<Link>> links = rawScheme.getLinks().stream()
                .collect(Collectors.groupingBy(Link::getFrom));
        Map<String, Task> tasks = rawScheme.getTasks().stream()
                .collect(Collectors.toMap(Task::getCode, Function.identity())); // TODO Обработать правильно ошибку валидации
        Map<String, Integer> incomingLinksCounterMap = new HashMap<>();
        Scheme resultScheme = new Scheme(schemeName);
        resultScheme.setParametersTypes(parametersDataTypes);
        for (Finish finish : rawScheme.getFinishes()) {
            FinishTask finishTask = taskFactory.newFinishTask(finish);
            resultScheme.addTask(finishTask);
        }
        StartTask startTask = taskFactory.newStartTask(
                rawScheme.getStart(),
                resultScheme.getParametersTypes()
        );
        Queue<TaskStage> tasksToEnrich = new LinkedList<>();
        tasksToEnrich.add(startTask);
        resultScheme.addStartTask(startTask);
        TaskStage currTask;
        while (!tasksToEnrich.isEmpty()) {
            currTask = tasksToEnrich.remove();
            for (Link link : links.get(currTask.getCode())) {
                Optional<TaskStage> nextStageOpt = resultScheme.getTaskStageByCode(link.getTo());
                TaskStage nextTask;
                if (nextStageOpt.isPresent()) {
                    nextTask = nextStageOpt.get();
                } else {
                    Task dataForNewTask = tasks.get(link.getTo());
                    if (isNull(dataForNewTask))
                        throw new RuntimeException("Next task with code " + link.getTo() + " not found");
                    nextTask = taskFactory.newTask(dataForNewTask, resultScheme.getParametersTypes());
                    tasksToEnrich.add(nextTask);
                    resultScheme.addTask(nextTask);
                }
                TaskLink generalLink;
                if (isNull(link.getCondition())) {
                    generalLink = TaskLink.guaranteedLink(nextTask);
                } else {
                    generalLink = switch (link.getCondition().getType()) {
                        case "default" -> TaskLink.defaultLink(nextTask);
                        case "expression" -> TaskLink.conditionLink(
                                nextTask,
                                constructLogicalExpression(link.getCondition().getConditionExpression(), resultScheme)
                        );
                        default -> throw new RuntimeException("Bad condition type " + link.getCondition().getType());
                    };
                }
                incomingLinksCounterMap.merge(nextTask.getCode(), 1, Integer::sum);
                currTask.addLink(generalLink);
            }
        }
        enrichTasksWithIncomingLinks(resultScheme.getAllTasks(), incomingLinksCounterMap);
        return resultScheme;
    }

    private void enrichTasksWithIncomingLinks(List<TaskStage> taskStages, Map<String, Integer> incomingLinksCounterMap) {
        for (TaskStage taskStage : taskStages) {
            int incomingLinksCounter = incomingLinksCounterMap.getOrDefault(taskStage.getCode(), 0);
            if (taskStage instanceof StartTask && incomingLinksCounter != 0) {
                throw new RuntimeException("There must be 0 incoming links for start task " + taskStage.getCode());
            }
            if (incomingLinksCounter == 0) {
                throw new RuntimeException("The number of incoming links cannot be 0 for task " + taskStage.getCode());
            }
            if (taskStage instanceof GateTask gt) {
                gt.setIncomingLinksCounter(incomingLinksCounter);
            }
            if (incomingLinksCounter != 1) {
                throw new RuntimeException(String.format("There must be 1 incoming links for task %s but %d found", taskStage.getCode(), incomingLinksCounter));
            }
        }
    }

    private DataType convertStringToDataType(ParameterType parameterType) {
        return DataType.fromTypeCode(parameterType.getType());
    }

    private PredicateOperation constructLogicalExpression(String expression, Scheme scheme) {
        ExpressionBuilder expressionBuilder = new ExpressionBuilder(expression, scheme.getParametersTypes());
        return expressionBuilder.build().predicateOperation();
    }
}
