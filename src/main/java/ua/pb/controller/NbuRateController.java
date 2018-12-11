package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.NbuRateCreateDto;
import ua.pb.exception.ApplicationException;
import ua.pb.service.NbuRateService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

@Controller
@RequestMapping(value = "/nburate")
public class NbuRateController {

    private static final String ROOT = "/";
    private static final String SYNCHRONIZE = "synchronize";
    private static final String ERROR = "error";
    private static final String REDIRECT_CATALOG = "redirect:/catalog";
    private static final String URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private NbuRateService nbuRateService;

    @Autowired
    private Validator validator;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @PostMapping(ROOT + SYNCHRONIZE)
    public String synchronizeRates() {
        NbuRateCreateDto[] nbuRateCreateDtos = restTemplate.getForObject(URL, NbuRateCreateDto[].class);
        Set<ConstraintViolation<NbuRateCreateDto>> violations = new HashSet<>();
        Arrays.stream(nbuRateCreateDtos).forEach((nbuRate)->violations
                .addAll(validator.validate(nbuRate)));
        if (violations.size() > 0) {
            throw new ApplicationException(properties.getProperty("UNPROCESSABLE_ENTITY"));
        }
        nbuRateService.addAllForExistingCurrencies(Arrays.asList(nbuRateCreateDtos));
        return REDIRECT_CATALOG;
    }

    @ExceptionHandler({Exception.class})
    public ModelAndView handleException(Exception e) {
        String messageCode = controllerUtils.resolveMessageForException(e);
        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(MESSAGE_CODE_ATTRIBUTE, messageCode);
        return modelAndView;
    }
}
