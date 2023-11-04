package ru.mikheev.kirill.custombpm.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.mikheev.kirill.custombpm.service.impl.ProcessService;
import ru.mikheev.kirill.custombpm.service.impl.TaskExecutorImpl;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

@EnableAsync
@Configuration
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {

    private final ProcessService processService;

    @Override
    public Executor getAsyncExecutor() {
        return ForkJoinPool.commonPool();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new TaskExecutionHandler();
    }

    // TODO : Проверить, что сервлет юзает другой пул
    @Slf4j
    private class TaskExecutionHandler implements AsyncUncaughtExceptionHandler {

        @Override
        public void handleUncaughtException(Throwable e, Method method, Object... params) {
            if (!method.getDeclaringClass().isAssignableFrom(TaskExecutorImpl.class)) {
                log.error("Unknown exception", e);
                return;
            }
            if (params.length != 3 || !(params[0] instanceof UUID) || !(params[1] instanceof String)) {
                log.error(String.format("Unknown method %s with params %s throw async exception", method.getName(), Arrays.toString(params)), e);
                return;
            }
            processService.interruptBranchExecutionWithError(
                    (UUID)params[0],
                    (String)params[1],
                    e
            );
        }
    }
}
