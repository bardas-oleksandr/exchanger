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
import ua.pb.model.Operation;
import ua.pb.service.OperationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(value = "/rest/operation",
        consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class OperationRestController {

    private static final String ID = "/{id}";

    @Autowired
    private OperationService operationService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    OperationViewDto create(@Valid @RequestBody OperationCreateDto operationCreateDto,
                            BindingResult result, HttpServletRequest request) {
        controllerUtils.checkValidationViolations(result);
        int userId = controllerUtils.extractUserId(request);
        try {
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

    @GetMapping(value = ID)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    OperationViewDto get(@PathVariable("id") int operationId) {
        try {
            return operationService.getById(operationId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + Operation.class)) {
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
    public @ResponseBody
    OperationViewDto updateDeletedState(@PathVariable("id") int operationId, HttpServletRequest request) {
        try {
            String isDeleted = request.getParameter("isDeleted");
            if(isDeleted != null && isDeleted.equals("on")){
                return operationService.updateDeletedState(operationId, true);
            }else{
                return operationService.updateDeletedState(operationId, false);
            }
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_OPERATION"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties
                    .getProperty("FAILED_UPDATE_OPERATION_NONEXISTENT"))) {
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
    List<OperationViewDto> getAll() {
        try {
            List<OperationViewDto> list = operationService.getAll();
            if (list.size() == 0) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND,
                        properties.getProperty("EMPTY_RESULTSET") + Operation.class);
            }
            return list;
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
