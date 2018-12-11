package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.create.UserCreateDto;
import ua.pb.dto.create.UserUpdateDto;
import ua.pb.dto.view.UserViewDto;
import ua.pb.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    private final static String ID = "/{id}";
    private static final String USER_LIST_ATTRIBUTE = "userList";
    private static final String USERS = "users";
    private static final String ERROR = "error";
    private static final String REDIRECT_USERS = "redirect:/user";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";

    @Autowired
    private UserService userService;

    @Autowired
    private ControllerUtils controllerUtils;

    @GetMapping
    public String usersPage(ModelMap modelMap) {
        List<UserViewDto> userList = userService.getAllUsers();
        modelMap.addAttribute(USER_LIST_ATTRIBUTE, userList);
        return USERS;
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute UserCreateDto userCreateDto, BindingResult result
            , ModelMap modelMap) {
        if (result.hasErrors()) {
            return controllerUtils.redirectValidationError(result, modelMap);
        }
        userService.create(userCreateDto);
        return REDIRECT_USERS;
    }

    @PostMapping(ID)
    public String updateUser(@Valid @ModelAttribute UserUpdateDto userUpdateDto, BindingResult result
            , @PathVariable("id") int userId, ModelMap modelMap) {
        if (result.hasErrors()) {
            return controllerUtils.redirectValidationError(result, modelMap);
        }
        userService.update(userUpdateDto, userId);
        return REDIRECT_USERS;
    }

    @ExceptionHandler({Exception.class})
    public ModelAndView handleException(Exception e) {
        String messageCode = controllerUtils.resolveMessageForException(e);
        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(MESSAGE_CODE_ATTRIBUTE, messageCode);
        return modelAndView;
    }
}
