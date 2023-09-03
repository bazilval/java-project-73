package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.AuthDTO;
import hexlet.code.dto.CreateUserDTO;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelToCreateGenerator;
import hexlet.code.util.ModelToUpdateGenerator;
import hexlet.code.util.NamedRoutes;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuthControllerTest {
    @Value("${base-url}")
    private String baseUrl;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserService service;
    @Autowired
    private ModelToCreateGenerator createGenerator;

    @Autowired
    private ModelToUpdateGenerator updateGenerator;
    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private NamedRoutes routes;

    @Test
    public void testLogin() throws Exception {
        CreateUserDTO data = Instancio.of(createGenerator.getUserModel()).create();

        var request = post(baseUrl + routes.usersPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request);

        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername(data.getEmail());
        authDTO.setPassword(data.getPassword());

        var loginRequest = post(baseUrl + routes.loginPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authDTO));

        var result = mockMvc.perform(loginRequest)
                .andExpect(status().isOk())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("jwtToken"));
    }

    @Test
    public void testLoginError() throws Exception {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("wrongLogin");
        authDTO.setPassword("wrongPassword");

        var loginRequest = post(baseUrl + routes.loginPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(authDTO));

        var result = mockMvc.perform(loginRequest)
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertTrue(result.getResponse().getContentAsString().contains("message"));
    }
}
