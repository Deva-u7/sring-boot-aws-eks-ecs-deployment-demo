package com.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.todo.dto.TodoDTO;
import com.todo.service.AWSS3FileUploadService;
import com.todo.service.TodoService;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class TodoS3UploadTest {

    private WireMockServer wireMockServer;

    @Autowired
    private TodoController todoController;

    @Mock
    private TodoService todoService; // Mocked dependency

    @Mock
    private AWSS3FileUploadService awss3FileUploadService; // Mocked dependency

    @Mock
    private S3Client amazonS3;

    private MockMvc mockMvc;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule();

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8082));
        wireMockServer.start();
        WireMock.configureFor(wireMockServer.port());

        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testFileUpload() throws Exception {
        // Define stub mappings for AWS S3 endpoint
        stubFor(any(urlEqualTo("/bucket/object/test.txt"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"fileURL\": \"https://s3.amazonaws.com/bucket/object\"}")));

        // Mock TodoService and its method addTodo
        TodoDTO todoRequest = TodoDTO.builder()
                .firstName("firstName")
                .lastName("lastName")
                .age(27)
                .build();

        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes());
        String expectedFileUrl = "https://s3.amazonaws.com/bucket/object";


        String responseContent = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/todo")
                        //.file(mockFile) // Add the file to the request
                        .file("file", multipartFile.getInputStream().readAllBytes())
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(todoRequest))) // Add JSON payload if needed
                        .andExpect(status().is(200)).andReturn().getResponse().getContentAsString();

        System.out.println(responseContent);
    }
}
    