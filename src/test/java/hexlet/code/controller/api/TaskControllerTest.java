package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.CreateTaskDTO;
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
import hexlet.code.service.TaskService;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
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
public class TaskControllerTest {
    @Value("${base-url}")
    private String baseUrl;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskService taskService;
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
    private Long userId;
    private Long statusId;
    private Long taskId;


    @BeforeEach
    public void setUp() throws Exception {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        statusRepository.deleteAll();
        labelRepository.deleteAll();

        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        token = "Bearer " + jwtUtils.generateToken(userDTO.getEmail());
        userId = userService.save(userDTO).getId();
        User user = userRepository.findById(userId).get();

        Status status = new Status("В работе");
        statusId = statusRepository.save(status)
                .getId();

        Task task = new Task();
        task.setName("TaskWithoutLabel");
        task.setTaskStatus(status);
        task.setAuthor(user);

        taskRepository.save(task);
        taskId = task.getId();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get(baseUrl + routes.tasksPath())
                .header(HttpHeaders.AUTHORIZATION, token);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray();
    }

    @Test
    public void testIndexWithFilters() throws Exception {
        Label label = new Label("test");
        labelRepository.save(label);

        Status status = statusRepository.findById(statusId).get();
        User user = userRepository.findById(userId).get();

        Set<Label> labels = Set.of(label);

        Task task = new Task();
        task.setName("TaskWithLabel");
        task.setTaskStatus(status);
        task.setLabels(labels);
        task.setAuthor(user);

        taskRepository.save(task);

        var request = get(baseUrl + routes.tasksPath())
                .queryParam("labelsId", label.getId().toString())
                .header(HttpHeaders.AUTHORIZATION, token);
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
        assertFalse(body.contains("TaskWithoutLabel"));
        assertTrue(body.contains("TaskWithLabel"));
    }

    @Test
    public void testCreate() throws Exception {
        CreateTaskDTO data = new CreateTaskDTO();
        data.setName("Task name");
        data.setDescription("Task description");
        data.setExecutorId(userId);
        data.setTaskStatusId(statusId);

        var request = post(baseUrl + routes.tasksPath())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request).andExpect(status().isCreated());

        Task task = taskRepository.findByName(data.getName()).get();

        assertNotNull(task);
        assertEquals(data.getName(), task.getName());
        assertEquals(data.getDescription(), task.getDescription());
    }

    @Test
    public void testCreateError() throws Exception {
        CreateTaskDTO data = new CreateTaskDTO();
        data.setDescription("Task description");

        var request = post(baseUrl + routes.tasksPath())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Name cannot be empty"));
        assertTrue(body.contains("Task status id cannot be empty"));
    }

    @Test
    public void testCreateNoAuth() throws Exception {
        CreateTaskDTO data = new CreateTaskDTO();
        data.setName("Task name");
        data.setTaskStatusId(statusId);

        var request = post(baseUrl + routes.tasksPath())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testShow() throws Exception {
        var request = get(baseUrl + routes.taskPath(taskId))
                .header(HttpHeaders.AUTHORIZATION, token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("TaskWithoutLabel")
        );
    }

    @Test
    public void testUpdate() throws Exception {
        String updateJSON = "{\"name\":\"test\", \"description\":\"test\"}";

        var request = put(baseUrl + routes.taskPath(taskId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJSON);

        mockMvc.perform(request).andExpect(status().isOk());

        Task updatedTask = taskRepository.findById(taskId).orElse(null);

        assertEquals("test", updatedTask.getName());
        assertEquals("test", updatedTask.getDescription());
    }

    @Test
    public void testUpdateError() throws Exception {
        String updateJSON = "{\"name\":\"\"}";

        var request = put(baseUrl + routes.taskPath(taskId))
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJSON);

        var result = mockMvc.perform(request)
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Name has to contain at least 1 symbol"));
    }

    public void testUpdateNoAuth() throws Exception {
        String updateJSON = "{\"name\":\"test\", \"description\":\"test\"}";

        var request = put(baseUrl + routes.taskPath(taskId))
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJSON);

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete(baseUrl + routes.taskPath(taskId))
                .header(HttpHeaders.AUTHORIZATION, token);

        mockMvc.perform(request).andExpect(status().isNoContent());

        Task deletedTask = taskRepository.findById(statusId).orElse(null);

        assertNull(deletedTask);
    }

    @Test
    public void testDeleteNoAuth() throws Exception {
        var request = delete(baseUrl + routes.taskPath(taskId));

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDeleteNotCreator() throws Exception {
        CreateUserDTO userDTO = Instancio.of(createGenerator.getUserModel()).create();
        String anotherToken = "Bearer " + jwtUtils.generateToken(userDTO.getEmail());
        userService.save(userDTO);

        var request = delete(baseUrl + routes.taskPath(taskId))
                .header(HttpHeaders.AUTHORIZATION, anotherToken);

        var result = mockMvc.perform(request)
                .andExpect(status().isForbidden())
                .andReturn();
        String body = result.getResponse().getContentAsString();

        assertTrue(body.contains("Permission denied!"));
    }
}
