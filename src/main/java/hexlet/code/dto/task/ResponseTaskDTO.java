package hexlet.code.dto.task;

import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.StatusDTO;
import hexlet.code.dto.user.ResponseUserDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ResponseTaskDTO {
    private Long id;
    private String name;
    private String description;
    private ResponseUserDTO author;
    private ResponseUserDTO executor;
    private List<LabelDTO> labels;
    private StatusDTO taskStatus;
    private String createdAt;
}
