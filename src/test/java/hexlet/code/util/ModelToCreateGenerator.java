package hexlet.code.util;

import hexlet.code.dto.user.CreateUserDTO;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelToCreateGenerator {
    private Model<CreateUserDTO> userModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(CreateUserDTO.class)
                .supply(Select.field(CreateUserDTO::getFirstName), () -> faker.name().firstName())
                .supply(Select.field(CreateUserDTO::getLastName), () -> faker.name().lastName())
                .supply(Select.field(CreateUserDTO::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(CreateUserDTO::getPassword), () -> faker.internet().password())
                .toModel();
    }
}
