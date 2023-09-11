package hexlet.code.controller;

import hexlet.code.dto.user.CreateUserDTO;
import hexlet.code.dto.user.ResponseUserDTO;
import hexlet.code.dto.user.UpdateUserDTO;
import hexlet.code.service.UserService;
import hexlet.code.handler.FieldErrorHandler;

import hexlet.code.util.exception.EntityDeleteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.List;

@RestController
@RequestMapping("${base-url}" + "/users")
public class UserController {
    @Autowired
    private UserService service;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseUserDTO> getUsers() {
        List<ResponseUserDTO> users = service.findAll();

        LOGGER.info("All users returned!");
        return users;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseUserDTO getUser(@PathVariable("id") Long id) {
        ResponseUserDTO userDTO = service.findById(id);

        LOGGER.info("User with id=" + id + " returned!");
        return userDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseUserDTO createUser(@RequestBody @Valid CreateUserDTO userDTO, BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseUserDTO savedUserDTO = service.save(userDTO);

        LOGGER.info("User is saved with id=" + savedUserDTO.getId());
        return savedUserDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseUserDTO updateUser(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateUserDTO userDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        ResponseUserDTO updatedUserDTO = service.update(id, userDTO);

        LOGGER.info("User with id=" + id + " updated!");
        return updatedUserDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {

        try {
            service.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDeleteException("User", id);
        }

        LOGGER.info("User with id=" + id + " deleted!");
    }
}
