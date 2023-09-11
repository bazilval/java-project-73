package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.user.CreateUserDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Status;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
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

import java.util.Set;

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
public class LabelControllerTest {
    @Value("${base-url}")
    private String baseUrl;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LabelRepository labelRepository;
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
    @Autowired
    private NamedRoutes routes;
    private String token;
    private Long labelId;
    private Long statusId;
    private Long taskId;
    private Long userId;


    @BeforeEach
    public void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();

        CreateUserDTO testUser = Instancio.of(createGenerator.getUserModel()).create();
        token = "Bearer " + jwtUtils.generateToken(testUser.getEmail());
        userId = userService.save(testUser)
                .getId();

        Status status = new Status("Initial");
        statusId = statusRepository.save(status)
                .getId();

        Label label = new Label("Initial");
        labelId = labelRepository.save(label)
                .getId();
    }

    @Test
    public void testIndex() throws Exception {
        MvcResult result = mockMvc.perform(get(baseUrl + routes.labelsPath()))
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testCreate() throws Exception {
        LabelDTO data = new LabelDTO("Новый");

        var request = post(baseUrl + routes.labelsPath())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isCreated());

        Label label = labelRepository.findByName(data.getName()).get();

        assertNotNull(label);
        assertEquals(data.getName(), label.getName());
    }

    @Test
    public void testCreateError() throws Exception {
        LabelDTO data = new LabelDTO("");

        var request = post(baseUrl + routes.labelsPath())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Label name can not be empty"));
    }

    @Test
    public void testCreateNoAuth() throws Exception {
        LabelDTO data = new LabelDTO("Новый");

        var request = post(baseUrl + routes.labelsPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        var request = get(baseUrl + routes.labelPath(labelId));
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
        LabelDTO data = new LabelDTO("Новый");

        var request = put(baseUrl + routes.labelPath(labelId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isOk());

        Label updatedLabel = labelRepository.findById(labelId).orElse(null);

        assertEquals(data.getName(), updatedLabel.getName());
    }

    @Test
    public void testUpdateError() throws Exception {
        LabelDTO data = new LabelDTO("");

        var request = put(baseUrl + routes.labelPath(labelId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Label name can not be empty"));
    }

    public void testUpdateNoAuth() throws Exception {
        LabelDTO data = new LabelDTO("Новый");

        var request = put(baseUrl + routes.labelPath(labelId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete(baseUrl + routes.labelPath(labelId))
                .header(HttpHeaders.AUTHORIZATION, token);

        var result = mockMvc.perform(request).andReturn();

        Label deletedLabel = labelRepository.findById(labelId).orElse(null);

        assertNull(deletedLabel);
    }

    @Test
    public void testDeleteNoAuth() throws Exception {
        var request = delete(baseUrl + routes.labelPath(labelId));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteWithTask() throws Exception {
        User createdUser = userRepository.findById(userId).get();
        Status createdStatus = statusRepository.findById(statusId).get();
        Label createdLabel = labelRepository.findById(labelId).get();

        Task task = new Task();
        task.setName("Test");
        task.setTaskStatus(createdStatus);
        task.setLabels(Set.of(createdLabel));
        task.setAuthor(createdUser);

        taskRepository.save(task);

        var request = delete(baseUrl + routes.labelPath(labelId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(request).andExpect(status().isConflict());

        Label deletedLabel = labelRepository.findById(labelId).orElse(null);

        assertNotNull(deletedLabel);
    }
}
