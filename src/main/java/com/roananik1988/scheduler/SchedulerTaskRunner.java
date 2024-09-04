package com.roananik1988.scheduler;

import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.enums.TimeStatusExecution;
import com.roananik1988.service.TaskService;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Log4j2
@Getter
public class SchedulerTaskRunner {
    @Value("${thread.count}")
    private int threadsCount;

    @Autowired
    private TaskService taskService;


    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(threadsCount);

    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private final BlockingQueue<TaskStatus> simpleTaskQueue = new LinkedBlockingQueue<>();

    @Scheduled(fixedDelay = 10000)
    public void checkTasks() {
        List<Executor> executors = taskService.getAllExecutors();
        for (Executor executor : executors) {
            List<TaskStatus> taskStatuses = taskService.getPendingTasks(executor);
            Iterator<TaskStatus> iterator = taskStatuses.iterator();
            long countRunSimpleTasks = 0;

            while (iterator.hasNext()) {
                TaskStatus taskStatus = iterator.next();
                if (taskStatus.getTaskType() == TaskType.SIMPLE && countRunSimpleTasks != 1) {
                    simpleTaskQueue.offer(taskStatus);
                    iterator.remove();
                    countRunSimpleTasks++;
                    startTaskProcessor();

                } else {
                    execute(taskStatus);
                }
            }
        }
    }

    private void startTaskProcessor() {
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            TaskStatus taskStatus = simpleTaskQueue.poll();
            if (taskStatus != null) {
                execute(taskStatus);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    private void execute(TaskStatus taskStatus) {
        Long delay = taskStatus.getScheduledTime().toSeconds();
        ScheduledFuture<?> scheduledFuture = scheduledThreadPoolExecutor.schedule(() -> runTask(taskStatus)
                , delay, TimeUnit.SECONDS);

        tasks.put(taskStatus.getId(), scheduledFuture);
        log.info("\nTask " + tasks.size());
    }

    private void runTask(TaskStatus taskStatus) {
        try {
            taskService.update(taskStatus.getId(), TimeStatusExecution.STARTED);
            log.info("Task status updated to STARTED");

            Thread.sleep(9000);
            taskService.update(taskStatus.getId(), TimeStatusExecution.COMPLETED);
            log.info("Task status updated to COMPLETED");

        } catch (InterruptedException e) {
            log.error("Task execution interrupted", e);
            taskService.update(taskStatus.getId(), TimeStatusExecution.INTERRUPTED);
            log.info("Task status updated to INTERRUPTED");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Unexpected error during task execution", e);
        } finally {
            tasks.remove(taskStatus.getId());
            simpleTaskQueue.remove(taskStatus);
        }

    }

    public String getStatus(Long taskId) {
        TaskStatus taskStatus = taskService.getTaskStatus(taskId);
        if (taskStatus == null) {
            ScheduledFuture<?> future = tasks.get(taskId);
            if (future != null) {
                return "Task is PENDING";
            } else {
                return "Task not found";
            }
        }
        return taskStatus.getResultExecution();
    }

    public String getWaitingTasks() {
        BlockingQueue<Runnable> queue = scheduledThreadPoolExecutor.getQueue();
        return String.format("in the queue waiting tasks: %s", queue.size());

    }

    public TaskStatus stopTask(Long taskId) {
        ScheduledFuture<?> future = tasks.get(taskId);
        if (future != null) {
            while (future.getDelay(TimeUnit.SECONDS) > 0) {
            }
            future.cancel(true);

            return taskService.getTaskStatus(taskId);

        }
        return TaskStatus.builder()
                .taskName("Task not found")
                .build();
    }

    @PreDestroy
    public void shutdown() {
        scheduledThreadPoolExecutor.shutdown();
    }

}



