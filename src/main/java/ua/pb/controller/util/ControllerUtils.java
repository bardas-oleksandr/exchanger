package ua.pb.controller.util;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import ua.pb.exception.RestException;
import ua.pb.exception.ValidationException;
import ua.pb.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class ControllerUtils {

    private static final String SPRING_SECURITY_CONTEXT = "SPRING_SECURITY_CONTEXT";
    private static final String MESSAGE_CODES_ATTRIBUTE = "messageCodes";
    private static final String VALIDATION_ERROR_PAGE = "validationerror";

    @Autowired
    private Gson gson;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    public int extractUserId(HttpServletRequest request) {
        try {
            SecurityContextImpl securityContext = (SecurityContextImpl) request
                    .getSession(true).getAttribute(SPRING_SECURITY_CONTEXT);
            User user = (User) securityContext.getAuthentication().getPrincipal();
            String username = user.getUsername();
            return userService.getUserByUsername(username).getId();
        } catch (Exception e) {
            throw new RestException(HttpStatus.FORBIDDEN, properties.getProperty("ACCESS_DENIED"));
        }
    }

    public String redirectValidationError(BindingResult result, ModelMap modelMap) {
        List<String> messageCodes = new ArrayList<>();
        result.getAllErrors().stream().forEach((errorMessage) -> messageCodes
                .add(errorMessage.getDefaultMessage()));
        modelMap.addAttribute(MESSAGE_CODES_ATTRIBUTE, messageCodes);
        return VALIDATION_ERROR_PAGE;
    }

    public String resolveMessageForException(Exception e) {
        String message = e.getMessage();
        if (message.equals(properties.getProperty("NOT_UNIQUE_USER"))) {
            return "not_unique_user";
        } else if (message.equals(properties.getProperty("NOT_UNIQUE_CURRENCY"))) {
            return "not_unique_currency";
        }
        return "you_should_watch_logs";
    }

    public List<String> getValidationExceptionMessageCodes(BindingResult result) {
        List<String> messageCodes = new ArrayList<>();
        result.getAllErrors().stream().forEach((errorMessage) -> messageCodes
                .add(errorMessage.getDefaultMessage()));
        return messageCodes;
    }

    public void checkValidationViolations(BindingResult result) {
        if (result.hasErrors()) {
            List<String> errorCodes = getValidationExceptionMessageCodes(result);
            String json = gson.toJson(errorCodes);
            throw new ValidationException(json);
        }
    }
}
