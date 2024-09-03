package com.roananik1988.scheduler;

import com.roananik1988.entity.TaskRequest;
import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.TimeStatusExecution;
import com.roananik1988.repository.TaskStatusRepository;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Log4j2
@Getter
public class SchedulerTaskRunner {
    @Value("${thread.count}")
    private int threadsCount;
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    public SchedulerTaskRunner(TaskStatusRepository taskStatusRepository) {
        this.taskStatusRepository = taskStatusRepository;
    }

    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(threadsCount);

    private final Map<Long, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();


    public void execute(TaskRequest taskRequest) {
        Long delay = taskRequest.getTimestamp().toSeconds();

        ScheduledFuture<?> scheduledFuture = scheduledThreadPoolExecutor.schedule(() -> {
            try {
                TaskStatus taskStatus = new TaskStatus();
                taskStatus.setId(taskRequest.getId());
                taskStatus.setTaskName(taskRequest.getTaskName());
                taskStatus.setCreateTime(LocalDateTime.now());
                taskStatus.setResultExecution(String.format(
                        "%s %s", TimeStatusExecution.CREATED, taskStatus.getCreateTime().toString()));

                log.info("Creating TaskStatus: {}", taskStatus);

                taskStatusRepository.save(taskStatus);
                log.info("TaskStatus saved with ID: {}", taskStatus.getId());

                taskStatusRepository.update(taskStatus.getId(), TimeStatusExecution.STARTED);
                taskStatus.setTimeStatusExecution(TimeStatusExecution.STARTED);
                log.info("Task status updated to STARTED");

                Thread.sleep(500);

                taskStatusRepository.update(taskStatus.getId(), TimeStatusExecution.COMPLETED);
                taskStatus.setTimeStatusExecution(TimeStatusExecution.COMPLETED);
                log.info("Task status updated to COMPLETED");

            } catch (InterruptedException e) {
                log.error("Task execution interrupted", e);
                taskStatusRepository.update(taskRequest.getId(), TimeStatusExecution.INTERRUPTED);
                log.info("Task status updated to INTERRUPTED");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Unexpected error during task execution", e);
            } finally {
                tasks.remove(taskRequest.getId());
            }
        }, delay, TimeUnit.SECONDS);

        tasks.put(taskRequest.getId(), scheduledFuture);
    }

    public String getStatus(Long taskId) {
        TaskStatus taskStatus = taskStatusRepository.findById(taskId).orElse(null);
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

    public String stopTask(Long taskId) {
        ScheduledFuture<?> future = tasks.get(taskId);
        if (future != null) {
            future.cancel(true);

            return String.format("Task %s stopped", taskId);

        }
        return "Task not found";
    }

    @PreDestroy
    public void shutdown() {
        scheduledThreadPoolExecutor.shutdown();
    }

}



