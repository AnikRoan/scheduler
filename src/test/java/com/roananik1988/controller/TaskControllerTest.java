//package com.roananik1988.controller;
//
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.roananik1988.entity.TaskRequest;
//import com.roananik1988.entity.TaskStatus;
//import com.roananik1988.repository.TaskStatusRepository;
//import com.roananik1988.scheduler.SchedulerTaskRunner;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.concurrent.ScheduledFuture;
//
//import static org.mockito.Mockito.mock;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//class TaskControllerTest {
//
//    @Autowired
//    MockMvc mockMvc;
//
//    @Autowired
//    ObjectMapper objectMapper;
//
//    @Autowired
//    SchedulerTaskRunner schedulerTaskRunner;
//
//    @Autowired
//    private TaskStatusRepository taskStatusRepository;
//
//
//    @Test
//    void createJobTest() throws Exception {
//        TaskRequest taskRequest = new TaskRequest();
//        taskRequest.setTaskName("Task 1");
//        taskRequest.setTimestamp(Duration.ofSeconds(1));
//
//        mockMvc.perform(post("/")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(taskRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.taskName").value("Task 1"));
//    }
//
//    @Test
//    void getInfoAboutJobTest() throws Exception {
//        TaskStatus taskStatus = new TaskStatus();
//        taskStatus.setId(1L);
//        taskStatus.setTaskName("Test Task");
//        taskStatus.setCreateTime(LocalDateTime.now());
//        taskStatus.setResultExecution("COMPLETED");
//        taskStatusRepository.save(taskStatus);
//
//        mockMvc.perform(get("/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(String.format("Task 1:\n%s", taskStatus.getResultExecution())));
//    }
//
//    @Test
//    void getStatusTaskNotFoundTest() throws Exception {
//        String response = "Task not found";
//
//        mockMvc.perform(get("/99"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(String.format("Task 99:\n%s", response)));
//    }
//
//    @Test
//    void getStatusTaskPendingTest() throws Exception {
//        ScheduledFuture<?> future = mock(ScheduledFuture.class);
//        schedulerTaskRunner.getTasks().put(1L, future);
//        String response = "Task is PENDING";
//
//        mockMvc.perform(get("/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(String.format("Task 1:\n%s", response)));
//    }
//
//    @Test
//    void getInfoWaitingJobsTest() throws Exception {
//        TaskRequest taskRequest = new TaskRequest();
//        taskRequest.setTimestamp(Duration.ofSeconds(2));
//
//        schedulerTaskRunner.execute(taskRequest);
//        schedulerTaskRunner.execute(taskRequest);
//        schedulerTaskRunner.execute(taskRequest);
//
//        int queueSize = 3;
//
//        mockMvc.perform(get("/"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(String.format("in the queue waiting tasks: %s", queueSize)));
//
//
//    }
//
//    @Test
//    void stopJobTest() throws Exception {
//        TaskRequest taskRequest = new TaskRequest();
//        taskRequest.setId(1L);
//        taskRequest.setTimestamp(Duration.ofSeconds(2));
//
//        schedulerTaskRunner.execute(taskRequest);
//
//        mockMvc.perform(delete("/1"))
//                .andExpect(status().isOk())
//                .andExpect(content().string(String.format("Task %s stopped", taskRequest.getId())));
//    }
//
//}