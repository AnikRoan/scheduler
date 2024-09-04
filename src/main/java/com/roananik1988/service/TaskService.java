package com.roananik1988.service;

import com.roananik1988.entity.TaskRequest;
import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TimeStatusExecution;
import com.roananik1988.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TaskService {


    private final TaskStatusRepository taskStatusRepository;


    public TaskStatus save(TaskRequest taskRequest) {
        TaskStatus taskStatus = TaskStatus.builder()
                .id(taskRequest.getId())
                .taskName(taskRequest.getTaskName())
                .executor(taskRequest.getExecutor())
                .taskType(taskRequest.getTaskType())
                .scheduledTime(taskRequest.getScheduledTime())
                .createTime(LocalDateTime.now())
                .timeStatusExecution(taskRequest.getTimeStatusExecution())
                .resultExecution(String.format("%s %s", TimeStatusExecution.CREATED,
                        LocalDateTime.now().toString()))
                .build();
        return taskStatusRepository.save(taskStatus);
    }

    public List<TaskStatus> getPendingTasks(Executor executor) {
        List<TaskStatus> task = taskStatusRepository.findByTimeStatusExecution(executor, TimeStatusExecution.PENDING);
        log.info("\nPending tasks: {}", task);
        return task;

    }

    public List<Executor> getAllExecutors() {
        return taskStatusRepository.findAllExecutors();
    }


    @Transactional
    public void update(Long id, TimeStatusExecution timeStatusExecution) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Task not found"));
        String status = taskStatus.getResultExecution();
        taskStatus.setResultExecution(String.format("%s,\n%s %s", status,
                timeStatusExecution,
                LocalDateTime.now().toString()));
        taskStatus.setTimeStatusExecution(timeStatusExecution);
        taskStatusRepository.save(taskStatus);
    }

    public TaskStatus getTaskStatus(Long id) {
        return taskStatusRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
    }
}
