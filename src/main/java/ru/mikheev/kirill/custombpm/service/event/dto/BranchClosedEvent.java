package ru.mikheev.kirill.custombpm.service.event.dto;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class BranchClosedEvent extends ApplicationEvent {

    private final String branchCode;
    private final UUID processId;

    public BranchClosedEvent(Object source, String branchCode, UUID processId) {
        super(source);
        this.branchCode = branchCode;
        this.processId = processId;
    }
}
