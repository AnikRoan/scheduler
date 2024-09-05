package com.roananik1988.service;

import com.roananik1988.entity.TaskRequest;
import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.enums.TimeStatusExecution;
import com.roananik1988.repository.TaskStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskStatusRepository taskStatusRepository;
    @InjectMocks
    private TaskService taskService;
    private TaskRequest taskRequest;
    private TaskStatus taskStatus;

    @BeforeEach
    void setUp() {
         taskRequest = TaskRequest.builder()
                .taskName("Task 1")
                .executor(Executor.JON_HENDERSON)
                .taskType(TaskType.SIMPLE)
                .scheduledTime(Duration.ofSeconds(1))
                .build();

         taskStatus = TaskStatus.builder()
                .id(1L)
                .taskName("Task 1")
                .executor(Executor.JON_HENDERSON)
                .taskType(TaskType.SIMPLE)
                .scheduledTime(Duration.ofSeconds(1))
                .createTime(LocalDateTime.now())
                .timeStatusExecution(TimeStatusExecution.COMPLETED)
                .resultExecution(String.format("%s %s", TimeStatusExecution.PENDING,
                        LocalDateTime.now().toString()))
                .build();


    }

    @Test
    void saveTest() {
        when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(taskStatus);

        TaskStatus task = taskService.save(taskRequest);

        assertEquals(taskStatus, task);
        verify(taskStatusRepository, times(1)).save(any(TaskStatus.class));

    }

    @Test
    void getPendingTasksTest() {
        when(taskStatusRepository.findByTimeStatusExecution(Executor.JON_HENDERSON, TimeStatusExecution.PENDING))
                .thenReturn(List.of(taskStatus));

        List<TaskStatus> tasks = taskService.getPendingTasks(Executor.JON_HENDERSON);

        assertEquals(1, tasks.size());
        assertEquals(taskStatus, tasks.get(0));
        verify(taskStatusRepository, times(1))
                .findByTimeStatusExecution(Executor.JON_HENDERSON, TimeStatusExecution.PENDING);
    }

    @Test
    void getAllExecutorsTest() {
        when(taskStatusRepository.findAllExecutors()).thenReturn(List.of(
                                                                 Executor.JON_HENDERSON,Executor.WILLIAM_SHAKESPEARE));
        List<Executor> executors = taskService.getAllExecutors();

        assertEquals(2, executors.size());

        verify(taskStatusRepository, times(1)).findAllExecutors();
    }

    @Test
    void updateTest() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));

        taskService.update(1L, TimeStatusExecution.COMPLETED);

        assertEquals(TimeStatusExecution.COMPLETED, taskStatus.getTimeStatusExecution());

        verify(taskStatusRepository,times(1)).findById(1L);
        verify(taskStatusRepository,times(1)).save(any(TaskStatus.class));
    }
    @Test
    void updateNotFoundTest() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,()->taskService.update(1L,TimeStatusExecution.COMPLETED));

        verify(taskStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskStatusTest() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));
        TaskStatus task = taskService.getTaskStatus(1L);

        assertEquals(taskStatus, task);
        verify(taskStatusRepository, times(1)).findById(1L);

    }
    @Test
    void getTaskStatusNotFoundTest() {
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.empty());

       assertThrows(RuntimeException.class, () -> taskService.getTaskStatus(1L));

       verify(taskStatusRepository, times(1)).findById(1L);
    }
}