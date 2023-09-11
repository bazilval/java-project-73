package hexlet.code.controller;

import hexlet.code.dto.StatusDTO;
import hexlet.code.service.StatusService;
import hexlet.code.handler.FieldErrorHandler;
import hexlet.code.util.exception.EntityDeleteException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${base-url}" + "/statuses")
public class StatusController {
    @Autowired
    private StatusService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<StatusDTO> getStatuses() {
        List<StatusDTO> statuses = service.findAll();

        LOGGER.info("All statuses returned!");
        return statuses;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StatusDTO getStatus(@PathVariable("id") Long id) {
        StatusDTO statusDTO = service.findById(id);

        LOGGER.info("Status with id=" + id + " returned!");
        return statusDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StatusDTO createStatus(@RequestBody @Valid StatusDTO statusDTO, BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        StatusDTO savedStatusDTO = service.save(statusDTO);

        LOGGER.info("Status is saved with id=" + savedStatusDTO.getId());
        return savedStatusDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public StatusDTO updateStatus(
            @PathVariable("id") Long id,
            @RequestBody @Valid StatusDTO statusDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        StatusDTO updatedStatusDTO = service.update(id, statusDTO);

        LOGGER.info("Status with id=" + id + " updated!");
        return updatedStatusDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStatus(@PathVariable("id") Long id) {

        try {
            service.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDeleteException("Status", id);
        }

        LOGGER.info("Status with id=" + id + " deleted!");
    }
}
