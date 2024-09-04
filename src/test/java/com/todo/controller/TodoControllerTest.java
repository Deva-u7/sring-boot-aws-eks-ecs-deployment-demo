package com.todo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.entity.Todo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class TodoControllerTest {

    public MockMvc mockMvc;

    @Autowired
    public TodoController todoController;

    private ObjectMapper objectMapper = new ObjectMapper();

    private String BASE_URL = "/api/v1/todo";

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Test
    void addTodo() throws Exception {
        Todo todo = Todo.builder()
                .firstName("firstName")
                .lastName("lastName")
                .age(27)
                .build();

        String responseContent = mockMvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().is(200))
                .andReturn()
                .getResponse().getContentAsString();

        todo.setId(1L);
        JsonNode expectedJson = objectMapper.readTree(objectMapper.writeValueAsString(todo));
        JsonNode actualJson = objectMapper.readTree(responseContent);
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @Test
    public void getTodo() throws Exception {
        Todo todo = Todo.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .age(27)
                .build();

        String responseContent = mockMvc.perform(get(BASE_URL)).andExpect(status().is(200)).andReturn().getResponse().getContentAsString();
        JsonNode expectedJson = objectMapper.readTree(objectMapper.writeValueAsString(Arrays.asList(todo)));
        JsonNode actualJson = objectMapper.readTree(responseContent);
        Assertions.assertEquals(expectedJson, actualJson);
    }

    @Test
    public void getTodoById() throws Exception {
        Todo todo = Todo.builder()
                .id(1L)
                .firstName("firstName")
                .lastName("lastName")
                .age(27)
                .build();

        String responseContent = mockMvc.perform(get(BASE_URL + "/1")).andExpect(status().is(200)).andReturn().getResponse().getContentAsString();
        JsonNode expectedJson = objectMapper.readTree(objectMapper.writeValueAsString(todo));
        JsonNode actualJson = objectMapper.readTree(responseContent);
        Assertions.assertEquals(expectedJson, actualJson);
    }
}