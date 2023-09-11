package hexlet.code.controller;

import hexlet.code.dto.LabelDTO;
import hexlet.code.handler.FieldErrorHandler;
import hexlet.code.service.LabelService;
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
@RequestMapping("${base-url}" + "/labels")
public class LabelController {
    @Autowired
    private LabelService service;
    private static final Logger LOGGER = LoggerFactory.getLogger(LabelController.class);

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<LabelDTO> getLabeles() {
        List<LabelDTO> labels = service.findAll();

        LOGGER.info("All labels returned!");
        return labels;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelDTO getLabel(@PathVariable("id") Long id) {
        LabelDTO labelDTO = service.findById(id);

        LOGGER.info("Label with id=" + id + " returned!");
        return labelDTO;

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDTO createLabel(@RequestBody @Valid LabelDTO labelDTO, BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        LabelDTO savedLabelDTO = service.save(labelDTO);

        LOGGER.info("Label is saved with id=" + savedLabelDTO.getId());
        return savedLabelDTO;
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public LabelDTO updateLabel(
            @PathVariable("id") Long id,
            @RequestBody @Valid LabelDTO labelDTO,
            BindingResult bindingResult) {
        FieldErrorHandler.handleErrors(bindingResult);

        LabelDTO updatedLabelDTO = service.update(id, labelDTO);

        LOGGER.info("Label with id=" + id + " updated!");
        return updatedLabelDTO;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(@PathVariable("id") Long id) {

        try {
            service.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new EntityDeleteException("Label", id);
        }

        LOGGER.info("Label with id=" + id + " deleted!");
    }
}
