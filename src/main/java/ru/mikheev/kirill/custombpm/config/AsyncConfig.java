package ru.mikheev.kirill.custombpm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.mikheev.kirill.custombpm.service.event.EventPublisher;
import ru.mikheev.kirill.custombpm.service.impl.AsyncTaskExecutor;
import ru.mikheev.kirill.custombpm.service.impl.ProcessService;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final ProcessService processService;
    private final EventPublisher eventPublisher;

    @Override
    @Bean("threadPoolTaskExecutor")
    public Executor getAsyncExecutor() {
        return ForkJoinPool.commonPool();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new TaskExecutionHandler();
    }

    // TODO : Проверить, что сервлет юзает другой пул
    private class TaskExecutionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable e, Method method, Object... params) {
            log.error("Exception in async method", e);
            if (!method.getDeclaringClass().isAssignableFrom(AsyncTaskExecutor.class)) {
                log.error("Unknown exception", e);
                return;
            }
            if (params.length != 3 || !(params[0] instanceof UUID processId) || !(params[1] instanceof String branchCode)) {
                log.error(String.format("Unknown method %s with params %s throw async exception", method.getName(), Arrays.toString(params)), e);
                return;
            }
            processService.interruptBranchExecutionWithError(
                    processId,
                    branchCode,
                    e
            );
            eventPublisher.publishBranchClosedEvent(branchCode, processId);
        }
    }
}
