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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        //Mock
        when(taskStatusRepository.save(any(TaskStatus.class))).thenReturn(taskStatus);
        //Data
        TaskStatus task = taskService.save(taskRequest);
        //When
        assertEquals(taskStatus, task);
        //Assert & Verify
        verify(taskStatusRepository, times(1)).save(any(TaskStatus.class));

    }

    @Test
    void getPendingTasksTest() {
        //Mock
        when(taskStatusRepository.findByTimeStatusExecution(Executor.JON_HENDERSON, TimeStatusExecution.PENDING))
                .thenReturn(List.of(taskStatus));
        //Data
        List<TaskStatus> tasks = taskService.getPendingTasks(Executor.JON_HENDERSON);
        //When
        assertEquals(1, tasks.size());
        assertEquals(taskStatus, tasks.get(0));
        //Assert & Verify
        verify(taskStatusRepository, times(1))
                .findByTimeStatusExecution(Executor.JON_HENDERSON, TimeStatusExecution.PENDING);
    }

    @Test
    void getAllExecutorsTest() {
        //Mock
        when(taskStatusRepository.findAllExecutors()).thenReturn(List.of(
                Executor.JON_HENDERSON, Executor.WILLIAM_SHAKESPEARE));
        //Data
        List<Executor> executors = taskService.getAllExecutors();
        //When
        assertEquals(2, executors.size());

        //Assert & Verify
        verify(taskStatusRepository, times(1)).findAllExecutors();
    }

    @Test
    void updateTest() {
        //Mock
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));

        taskService.update(1L, TimeStatusExecution.COMPLETED);
        //When
        assertEquals(TimeStatusExecution.COMPLETED, taskStatus.getTimeStatusExecution());
        //Assert & Verify
        verify(taskStatusRepository, times(1)).findById(1L);
        verify(taskStatusRepository, times(1)).save(any(TaskStatus.class));
    }

    @Test
    void updateNotFoundTest() {
        //Mock
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.empty());
        //When
        assertThrows(RuntimeException.class, () -> taskService.update(1L, TimeStatusExecution.COMPLETED));
        //Assert & Verify
        verify(taskStatusRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskStatusTest() {
        //Mock
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.of(taskStatus));
        //Data
        TaskStatus task = taskService.getTaskStatus(1L);
        //When
        assertEquals(taskStatus, task);
        //Assert & Verify
        verify(taskStatusRepository, times(1)).findById(1L);

    }

    @Test
    void getTaskStatusNotFoundTest() {
        //Mock
        when(taskStatusRepository.findById(1L)).thenReturn(Optional.empty());
        //When
        assertThrows(RuntimeException.class, () -> taskService.getTaskStatus(1L));
        //Assert & Verify
        verify(taskStatusRepository, times(1)).findById(1L);
    }
}