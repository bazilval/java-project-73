package hexlet.code.service;

import hexlet.code.dto.LabelDTO;
import hexlet.code.mapper.LabelMapperImpl;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.exception.EntityExistsException;
import hexlet.code.util.exception.EntityNotFoundByNameException;
import hexlet.code.util.exception.EntityNotFoundException;
import hexlet.code.util.exception.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private LabelMapperImpl mapper;

    private final String entityName = "Label";

    @Transactional
    public LabelDTO save(LabelDTO dto) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        String name = dto.getName();
        Optional<Label> existingLabel = labelRepository.findByName(name);

        if (existingLabel.isPresent()) {
            throw new EntityExistsException(entityName, name);
        }

        Label label = mapper.map(dto);
        labelRepository.save(label);

        return mapper.map(label);
    }

    public List<LabelDTO> findAll() {
        List<Label> labels = labelRepository.findAll();
        if (labels.isEmpty()) {
            return List.of();
        }

        List<LabelDTO> labelsDTO = labels.stream()
                .map(label -> mapper.map(label))
                .collect(Collectors.toList());

        return labelsDTO;
    }

    public LabelDTO findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return mapper.map(label);
    }

    public LabelDTO findByName(String name) {
        Label label = labelRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundByNameException(entityName, name));

        return mapper.map(label);
    }

    @Transactional
    public LabelDTO update(Long id, LabelDTO data) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        mapper.update(data, label);
        Label updatedLabel = labelRepository.findById(id).get();

        return mapper.map(updatedLabel);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        labelRepository.delete(label);
    }

}
