package com.roananik1988.entity;

import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.enums.TimeStatusExecution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.UUID;


@Getter
@Setter
@ToString
@AllArgsConstructor
public class TaskRequest {
    private Long id;
    private String taskName;
    private Executor executor;
    private TaskType taskType;
    private Duration scheduledTime;
    private TimeStatusExecution timeStatusExecution;

    public TaskRequest() {
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.timeStatusExecution = TimeStatusExecution.PENDING;
    }

}
