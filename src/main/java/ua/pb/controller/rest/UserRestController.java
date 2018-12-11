package ua.pb.controller.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.UserCreateDto;
import ua.pb.dto.create.UserUpdateDto;
import ua.pb.dto.view.UserViewDto;
import ua.pb.exception.ApplicationException;
import ua.pb.exception.RestException;
import ua.pb.model.User;
import ua.pb.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(value = "/rest/user",
        consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class UserRestController {

    private static final Logger logger = LogManager.getLogger();

    private static final String ID = "/{id}";

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody UserCreateDto userCreateDto, BindingResult result) {
        controllerUtils.checkValidationViolations(result);
        try {
            userService.create(userCreateDto);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("NOT_UNIQUE_USER"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }

    @GetMapping(value = ID)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    UserViewDto get(@PathVariable("id") int userId) {
        try {
            return userService.getUserById(userId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + User.class)) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }

    @PutMapping(value = ID)
    @ResponseStatus(HttpStatus.OK)
    public UserViewDto update(@Valid @RequestBody UserUpdateDto userUpdateDto, BindingResult result
            , @PathVariable("id") int userId) {
        controllerUtils.checkValidationViolations(result);
        try {
            return userService.update(userUpdateDto, userId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("NOT_UNIQUE_USER"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties.getProperty("FAILED_UPDATE_USER_NONEXISTENT"))) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }

    @DeleteMapping(value = ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int userId) {
        try {
            userService.delete(userId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("FAILED_DELETE_USER_NONEXISTENT"))) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<UserViewDto> getAll() {
        logger.info("UserRestController is working");
        try {
            return userService.getAllUsers();
        } catch (ApplicationException e) {
            logger.error("UserRestController caught an ApplicationException: " + e.getMessage());
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
