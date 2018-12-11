package ua.pb.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.OperationCreateDto;
import ua.pb.dto.view.OperationViewDto;
import ua.pb.exception.ApplicationException;
import ua.pb.exception.RestException;
import ua.pb.service.OperationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Properties;


@RestController
@RequestMapping(value = "/rest/operator",
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE})
public class OperatorRestController {

    private static final String OPERATION = "/operation";

    @Autowired
    private OperationService operationService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @GetMapping(value = OPERATION)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<OperationViewDto> getOperationsForUser(HttpServletRequest request) {
        try {
            int userId = controllerUtils.extractUserId(request);
            return operationService.getAllByUserId(userId);
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(value = OPERATION)
    @ResponseStatus(HttpStatus.CREATED)
    public OperationViewDto createOperation(@Valid @RequestBody OperationCreateDto operationCreateDto
            , BindingResult result, HttpServletRequest request) {
        controllerUtils.checkValidationViolations(result);
        try {
            int userId = controllerUtils.extractUserId(request);
            return operationService.create(operationCreateDto, userId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }
}
