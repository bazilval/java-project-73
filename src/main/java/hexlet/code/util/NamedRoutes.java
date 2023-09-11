package hexlet.code.util;

import org.springframework.stereotype.Component;

@Component
public class NamedRoutes {

    private final String swaggerPath = "/swagger.html";
    private final String usersPath = "/users";
    private final String login = "/login";
    private final String statusesPath = "/statuses";
    private final String tasksPath = "/tasks";
    private final String labelsPath = "/labels";

    public String usersPath() {
        return usersPath;
    }

    public String userPath(String id) {
        return usersPath + "/" + id;
    }

    public String userPath(Long id) {
        return userPath(String.valueOf(id));
    }

    public String loginPath() {
        return login;
    }

    public String statusesPath() {
        return statusesPath;
    }

    public String statusPath(String id) {
        return statusesPath + "/" + id;
    }

    public String statusPath(Long id) {
        return statusPath(String.valueOf(id));
    }

    public String tasksPath() {
        return tasksPath;
    }

    public String taskPath(String id) {
        return tasksPath + "/" + id;
    }

    public String taskPath(Long id) {
        return taskPath(String.valueOf(id));
    }

    public String labelsPath() {
        return labelsPath;
    }

    public String labelPath(String id) {
        return labelsPath + "/" + id;
    }

    public String labelPath(Long id) {
        return labelPath(String.valueOf(id));
    }
    public String swaggerPath() {
        return swaggerPath;
    }
}
