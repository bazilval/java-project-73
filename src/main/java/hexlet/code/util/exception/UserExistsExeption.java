package hexlet.code.util.exception;

public class UserExistsExeption extends RuntimeException {
    public UserExistsExeption(String email) {
        super("User with email " + email + " already exists!");
    }
}
