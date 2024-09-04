package com.todo.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.todo.entity.Todo;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

class TodoControllerWIreMockTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private String BASE_URL = "http://localhost:8080/api/v1/todo";

//    private String BASE_URL = "localhost:8080/api/v1/todo";

    private WireMockServer wireMockServer;

    @BeforeEach
    public void init() {
        wireMockServer =  new WireMockServer();
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
    }

    @AfterEach
    public void tearDown(){
        if(wireMockServer != null){
            wireMockServer.stop();
        }
    }

    @Test
    void addTodo() throws Exception {
        Todo todo = Todo.builder()
                .firstName("firstName")
                .lastName("lastName")
                .age(27)
                .fileURL("https://s3.amazonaws.com/bucket/object")
                .build();

        // Stubbing the response for the POST request
        stubFor(post(urlEqualTo("/api/v1/todo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(todo))));

        // Sending the HTTP POST request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(BASE_URL);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(objectMapper.writeValueAsString(todo)));
        CloseableHttpResponse httpResponse = httpClient.execute(request);

        // Converting the response to a string
        String stringResponse = EntityUtils.toString(httpResponse.getEntity());

        // Parsing the response content as JSON
        JsonNode actualJson = objectMapper.readTree(stringResponse);

        // Comparing the response with expected JSON
        JsonNode expectedJson = objectMapper.readTree(objectMapper.writeValueAsString(todo));
        Assertions.assertEquals(expectedJson, actualJson);

        verify(1, postRequestedFor(urlPathEqualTo("/api/v1/todo")).withRequestBody(equalTo(stringResponse)));
    }
}
