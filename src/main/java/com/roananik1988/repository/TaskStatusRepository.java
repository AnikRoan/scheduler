package com.roananik1988.repository;

import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TimeStatusExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    @Query("SELECT DISTINCT e.executor FROM TaskStatus e")
    List<Executor> findAllExecutors();

    @Query("""
            SELECT e FROM TaskStatus e
                    WHERE e.timeStatusExecution = :timeStatusExecution AND e.executor = :executor
            """)
    List<TaskStatus> findByTimeStatusExecution(@Param("executor") Executor executor,
                                               @Param("timeStatusExecution") TimeStatusExecution timeStatusExecution);


}
