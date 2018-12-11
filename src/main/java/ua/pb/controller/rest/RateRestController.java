package ua.pb.controller.rest;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.RateCreateDto;
import ua.pb.dto.view.RateViewDto;
import ua.pb.exception.ApplicationException;
import ua.pb.exception.RestException;
import ua.pb.model.Currency;
import ua.pb.model.Rate;
import ua.pb.service.RateService;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;

@RestController
@RequestMapping(value = "/rest/rate",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class RateRestController {

    private static final String ID = "/{id}";
    private static final String ACTUAL = "/actual";
    private static final String SYNCHRONIZE = "/synchronize";
    private static final String ALL = "/all";
    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";

    @Autowired
    private RateService rateService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    @Autowired
    private Gson gson;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    RateViewDto create(@Valid @RequestBody RateCreateDto rateCreateDto, BindingResult result) {
        controllerUtils.checkValidationViolations(result);
        try {
            return rateService.create(rateCreateDto);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"))
                    || message.equals(properties.getProperty("EMPTY_RESULTSET") + Currency.class)) {
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
    RateViewDto get(@PathVariable("id") int rateId) {
        try {
            return rateService.getById(rateId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + Rate.class)) {
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
    RateViewDto update(@Valid @RequestBody RateCreateDto rateCreateDto, BindingResult result
            , @PathVariable("id") int rateId) {
        controllerUtils.checkValidationViolations(result);
        try {
            return rateService.update(rateCreateDto, rateId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_RATE"))
                    || message.equals(properties.getProperty("EMPTY_RESULTSET") + Currency.class)) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties
                    .getProperty("FAILED_UPDATE_RATE_NONEXISTENT"))) {
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
    public void delete(@PathVariable("id") int rateId) {
        try {
            rateService.delete(rateId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_RATE"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties.getProperty("FAILED_UPDATE_RATE_NONEXISTENT"))) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }

    @GetMapping(value = ACTUAL)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<RateViewDto> getActual() {
        try {
            List<RateViewDto> list = rateService.getActual();
            if (list.size() == 0) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND,
                        properties.getProperty("EMPTY_RESULTSET") + Rate.class);
            }
            return list;
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = ACTUAL + ID)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    RateViewDto getActualByCurrencyId(@PathVariable("id") int currencyId) {
        try {
            return rateService.getActualByCurrencyId(currencyId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + Rate.class)) {
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
    List<RateViewDto> getAll() {
        try {
            List<RateViewDto> list = rateService.getAll();
            if (list.size() == 0) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND,
                        properties.getProperty("EMPTY_RESULTSET") + Rate.class);
            }
            return list;
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(value = ALL)
    @ResponseStatus(HttpStatus.CREATED)
    public void addAllForExistingCurrencies(@Valid @RequestBody List<RateCreateDto> rateCreateDtoList,
                                            BindingResult result) {
        controllerUtils.checkValidationViolations(result);
        try {
            rateService.addAllForExistingCurrencies(rateCreateDtoList);
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(SYNCHRONIZE)
    @ResponseStatus(HttpStatus.OK)
    public void synchronizeWithPrivatbankRates() {
        RateCreateDto[] rateCreateDtos = restTemplate.getForObject(URL, RateCreateDto[].class);
        Set<ConstraintViolation<RateCreateDto>> violations = new HashSet<>();
        Arrays.stream(rateCreateDtos).forEach((rate) -> violations
                .addAll(validator.validate(rate)));
        if (violations.size() > 0) {
            throw new RestException(HttpStatus.UNPROCESSABLE_ENTITY, gson.toJson(violations));
        }
        rateService.addAllForExistingCurrencies(Arrays.asList(rateCreateDtos));
    }
}
