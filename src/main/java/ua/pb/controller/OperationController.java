package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.view.OperationViewDto;
import ua.pb.service.OperationService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/operation")
public class OperationController {

    private final static String ID = "/{id}";
    private static final String OPERATION = "operation";
    private static final String ERROR = "error";
    private static final String REDIRECT_OPERATION = "redirect:/operation";
    private static final String OPERATION_LIST_ATTRIBUTE = "operationList";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";

    @Autowired
    private OperationService operationService;

    @Autowired
    private ControllerUtils controllerUtils;

    @GetMapping
    public String operationPage(ModelMap modelMap, HttpServletRequest request) {
        List<OperationViewDto> operationViewDtos = operationService.getAll();
        modelMap.addAttribute(OPERATION_LIST_ATTRIBUTE, operationViewDtos);
        return OPERATION;
    }

    @PostMapping(ID)
    public String updateDeletedState(@PathVariable("id") int operationId, HttpServletRequest request) {
        String isDeleted = request.getParameter("isDeleted");
        if(isDeleted != null && isDeleted.equals("on")){
            operationService.updateDeletedState(operationId, true);
        }else{
            operationService.updateDeletedState(operationId, false);
        }
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
