package hexlet.code.service;

import hexlet.code.dto.CreateUserDTO;
import hexlet.code.dto.ResponseUserDTO;
import hexlet.code.dto.UpdateUserDTO;
import hexlet.code.mapper.UserMapperImpl;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.exception.PermissionDeniedException;
import hexlet.code.util.exception.UserExistsExeption;
import hexlet.code.util.exception.UserNotFoundByEmailException;
import hexlet.code.util.exception.UserNotFoundException;
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

    @Transactional
    public ResponseUserDTO save(CreateUserDTO userDTO) {
        String email = userDTO.getEmail();
        Optional<User> existingUser = repository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new UserExistsExeption(email);
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
                .orElseThrow(() -> new UserNotFoundException(id));

        return mapper.map(user);
    }

    public ResponseUserDTO findByEmail(String email) {
        User user = repository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailException(email));

        return mapper.map(user);
    }

    @Transactional
    public ResponseUserDTO update(Long id, UpdateUserDTO data) {
        User user = repository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!authService.hasPermissions(user)) {
            throw new PermissionDeniedException();
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
                        .orElseThrow(() -> new UserNotFoundException(id));

        if (!authService.hasPermissions(user)) {
            throw new PermissionDeniedException();
        }

        repository.delete(user);
    }
}
