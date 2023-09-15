package hexlet.code.util;

import org.springframework.stereotype.Component;

@Component
public class NamedRoutes {

    private static final String USERS_PATH = "/users";
    private static final String LOGIN = "/login";
    private static final String STATUSES_PATH = "/statuses";
    private static final String TASKS_PATH = "/tasks";
    private static final String LABELS_PATH = "/labels";

    public static String usersPath() {
        return USERS_PATH;
    }

    public static String userPath(String id) {
        return USERS_PATH + "/" + id;
    }

    public static String userPath(Long id) {
        return userPath(String.valueOf(id));
    }

    public static String loginPath() {
        return LOGIN;
    }

    public static String statusesPath() {
        return STATUSES_PATH;
    }

    public static String statusPath(String id) {
        return STATUSES_PATH + "/" + id;
    }

    public static String statusPath(Long id) {
        return statusPath(String.valueOf(id));
    }

    public static String tasksPath() {
        return TASKS_PATH;
    }

    public static String taskPath(String id) {
        return TASKS_PATH + "/" + id;
    }

    public static String taskPath(Long id) {
        return taskPath(String.valueOf(id));
    }

    public static String labelsPath() {
        return LABELS_PATH;
    }

    public static String labelPath(String id) {
        return LABELS_PATH + "/" + id;
    }

    public static String labelPath(Long id) {
        return labelPath(String.valueOf(id));
    }
}
