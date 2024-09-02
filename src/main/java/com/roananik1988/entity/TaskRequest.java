package com.roananik1988.entity;
import com.roananik1988.enums.TimeStatusExecution;
import lombok.*;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.UUID;



@Getter
@Setter
@ToString
@AllArgsConstructor
@Component
public class TaskRequest {
    private Long id;
    private String taskName;
    private Duration timestamp;
    private TimeStatusExecution timeStatusExecution;
    public TaskRequest(){
        this.id = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        this.timeStatusExecution = TimeStatusExecution.PENDING;
    }

}
