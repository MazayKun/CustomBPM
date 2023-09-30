package ru.mikheev.kirill.custombpm.scheme;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.scheme.general.LogicalExpression;
import ru.mikheev.kirill.custombpm.scheme.general.Scheme;
import ru.mikheev.kirill.custombpm.scheme.general.link.TaskLink;
import ru.mikheev.kirill.custombpm.scheme.general.task.FinishTask;
import ru.mikheev.kirill.custombpm.scheme.general.task.StartTask;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskFactory;
import ru.mikheev.kirill.custombpm.scheme.general.task.TaskStage;
import ru.mikheev.kirill.custombpm.scheme.primary.Finish;
import ru.mikheev.kirill.custombpm.scheme.primary.Link;
import ru.mikheev.kirill.custombpm.scheme.primary.SchemeRawRepresentation;
import ru.mikheev.kirill.custombpm.scheme.primary.Task;

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

    public Scheme parseScheme(File schemeFile) {
        SchemeRawRepresentation rawRepresentation;
        try {
            rawRepresentation = mapper.readValue(schemeFile, SchemeRawRepresentation.class);
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return constructGeneralScheme(rawRepresentation);
    }

    private Scheme constructGeneralScheme(SchemeRawRepresentation rawScheme) {
        Map<String, List<Link>> links = rawScheme.getLinks().stream()
                .collect(Collectors.groupingBy(Link::getFrom));
        Map<String, Task> tasks = rawScheme.getTasks().stream()
                .collect(Collectors.toMap(Task::getCode, Function.identity())); // TODO Обработать правильно ошибку валидации
        Scheme resultScheme = new Scheme();
        for(Finish finish : rawScheme.getFinishes()) {
            FinishTask finishTask = taskFactory.newFinishTask(finish);
            resultScheme.addTask(finishTask);
        }
        StartTask startTask = taskFactory.newStartTask(rawScheme.getStart());
        Queue<TaskStage> tasksToEnrich = new LinkedList<>();
        tasksToEnrich.add(startTask);
        resultScheme.addStartTask(startTask);
        TaskStage currTask;
        while(!tasksToEnrich.isEmpty()) {
            currTask = tasksToEnrich.remove();
            for(Link link : links.get(currTask.getCode())) {
                Optional<TaskStage> nextStageOpt = resultScheme.getTaskStageByCode(link.getTo());
                TaskStage nextTask;
                if(nextStageOpt.isPresent()) {
                    nextTask = nextStageOpt.get();
                }else{
                    Task dataForNewTask = tasks.get(link.getTo());
                    if(isNull(dataForNewTask)) throw new RuntimeException("Next task with code " + link.getTo() + " not found");
                    nextTask = taskFactory.newTask(dataForNewTask);
                    tasksToEnrich.add(nextTask);
                    resultScheme.addTask(nextTask);
                }
                TaskLink generalLink;
                if(isNull(link.getCondition())) {
                    generalLink = TaskLink.guaranteedLink(nextTask);
                }else{
                    generalLink = switch (link.getCondition().getType()) {
                        case "default" -> TaskLink.defaultLink(nextTask);
                        case "expression" -> TaskLink.conditionLink(
                                nextTask,
                                constructLogicalExpression(link.getCondition().getConditionExpression())
                        );
                        default -> throw new RuntimeException("Bad condition type " + link.getCondition().getType());
                    };
                }
                currTask.addLink(generalLink);
            }
        }
        return resultScheme;
    }

    private LogicalExpression constructLogicalExpression(String expression) {
        return data -> false;
    }
}
