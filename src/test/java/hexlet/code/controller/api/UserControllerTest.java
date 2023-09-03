package hexlet.code.controller.api;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;


import hexlet.code.dto.CreateUserDTO;
import hexlet.code.dto.ResponseUserDTO;
import hexlet.code.dto.UpdateUserDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.security.JWTUtils;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelToCreateGenerator;
import hexlet.code.util.ModelToUpdateGenerator;
import hexlet.code.util.NamedRoutes;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.instancio.Instancio;
import org.openapitools.jackson.nullable.JsonNullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerTest {
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
    private JWTUtils jwtUtils;

    @Autowired
    private NamedRoutes routes;
    private String token;


    @BeforeEach
    public void setUp() {
        CreateUserDTO testUser = Instancio.of(createGenerator.getUserModel()).create();
        token = "Bearer " + jwtUtils.generateToken(testUser.getEmail());
        service.save(testUser);
    }

    @Test
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get(baseUrl + routes.usersPath()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        CreateUserDTO data = Instancio.of(createGenerator.getUserModel()).create();

        var request = post(baseUrl + routes.usersPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isCreated());

        var user = repository.findByEmail(data.getEmail()).get();

        assertNotNull(user);
        assertEquals(data.getFirstName(), user.getFirstName());
        assertNotEquals(data.getPassword(), user.getPassword());
    }

    @Test
    public void testOneFieldCreateError() throws Exception {
        String updateJSON = "{\"firstName\":null}";

        var request = post(baseUrl + routes.usersPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJSON);

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Lastname cannot be empty"));
        assertTrue(body.contains("Email cannot be empty"));
        assertTrue(body.contains("Password cannot be empty"));
    }

    @Test
    public void testCreateError() throws Exception {
        CreateUserDTO data = Instancio.of(createGenerator.getUserModel()).create();
        data.setPassword(("12"));
        data.setEmail(("notEmail"));

        var request = post(baseUrl + routes.usersPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Email has to be correct"));
        assertTrue(body.contains("Password has to contain at least 3 symbols"));
    }

    @Test
    public void testShow() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);

        var request = get(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(user.getFirstName()),
                v -> v.node("lastName").isEqualTo(user.getLastName()),
                v -> v.node("email").isEqualTo(user.getEmail())
        );
    }

    @Test
    public void testUpdate() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);
        token = "Bearer " + jwtUtils.generateToken(user.getEmail());

        UpdateUserDTO data = Instancio.of(updateGenerator.getUserModel()).create();

        var request = put(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        User updatedUser = repository.findById(user.getId()).orElse(null);

        assertEquals(data.getFirstName().get(), updatedUser.getFirstName());
        assertEquals(data.getLastName().get(), updatedUser.getLastName());
        assertEquals(data.getEmail().get(), updatedUser.getEmail());
        assertNotEquals(data.getPassword().get(), updatedUser.getPassword());
    }

    @Test
    public void testUpdateOneField() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);
        token = "Bearer " + jwtUtils.generateToken(user.getEmail());

        String updateJSON = "{\"firstName\":\"test\"}";

        var request = put(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJSON);

        mockMvc.perform(request).andExpect(status().isOk());

        User updatedUser = repository.findById(user.getId()).orElse(null);

        assertEquals("test", updatedUser.getFirstName());
    }

    @Test
    public void testUpdateError() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);
        token = "Bearer " + jwtUtils.generateToken(user.getEmail());

        UpdateUserDTO data = Instancio.of(updateGenerator.getUserModel()).create();
        data.setPassword(JsonNullable.of("12"));
        data.setEmail(JsonNullable.of("notEmail"));

        var request = put(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Email has to be correct"));
        assertTrue(body.contains("Password has to contain at least 3 symbols"));
    }

    public void testUpdateAnotherUser() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);

        UpdateUserDTO data = Instancio.of(updateGenerator.getUserModel()).create();
        data.setPassword(JsonNullable.of("12"));
        data.setEmail(JsonNullable.of("notEmail"));

        var request = put(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Permission denied!"));
    }

    @Test
    public void testDelete() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);
        token = "Bearer " + jwtUtils.generateToken(user.getEmail());

        var request = delete(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(request).andExpect(status().isNoContent());

        User deletedUser = repository.findById(user.getId()).orElse(null);

        assertNull(deletedUser);
    }

    @Test
    public void testDeleteAnotherUser() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        ResponseUserDTO user = service.save(userDTO);

        var request = delete(baseUrl + routes.userPath(user.getId()))
                .header(HttpHeaders.AUTHORIZATION, token);

        var result = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Permission denied!"));
    }
}
