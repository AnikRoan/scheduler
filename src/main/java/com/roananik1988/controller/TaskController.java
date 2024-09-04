package com.roananik1988.controller;

import com.roananik1988.dto.TaskRequestDto;
import com.roananik1988.entity.TaskRequest;
import com.roananik1988.entity.TaskStatus;
import com.roananik1988.scheduler.SchedulerTaskRunner;
import com.roananik1988.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {

    private final SchedulerTaskRunner schedulerTaskRunner;
    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskRequest> createJob(@Valid @RequestBody TaskRequestDto taskRequest) {
        TaskRequest task = new TaskRequest();
        task.setTaskName(taskRequest.getTaskName());
        task.setExecutor(taskRequest.getExecutor());
        task.setTaskType(taskRequest.getTaskType());
        task.setScheduledTime(taskRequest.getScheduledTime());

        taskService.save(task);
        return ResponseEntity.ok().body(task);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<String> getInfoAboutJob(@PathVariable Long taskId) {
        return ResponseEntity.ok().body(String.format("Task %s:\n%s", taskId, schedulerTaskRunner.getStatus(taskId)));
    }

    @GetMapping
    public ResponseEntity<String> getInfoWaitingJobs() {
        return ResponseEntity.ok().body(schedulerTaskRunner.getWaitingTasks());
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<TaskStatus> stopJob(@PathVariable Long taskId) {
        return ResponseEntity.ok().body(schedulerTaskRunner.stopTask(taskId));
    }
}
