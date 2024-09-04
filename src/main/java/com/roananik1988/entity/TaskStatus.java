package com.roananik1988.entity;

import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.enums.TimeStatusExecution;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.LocalDateTime;


@Builder
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Log4j2
@Entity
@Table(name = "task_statuses")
public class TaskStatus {
    @Id
    private Long id;
    private String taskName;
    @Enumerated(value = EnumType.STRING)
    private Executor executor;
    @Enumerated(value = EnumType.STRING)
    private TaskType taskType;
    private Duration scheduledTime;
    private LocalDateTime createTime;
    @Enumerated(value = EnumType.STRING)
    private TimeStatusExecution timeStatusExecution;
    private String resultExecution;
}
