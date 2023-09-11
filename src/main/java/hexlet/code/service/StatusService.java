package hexlet.code.service;

import hexlet.code.dto.StatusDTO;
import hexlet.code.mapper.StatusMapperImpl;
import hexlet.code.model.Status;
import hexlet.code.repository.StatusRepository;
import hexlet.code.util.exception.PermissionDeniedException;
import hexlet.code.util.exception.EntityExistsException;
import hexlet.code.util.exception.EntityNotFoundByNameException;
import hexlet.code.util.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private StatusMapperImpl mapper;

    private final String entityName = "Status";

    @Transactional
    public StatusDTO save(StatusDTO dto) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        String name = dto.getName();
        Optional<Status> existingStatus = statusRepository.findByName(name);

        if (existingStatus.isPresent()) {
            throw new EntityExistsException(entityName, name);
        }

        Status status = mapper.map(dto);
        statusRepository.save(status);

        return mapper.map(status);
    }

    public List<StatusDTO> findAll() {
        List<Status> statuses = statusRepository.findAll();
        if (statuses.isEmpty()) {
            return List.of();
        }

        List<StatusDTO> statusesDTO = statuses.stream()
                .map(status -> mapper.map(status))
                .collect(Collectors.toList());

        return statusesDTO;
    }

    public StatusDTO findById(Long id) {
        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return mapper.map(status);
    }

    public StatusDTO findByName(String name) {
        Status status = statusRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundByNameException(entityName, name));

        return mapper.map(status);
    }

    @Transactional
    public StatusDTO update(Long id, StatusDTO data) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        String name = data.getName();
        Optional<Status> existingStatus = statusRepository.findByName(name);

        if (existingStatus.isPresent()) {
            throw new EntityExistsException(entityName, name);
        }

        mapper.update(data, status);
        Status updatedStatus = statusRepository.findById(id).get();

        return mapper.map(updatedStatus);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!authService.isAuthenticated()) {
            throw new PermissionDeniedException();
        }

        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        statusRepository.delete(status);
    }

}
