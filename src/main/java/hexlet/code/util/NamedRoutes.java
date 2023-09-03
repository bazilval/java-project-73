package hexlet.code.util;

import org.springframework.stereotype.Component;

@Component
public class NamedRoutes {

    private String path = "/users";
    private String login = "/login";

    public String usersPath() {
        return path;
    }

    public String userPath(String id) {
        return path + "/" + id;
    }

    public String userPath(Long id) {
        return userPath(String.valueOf(id));
    }

    public String loginPath() {
        return login;
    }
}
