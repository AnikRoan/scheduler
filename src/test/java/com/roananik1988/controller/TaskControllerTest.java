package com.roananik1988.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.roananik1988.entity.TaskRequest;
import com.roananik1988.entity.TaskStatus;
import com.roananik1988.enums.Executor;
import com.roananik1988.enums.TaskType;
import com.roananik1988.scheduler.SchedulerTaskRunner;
import com.roananik1988.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Duration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private SchedulerTaskRunner schedulerTaskRunner;
    @MockBean
    private TaskService taskService;

    @Test
    void createJob() throws Exception {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTaskName("Task 1");
        taskStatus.setExecutor(Executor.JON_HENDERSON);
        taskStatus.setTaskType(TaskType.SIMPLE);
        taskStatus.setScheduledTime(Duration.ofSeconds(1));

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskName("Task 1");
        taskRequest.setExecutor(Executor.JON_HENDERSON);
        taskRequest.setTaskType(TaskType.SIMPLE);
        taskRequest.setScheduledTime(Duration.ofSeconds(1));

        taskStatus.setId(taskRequest.getId());


        when(taskService.save(taskRequest)).thenReturn(taskStatus);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Task 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.executor").value("JON_HENDERSON"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskType").value("SIMPLE"));

    }

    @Test
    void getInfoAboutJobTest() throws Exception {
        when(schedulerTaskRunner.getStatus(any())).thenReturn("Task is PENDING");

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Task 1:\nTask is PENDING"));
    }

    @Test
    void getInfoWaitingJobs() throws Exception {
        when(schedulerTaskRunner.getWaitingTasks()).thenReturn("in the queue waiting tasks: 3");

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("in the queue waiting tasks: 3"));
    }

    @Test
    void stopJob() throws Exception {

        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setTaskName("Task 1");

        when(schedulerTaskRunner.stopTask(any())).thenReturn(taskStatus);

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taskName").value("Task 1"));
    }
}