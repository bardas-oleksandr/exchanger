package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.service.CatalogService;

@Controller
public class MainController {

    private static final String ROOT = "/";
    private static final String LOGIN_FAILED = "loginfailed";
    private static final String INDEX = "index";
    private static final String ERROR = "error";
    private static final String LOGIN = "login";
    private static final String LOGIN_FAILED_ATTRIBUTE = "login_failed";

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(ROOT)
    public String indexPage(ModelMap modelMap){
        return INDEX;
    }

    @GetMapping(ROOT + ERROR)
    public String errorPage(){
        return ERROR;
    }

    @GetMapping(ROOT + LOGIN)
    public String loginPage(){
        return LOGIN;
    }

    @GetMapping(ROOT + LOGIN_FAILED)
    public ModelAndView loginFailed() {
        ModelAndView modelAndView = new ModelAndView(LOGIN);
        modelAndView.addObject(LOGIN_FAILED_ATTRIBUTE,true);
        return modelAndView;
    }
}
