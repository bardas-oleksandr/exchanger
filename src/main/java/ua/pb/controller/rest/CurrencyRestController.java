package ua.pb.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.dto.view.CurrencyViewDto;
import ua.pb.exception.ApplicationException;
import ua.pb.exception.RestException;
import ua.pb.model.Currency;
import ua.pb.service.CurrencyService;

import javax.validation.Valid;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(value = "/rest/currency",
        consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CurrencyRestController {

    private static final String ID = "/{id}";

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    CurrencyViewDto create(@Valid @RequestBody CurrencyCreateDto currencyCreateDto,
                           BindingResult result) {
        controllerUtils.checkValidationViolations(result);
        try {
            return currencyService.create(currencyCreateDto);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"))
                    || message.equals(properties.getProperty("NOT_UNIQUE_CURRENCY"))) {
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
    CurrencyViewDto get(@PathVariable("id") int currencyId) {
        try {
            return currencyService.getById(currencyId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + Currency.class)) {
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
    CurrencyViewDto update(@Valid @RequestBody CurrencyCreateDto currencyCreateDto,
                               BindingResult result, @PathVariable("id") int currencyId) {
        controllerUtils.checkValidationViolations(result);
        try {
            return currencyService.update(currencyCreateDto, currencyId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties
                    .getProperty("DATA_INTEGRITY_VIOLATION_FOR_CURRENCY"))
                    || message.equals(properties.getProperty("NOT_UNIQUE_CURRENCY"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties
                    .getProperty("FAILED_UPDATE_CURRENCY_NONEXISTENT"))) {
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
    public void delete(@PathVariable("id") int currencyId) {
        try {
            currencyService.delete(currencyId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties
                    .getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_CURRENCY"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties
                    .getProperty("FAILED_UPDATE_CURRENCY_NONEXISTENT"))) {
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
    List<CurrencyViewDto> getAll() {
        try {
            List<CurrencyViewDto> list = currencyService.getAll();
            if (list.size() == 0) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND,
                        properties.getProperty("EMPTY_RESULTSET") + Currency.class);
            }
            return list;
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
