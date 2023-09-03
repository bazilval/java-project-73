package hexlet.code.component;

import hexlet.code.dto.CreateUserDTO;
import hexlet.code.service.UserService;
import hexlet.code.util.exception.UserExistsExeption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {
    @Autowired
    private UserService userService;
    public void run(ApplicationArguments args) {
        CreateUserDTO userDTO = new CreateUserDTO();
        userDTO.setFirstName("Test");
        userDTO.setLastName("Testov");
        userDTO.setEmail("test@mail.com");
        userDTO.setPassword("test");

        try {
            userService.save(userDTO);
        } catch (UserExistsExeption e) {
            return;
        }
    }
}
