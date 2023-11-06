package ru.mikheev.kirill.custombpm.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.mikheev.kirill.custombpm.service.event.dto.BranchClosedEvent;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishBranchClosedEvent(String branchCode, UUID processId) {
        applicationEventPublisher.publishEvent(new BranchClosedEvent(
                this,
                branchCode,
                processId
        ));
    }
}
