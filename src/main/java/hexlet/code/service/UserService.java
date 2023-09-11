package hexlet.code.service;

import hexlet.code.dto.user.CreateUserDTO;
import hexlet.code.dto.user.ResponseUserDTO;
import hexlet.code.dto.user.UpdateUserDTO;
import hexlet.code.mapper.UserMapperImpl;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.exception.EntityExistsException;
import hexlet.code.util.exception.EntityNotFoundByNameException;
import hexlet.code.util.exception.EntityNotFoundException;
import hexlet.code.util.exception.PermissionDeniedException;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserService {
    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserMapperImpl mapper;

    @Autowired
    private PasswordEncoder encoder;

    private final String entityName = "User";

    @Transactional
    public ResponseUserDTO save(CreateUserDTO userDTO) {
        String email = userDTO.getEmail();
        Optional<User> existingUser = repository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new EntityExistsException(entityName, email);
        }

        User user = mapper.map(userDTO);

        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        repository.save(user);

        return mapper.map(user);
    }

    public List<ResponseUserDTO> findAll() {
        List<User> users = repository.findAll();
        if (users.isEmpty()) {
            return List.of();
        }

        List<ResponseUserDTO> usersDTO = users.stream()
                .map(user -> mapper.map(user))
                .collect(Collectors.toList());

        return usersDTO;
    }

    public ResponseUserDTO findById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        return mapper.map(user);
    }

    public ResponseUserDTO findByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundByNameException(entityName, email));

        return mapper.map(user);
    }

    @Transactional
    public ResponseUserDTO update(Long id, UpdateUserDTO data) {
        User user = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        if (!authService.hasPermissions(user)) {
            throw new PermissionDeniedException();
        }

        if (data.getEmail() != null && data.getEmail().isPresent()) {
            String email = data.getEmail().get();
            Optional<User> existingUser = repository.findByEmail(email);
            if (existingUser.isPresent()) {
                throw new EntityExistsException(entityName, email);
            }
        }

        if (data.getPassword() != null && data.getPassword().isPresent()) {
            String encodedPassword = encoder.encode(data.getPassword().get());
            data.setPassword(JsonNullable.of(encodedPassword));
        }

        mapper.update(data, user);
        User updatedUser = repository.findById(id).get();

        return mapper.map(updatedUser);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = repository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(entityName, id));

        if (!authService.hasPermissions(user)) {
            throw new PermissionDeniedException();
        }

        repository.delete(user);
    }
}
