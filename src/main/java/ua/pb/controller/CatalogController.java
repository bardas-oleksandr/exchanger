package ua.pb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.view.CatalogItemViewDto;
import ua.pb.service.CatalogService;

import java.util.List;

@Controller
@RequestMapping(value = "/catalog")
public class CatalogController {
    
    private static final String CATALOG = "catalog";
    private static final String ERROR = "error";
    private static final String CATALOG_ATTRIBUTE = "catalogItems";
    private static final String MESSAGE_CODE_ATTRIBUTE = "message_code";

    @Autowired
    private CatalogService catalogService;

    @Autowired
    private ControllerUtils controllerUtils;
    
    @GetMapping
    public String catalogPage(ModelMap modelMap){
        List<CatalogItemViewDto> catalogItemViewDtos = catalogService.getCatalog();
        modelMap.addAttribute(CATALOG_ATTRIBUTE, catalogItemViewDtos);
        return CATALOG;
    }

    @ExceptionHandler({Exception.class})
    public ModelAndView handleException(Exception e) {
        String messageCode = controllerUtils.resolveMessageForException(e);
        ModelAndView modelAndView = new ModelAndView(ERROR);
        modelAndView.addObject(MESSAGE_CODE_ATTRIBUTE, messageCode);
        return modelAndView;
    }
}
