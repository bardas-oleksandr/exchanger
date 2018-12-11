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
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.dto.view.NbuRateViewDto;
import ua.pb.exception.ApplicationException;
import ua.pb.exception.RestException;
import ua.pb.model.Currency;
import ua.pb.model.NbuRate;
import ua.pb.service.NbuRateService;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import java.util.*;

@RestController
@RequestMapping(value = "/rest/nburate",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class NbuRateRestController {

    private static final String ID = "/{id}";
    private static final String ACTUAL = "/actual";
    private static final String SYNCHRONIZE = "/synchronize";
    private static final String ALL = "/all";
    private static final String URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";

    @Autowired
    private NbuRateService nbuRateService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Validator validator;

    @Autowired
    private Gson gson;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    NbuRateViewDto create(@Valid @RequestBody NbuRateCreateDto nbuRateCreateDto,
                          BindingResult result) {
        controllerUtils.checkValidationViolations(result);
        try {
            return nbuRateService.create(nbuRateCreateDto);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"))
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
    NbuRateViewDto get(@PathVariable("id") int nbuRateId) {
        try {
            return nbuRateService.getById(nbuRateId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + NbuRate.class)) {
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
    NbuRateViewDto update(@Valid @RequestBody NbuRateCreateDto nbuRateCreateDto,
                          BindingResult result, @PathVariable("id") int nbuRateId) {
        controllerUtils.checkValidationViolations(result);
        try {
            return nbuRateService.update(nbuRateCreateDto, nbuRateId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("DATA_INTEGRITY_VIOLATION_FOR_NBU_RATE"))
                    || message.equals(properties.getProperty("EMPTY_RESULTSET") + Currency.class)) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties
                    .getProperty("FAILED_UPDATE_NBU_RATE_NONEXISTENT"))) {
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
    public void delete(@PathVariable("id") int nbuRateId) {
        try {
            nbuRateService.delete(nbuRateId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("INTEGRITY_VIOLATION_WHILE_DELETE_NBU_RATE"))) {
                //http status 409
                throw new RestException(HttpStatus.CONFLICT, message);
            } else if (message.equals(properties.getProperty("FAILED_UPDATE_NBU_RATE_NONEXISTENT"))) {
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
    List<NbuRateViewDto> getActual() {
        try {
            List<NbuRateViewDto> list = nbuRateService.getActual();
            if (list.size() == 0) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND,
                        properties.getProperty("EMPTY_RESULTSET") + NbuRate.class);
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
    NbuRateViewDto getActualByCurrencyId(@PathVariable("id") int currencyId) {
        try {
            return nbuRateService.getActualByCurrencyId(currencyId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.equals(properties.getProperty("EMPTY_RESULTSET") + NbuRate.class)) {
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
    List<NbuRateViewDto> getAll() {
        try {
            List<NbuRateViewDto> list = nbuRateService.getAll();
            if (list.size() == 0) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND,
                        properties.getProperty("EMPTY_RESULTSET") + NbuRate.class);
            }
            return list;
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(value = ALL)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addAllForExistingCurrencies(@Valid @RequestBody List<NbuRateCreateDto> nbuRateCreateDtoList,
                                            BindingResult result) {
        controllerUtils.checkValidationViolations(result);
        try {
            nbuRateService.addAllForExistingCurrencies(nbuRateCreateDtoList);
        } catch (ApplicationException e) {
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @PostMapping(SYNCHRONIZE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void synchronizeWithPrivatbankRates() {
        NbuRateCreateDto[] nbuRateCreateDtos = restTemplate.getForObject(URL, NbuRateCreateDto[].class);
        Set<ConstraintViolation<NbuRateCreateDto>> violations = new HashSet<>();
        Arrays.stream(nbuRateCreateDtos).forEach((rate) -> violations
                .addAll(validator.validate(rate)));
        if (violations.size() > 0) {
            throw new RestException(HttpStatus.UNPROCESSABLE_ENTITY, gson.toJson(violations));
        }
        nbuRateService.addAllForExistingCurrencies(Arrays.asList(nbuRateCreateDtos));
    }
}
