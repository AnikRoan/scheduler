package com.roananik1988.scheduler;

import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerTaskRunnerTest {

    @Mock
    private TaskService taskService;
    @Mock
    ScheduledFuture<?> scheduledFuture;
    @Mock
    private TaskStatus taskStatus;
    @Mock
    private Executor executor;
    @Mock
    BlockingQueue<Runnable> queue;
    @InjectMocks
    private SchedulerTaskRunner schedulerTaskRunner;


    @Test
    void checkTasksTest() {
        //Mock
        when(taskService.getAllExecutors()).thenReturn(List.of(executor));
        when(taskService.getPendingTasks(executor)).thenReturn(new ArrayList<>(List.of(taskStatus)));
        when(taskStatus.getTaskType()).thenReturn(TaskType.SIMPLE);

        schedulerTaskRunner.checkTasks();
        //Assert & Verify
        verify(taskService).getAllExecutors();
        verify(taskService).getPendingTasks(executor);

    }

    @Test
    void getStatusTest() {
        //Data
        schedulerTaskRunner.getTasks().put(1L, scheduledFuture);
        String status = schedulerTaskRunner.getStatus(1L);
        //When
        assertNotNull(status);
        assertEquals("Task is PENDING", status);
    }

    @Test
    void getWaitingTasks() {
        //Data
        String result = schedulerTaskRunner.getWaitingTasks();
        //When
        assertNotNull(result);
        assertEquals("in the queue waiting tasks: 0", result);
    }

    @Test
    void stopTaskTest() {
        //Mock
        when(scheduledFuture.getDelay(any())).thenReturn(0L);
        schedulerTaskRunner.getTasks().put(1L, scheduledFuture);
        when(taskService.getTaskStatus(anyLong())).thenReturn(TaskStatus.builder().id(1L).build());
        //Data
       TaskStatus result = schedulerTaskRunner.stopTask(1L);
        //When
       assertNotNull(result);
       //Assert & Verify
       verify(scheduledFuture).cancel(true);

    }

}