package com.roananik1988.dto;

import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Duration;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    @NotEmpty(message = "Task name cannot be empty")
    private String taskName;
    @NotNull(message = "Executor cannot be null")
    private Executor executor;
    @NotNull(message = "Task type cannot be null")
    private TaskType taskType;
    @NotNull(message = "Timestamp cannot be null")
    private Duration scheduledTime;
}
