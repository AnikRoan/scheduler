package com.roananik1988.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roananik1988.dto.TaskRequestDto;
import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.enums.TimeStatusExecution;
import com.roananik1988.repository.TaskStatusRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TaskStatusRepository taskStatusRepository;


    @BeforeEach
    void setUp() {
        taskStatusRepository.deleteAll();
    }

    @Test
    @Order(1)
    void getInfoWaitingJobs() throws Exception {
        //Test
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("in the queue waiting tasks: 0"));
    }

    @Test
    @Order(2)
    void getInfoWaitingJobs2() throws Exception {
        //Data
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(1L);
        taskStatus.setTaskName("Task 1");
        taskStatus.setExecutor(Executor.JON_HENDERSON);
        taskStatus.setTaskType(TaskType.SIMPLE);
        taskStatus.setCreateTime(LocalDateTime.now());
        taskStatus.setTimeStatusExecution(TimeStatusExecution.PENDING);
        taskStatus.setScheduledTime(Duration.ofSeconds(1));

        TaskStatus taskStatus1 = new TaskStatus();
        taskStatus1.setId(2L);
        taskStatus1.setTaskName("Task 2");
        taskStatus1.setExecutor(Executor.JON_HENDERSON);
        taskStatus1.setTaskType(TaskType.DIFFICULT);
        taskStatus1.setCreateTime(LocalDateTime.now());
        taskStatus1.setTimeStatusExecution(TimeStatusExecution.PENDING);
        taskStatus1.setScheduledTime(Duration.ofSeconds(1));

        taskStatusRepository.save(taskStatus);
        taskStatusRepository.save(taskStatus1);
        TimeUnit.SECONDS.sleep(11);
        //Test
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("in the queue waiting tasks: 2"));
    }

    @Test
    void createJob() throws Exception {
        //Data
        TaskRequestDto taskRequest = new TaskRequestDto();
        taskRequest.setTaskName("Task 1");
        taskRequest.setExecutor(Executor.JON_HENDERSON);
        taskRequest.setTaskType(TaskType.SIMPLE);
        taskRequest.setScheduledTime(Duration.ofSeconds(1));
        //Test
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("taskName").value("Task 1"))
                .andExpect(jsonPath("executor").value("JON_HENDERSON"));
    }

    @Test
    void getInfoAboutJob() throws Exception {
        //Data
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(1L);
        taskStatus.setTaskName("Task 1");
        taskStatus.setExecutor(Executor.JON_HENDERSON);
        taskStatus.setTaskType(TaskType.SIMPLE);
        taskStatus.setCreateTime(LocalDateTime.now());
        taskStatus.setTimeStatusExecution(TimeStatusExecution.PENDING);
        taskStatus.setResultExecution(String.format("Task is PENDING"));
        taskStatusRepository.save(taskStatus);
        //Test
        mockMvc.perform(get("/tasks/" + 1L))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Task %s:\nTask is PENDING", 1L)));
    }

    @Test
    void stopJob() throws Exception {
        //Data
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setId(3L);
        taskStatus.setTaskName("Task 1");
        taskStatus.setExecutor(Executor.JON_HENDERSON);
        taskStatus.setTaskType(TaskType.SIMPLE);
        taskStatus.setCreateTime(LocalDateTime.now());
        taskStatus.setTimeStatusExecution(TimeStatusExecution.PENDING);
        taskStatus.setScheduledTime(Duration.ofSeconds(1));
        taskStatus.setResultExecution(String.format("%s %s", TimeStatusExecution.CREATED, LocalDateTime.now().toString()));
        taskStatusRepository.save(taskStatus);
        TimeUnit.SECONDS.sleep(15);
        //Test
        mockMvc.perform(delete("/tasks/" + 3L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Task 1"));

        TimeUnit.SECONDS.sleep(5);
        //Data
        TaskStatus taskStatus1 = taskStatusRepository.findById(3L).get();
        //When
        assertNotNull(taskStatus1);
        assertEquals(taskStatus1.getTimeStatusExecution(), TimeStatusExecution.INTERRUPTED);

    }
}