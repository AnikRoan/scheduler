package com.roananik1988.entity;

import com.roananik1988.enums.TimeStatusExecution;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.extern.log4j.Log4j2;
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
    private LocalDateTime createTime;
    private TimeStatusExecution timeStatusExecution;
    private String resultExecution;
}
