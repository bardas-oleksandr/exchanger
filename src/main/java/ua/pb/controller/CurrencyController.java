package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.CurrencyCreateDto;
import ua.pb.service.CurrencyService;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/currency")
public class CurrencyController {

    private final static String ID = "/{id}";
    private static final String ERROR = "error";
    private static final String REDIRECT_CATALOG = "redirect:/catalog";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ControllerUtils controllerUtils;

    @PostMapping
    public String addCurrency(@Valid @ModelAttribute CurrencyCreateDto currencyCreateDto
            , BindingResult result, ModelMap modelMap) {
        if (result.hasErrors()) {
            return controllerUtils.redirectValidationError(result, modelMap);
        }
        currencyService.create(currencyCreateDto);
        return REDIRECT_CATALOG;
    }

    @PostMapping(ID)
    public String updateCurrencyAndRate(@Valid @ModelAttribute CurrencyCreateDto currencyCreateDto
            , BindingResult result, @PathVariable("id") int currencyId, ModelMap modelMap) {
        if (result.hasErrors()) {
            return controllerUtils.redirectValidationError(result, modelMap);
        }
        currencyService.update(currencyCreateDto, currencyId);
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
