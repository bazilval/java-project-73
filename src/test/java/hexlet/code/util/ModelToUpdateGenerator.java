package hexlet.code.util;

import hexlet.code.dto.UpdateUserDTO;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelToUpdateGenerator {
    private Model<UpdateUserDTO> userModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(UpdateUserDTO.class)
                .supply(Select.field(UpdateUserDTO::getFirstName), () -> JsonNullable.of(faker.name().firstName()))
                .supply(Select.field(UpdateUserDTO::getLastName), () -> JsonNullable.of(faker.name().lastName()))
                .supply(Select.field(UpdateUserDTO::getEmail), () -> JsonNullable.of(faker.internet().emailAddress()))
                .supply(Select.field(UpdateUserDTO::getPassword), () -> JsonNullable.of(faker.internet().password()))
                .toModel();
    }
}
