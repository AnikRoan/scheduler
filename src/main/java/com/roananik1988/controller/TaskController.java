package com.roananik1988.controller;

import com.roananik1988.entity.TaskRequest;
import com.roananik1988.scheduler.SchedulerTaskRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Log4j2
public class TaskController {

    private final SchedulerTaskRunner schedulerTaskRunner;

    @PostMapping
    public ResponseEntity<TaskRequest> createJob(@RequestBody TaskRequest taskRequest) {
        TaskRequest task = new TaskRequest();
        task.setTaskName(taskRequest.getTaskName());
        task.setTimestamp(taskRequest.getTimestamp());
        schedulerTaskRunner.execute(task);
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
    public ResponseEntity<String> stopJob(@PathVariable Long taskId) {
        return ResponseEntity.ok().body(schedulerTaskRunner.stopTask(taskId));
    }
}
