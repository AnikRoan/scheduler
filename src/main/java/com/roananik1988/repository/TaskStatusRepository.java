package com.roananik1988.repository;

import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.TimeStatusExecution;
import org.springframework.data.jpa.repository.JpaRepository;


import java.time.LocalDateTime;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    default void update(Long id, TimeStatusExecution timeStatusExecution) {
        TaskStatus taskStatus = findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        String status = taskStatus.getResultExecution();
        taskStatus.setResultExecution(String.format("%s,\n%s %s", status,
                                                                  timeStatusExecution,
                                                                  LocalDateTime.now().toString()));
        taskStatus.setTimeStatusExecution(timeStatusExecution);
        save(taskStatus);
    }
}
