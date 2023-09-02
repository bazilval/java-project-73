package hexlet.code;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UpdateUserDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class JacksonConfigTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserMapper userMapper;

    @Test
    void nullableModuleTest() throws JsonProcessingException {
        UpdateUserDTO dto1 = mapper.readValue("{\"firstName\":\"Jack\"}", UpdateUserDTO.class);
        User user = userMapper.map(dto1);
        assertEquals(JsonNullable.of("Jack"), dto1.getFirstName());

        UpdateUserDTO dto2 = mapper.readValue("{\"firstName\":null}", UpdateUserDTO.class);
        assertEquals(JsonNullable.of(null), dto2.getFirstName());

        assertNull(mapper.readValue("{}", UpdateUserDTO.class).getFirstName());
    }
}
