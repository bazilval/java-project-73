package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@NoArgsConstructor
public class LabelDTO {
    private Long id;

    @NotBlank(message = "Label name can not be empty")
    @Length(min = 1, message = "Label name has to contain at least 1 symbol")
    private String name;

    private String createdAt;

    public LabelDTO(String name) {
        this.name = name;
    }
}
