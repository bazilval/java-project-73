package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.CreateUserDTO;
import hexlet.code.dto.StatusDTO;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.StatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.security.JWTUtils;
import hexlet.code.service.StatusService;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelToCreateGenerator;
import hexlet.code.util.NamedRoutes;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class StatusControllerTest {
    @Value("${base-url}")
    private String baseUrl;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusService statusService;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelToCreateGenerator createGenerator;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private JWTUtils jwtUtils;
    private String token;
    private Long statusId;
    private Long userId;


    @BeforeEach
    public void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();

        CreateUserDTO testUser = Instancio.of(createGenerator.getUserModel()).create();
        token = "Bearer " + jwtUtils.generateToken(testUser.getEmail());
        userId = userService.save(testUser)
                .getId();

        Status data = new Status("Initial");

        statusId = statusRepository.save(data)
                .getId();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get(baseUrl + NamedRoutes.statusesPath())
                .header(HttpHeaders.AUTHORIZATION, token);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        StatusDTO data = new StatusDTO("Новый");

        var request = post(baseUrl + NamedRoutes.statusesPath())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isCreated());

        Status status = statusRepository.findByName(data.getName()).get();

        assertNotNull(status);
        assertEquals(data.getName(), status.getName());
    }

    @Test
    public void testCreateError() throws Exception {
        StatusDTO data = new StatusDTO("");

        var request = post(baseUrl + NamedRoutes.statusesPath())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Status name can not be empty"));
    }

    @Test
    public void testCreateNoAuth() throws Exception {
        StatusDTO data = new StatusDTO("Новый");

        var request = post(baseUrl + NamedRoutes.statusesPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        var request = get(baseUrl + NamedRoutes.statusPath(statusId))
                .header(HttpHeaders.AUTHORIZATION, token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Initial")
        );
    }

    @Test
    public void testUpdate() throws Exception {
        StatusDTO data = new StatusDTO("Новый");

        var request = put(baseUrl + NamedRoutes.statusPath(statusId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        Status updatedStatus = statusRepository.findById(statusId).orElse(null);

        assertEquals(data.getName(), updatedStatus.getName());
    }

    @Test
    public void testUpdateError() throws Exception {
        StatusDTO data = new StatusDTO("");

        var request = put(baseUrl + NamedRoutes.statusPath(statusId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Status name can not be empty"));
    }

    public void testUpdateNoAuth() throws Exception {
        StatusDTO data = new StatusDTO("Новый");

        var request = put(baseUrl + NamedRoutes.statusPath(statusId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete(baseUrl + NamedRoutes.statusPath(statusId))
                .header(HttpHeaders.AUTHORIZATION, token);

        var result = mockMvc.perform(request).andReturn();

        Status deletedStatus = statusRepository.findById(statusId).orElse(null);

        assertNull(deletedStatus);
    }

    @Test
    public void testDeleteNoAuth() throws Exception {
        var request = delete(baseUrl + NamedRoutes.statusPath(statusId));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteWithTask() throws Exception {
        User createdUser = userRepository.findById(userId).get();
        Status createdStatus = statusRepository.findById(statusId).get();

        Task task = new Task();
        task.setName("Test");
        task.setTaskStatus(createdStatus);
        task.setAuthor(createdUser);

        taskRepository.save(task);

        var request = delete(baseUrl + NamedRoutes.statusPath(statusId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(request).andExpect(status().isConflict());

        Status deletedStatus = statusRepository.findById(statusId).orElse(null);

        assertNotNull(deletedStatus);
    }
}
