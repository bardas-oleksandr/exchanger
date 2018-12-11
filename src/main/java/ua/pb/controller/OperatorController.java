package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.OperationCreateDto;
import ua.pb.dto.view.CurrencyViewDto;
import ua.pb.dto.view.OperationViewDto;
import ua.pb.service.CurrencyService;
import ua.pb.service.OperationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Properties;

@Controller
@RequestMapping(value = "/operator")
public class OperatorController {

    private static final String ROOT = "/";
    private static final String REDIRECT_OPERATION = "redirect:/operator/operation";
    private static final String OPERATION = "operation";
    private static final String ERROR = "error";
    private static final String OPERATION_LIST_ATTRIBUTE = "operationList";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";
    private static final String CURRENCY_LIST_ATTRIBUTE = "currencyList";

    @Autowired
    private OperationService operationService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private ControllerUtils controllerUtils;

    @GetMapping(ROOT + OPERATION)
    public String operationPage(ModelMap modelMap, HttpServletRequest request){
        int userId = controllerUtils.extractUserId(request);

        List<OperationViewDto> operationViewDtos = operationService.getAllByUserId(userId);
        modelMap.addAttribute(OPERATION_LIST_ATTRIBUTE, operationViewDtos);

        List<CurrencyViewDto> currencyViewDtoList = currencyService.getAll();
        modelMap.addAttribute(CURRENCY_LIST_ATTRIBUTE, currencyViewDtoList);

        return OPERATION;
    }

    @PostMapping(ROOT + OPERATION)
    public String createOperation(@Valid @ModelAttribute OperationCreateDto operationCreateDto
            , BindingResult result, HttpServletRequest request, ModelMap modelMap){
        if (result.hasErrors()) {
            return controllerUtils.redirectValidationError(result, modelMap);
        }
        int userId = controllerUtils.extractUserId(request);
        operationService.create(operationCreateDto, userId);
        return REDIRECT_OPERATION;
    }

    @ExceptionHandler({Exception.class})
    public ModelAndView handleException(Exception e) {
        String messageCode = controllerUtils.resolveMessageForException(e);
        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(MESSAGE_CODE_ATTRIBUTE, messageCode);
        return modelAndView;
    }
}
