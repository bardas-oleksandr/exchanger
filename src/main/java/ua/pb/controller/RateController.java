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
import ua.pb.dto.create.RateCreateDto;
import ua.pb.exception.ApplicationException;
import ua.pb.service.RateService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;

@Controller
@RequestMapping(value = "/rate")
public class RateController {

    private static final String SYNCHRONIZE = "/synchronize";
    private static final String ERROR = "error";
    private static final String REDIRECT_CATALOG = "redirect:/catalog";
    private static final String URL = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RateService rateService;

    @Autowired
    private Validator validator;

    @Autowired
    private ControllerUtils controllerUtils;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @PostMapping(SYNCHRONIZE)
    public String synchronizeRates() {
        RateCreateDto[] rateCreateDtos = restTemplate.getForObject(URL, RateCreateDto[].class);
        Set<ConstraintViolation<RateCreateDto>> violations = new HashSet<>();
        Arrays.stream(rateCreateDtos).forEach((rate)->violations
                .addAll(validator.validate(rate)));
        if (violations.size() > 0) {
            throw new ApplicationException(properties.getProperty("UNPROCESSABLE_ENTITY"));
        }
        rateService.addAllForExistingCurrencies(Arrays.asList(rateCreateDtos));
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
