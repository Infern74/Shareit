package ru.practicum.shareit.gateway.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.dto.user.UserCreateDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserCreateDtoTest {

    @Autowired
    private JacksonTester<UserCreateDto> json;

    @Test
    void testSerialize() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("test", "test@test.com");

        var result = json.write(userCreateDto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("test");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("test@test.com");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"name\":\"test\",\"email\":\"test@test.com\"}";

        UserCreateDto result = json.parseObject(content);

        assertThat(result.getName()).isEqualTo("test");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }
}