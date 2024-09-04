//package com.roananik1988.scheduler;
//
//import com.roananik1988.entity.TaskRequest;
//import com.roananik1988.entity.TaskStatus;
//import com.roananik1988.enums.TimeStatusExecution;
//import com.roananik1988.repository.TaskStatusRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.Duration;
//import java.util.Optional;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SchedulerTaskRunnerTest {
//    @Mock
//    private TaskStatusRepository taskStatusRepository;
//    @InjectMocks
//    private SchedulerTaskRunner schedulerTaskRunner;
//
//    @BeforeEach
//    void init() {
//        schedulerTaskRunner = new SchedulerTaskRunner(taskStatusRepository);
//    }
//
//    @BeforeEach
//    void setUp() {
//        taskRequest = new TaskRequest();
//        taskRequest.setId(1L);
//        taskRequest.setTaskName("Task 1");
//        taskRequest.setTimestamp(Duration.ofSeconds(1));
//
//        taskStatus = new TaskStatus();
//        taskStatus.setId(1L);
//        taskStatus.setTaskName("Task 1");
//
//
//    }
//
//    private TaskRequest taskRequest;
//    private TaskStatus taskStatus;
//
//    @Test
//    void executeTest() throws InterruptedException {
//        ArgumentCaptor<TaskStatus> argumentCaptor = ArgumentCaptor.forClass(TaskStatus.class);
//        schedulerTaskRunner.execute(taskRequest);
//        TimeUnit.SECONDS.sleep(5);
//
//        verify(taskStatusRepository, times(1))
//                .save(argumentCaptor.capture());
//        verify(taskStatusRepository, times(1))
//                .update(eq(1L), eq(TimeStatusExecution.STARTED));
//        verify(taskStatusRepository, times(1))
//                .update(eq(1L), eq(TimeStatusExecution.COMPLETED));
//
//        TaskStatus taskStatus = argumentCaptor.getValue();
//
//        assertEquals(taskRequest.getTaskName(), taskStatus.getTaskName());
//        assertEquals(TimeStatusExecution.COMPLETED, taskStatus.getTimeStatusExecution());
//        assertEquals(taskRequest.getId(), taskStatus.getId());
//    }
//
//    @Test
//    void executeInterruptedTest() throws InterruptedException {
//        doNothing().when(taskStatusRepository).update(anyLong(), any(TimeStatusExecution.class));
//        schedulerTaskRunner.execute(taskRequest);
//        TimeUnit.SECONDS.sleep(1);
//
//        ScheduledFuture<?> future = schedulerTaskRunner.getTasks().get(1L);
//        assertNotNull(future);
//        future.cancel(true);
//        TimeUnit.SECONDS.sleep(2);
//
//        verify(taskStatusRepository, times(1))
//                .update(eq(1L), eq(TimeStatusExecution.INTERRUPTED));
//    }
//
//    @Test
//    void getStatusTest() {
//        taskStatus.setResultExecution("COMPLETED");
//
//        when(taskStatusRepository.findById(eq(1L))).thenReturn(Optional.of(taskStatus));
//
//        String status = schedulerTaskRunner.getStatus(1L);
//        assertEquals("COMPLETED", status);
//    }
//
//    @Test
//    void getStatusPendingTest() {
//        schedulerTaskRunner.execute(taskRequest);
//
//        String status = schedulerTaskRunner.getStatus(1L);
//
//        assertEquals("Task is PENDING", status);
//    }
//
//    @Test
//    void getStatusNotFoundTest() {
//        String status = schedulerTaskRunner.getStatus(99L);
//
//        assertEquals("Task not found", status);
//    }
//
//    @Test
//    void getWaitingTasks() {
//        schedulerTaskRunner.execute(taskRequest);
//        schedulerTaskRunner.execute(taskRequest);
//        schedulerTaskRunner.execute(taskRequest);
//
//        String status = schedulerTaskRunner.getWaitingTasks();
//
//        assertEquals("in the queue waiting tasks: 3", status);
//    }
//
//    @Test
//    void stopTask() {
//        schedulerTaskRunner.execute(taskRequest);
//
//        String response = schedulerTaskRunner.stopTask(1L);
//
//        assertEquals("Task 1 stopped", response);
//    }
//
//
//}