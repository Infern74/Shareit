package ru.practicum.shareit.gateway.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.user.UserCreateDto;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Autowired
    private UserClient userClient;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void createUser() {
        UserCreateDto userCreateDto = new UserCreateDto("test", "test@test.com");
        String responseBody = "{\"id\":1,\"name\":\"test\",\"email\":\"test@test.com\"}";

        server.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"name\":\"test\",\"email\":\"test@test.com\"}"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        userClient.createUser(userCreateDto);
        server.verify();
    }
}