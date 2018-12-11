package ua.pb.controller.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ua.pb.controller.util.ControllerUtils;
import ua.pb.dto.view.CatalogItemViewDto;
import ua.pb.exception.ApplicationException;
import ua.pb.exception.RestException;
import ua.pb.service.CatalogService;

import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping(value = "/rest/catalog",
        consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE},
        produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CatalogRestController {

    private final static String ID = "/{id}";

    @Autowired
    private CatalogService catalogService;

    @Autowired
    @Qualifier("messageProperties")
    private Properties properties;

    @Autowired
    private ControllerUtils controllerUtils;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<CatalogItemViewDto> getCatalog() {
        try {
            return catalogService.getCatalog();
        } catch (ApplicationException e) {
            System.out.println();
            //http status 500
            throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping(value = ID)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    CatalogItemViewDto getCatalogItem(@PathVariable("id") int currencyId) {
        try {
            return catalogService.getCatalogItem(currencyId);
        } catch (ApplicationException e) {
            String message = e.getMessage();
            if (message.startsWith(properties.getProperty("EMPTY_RESULTSET"))) {
                //http status 404
                throw new RestException(HttpStatus.NOT_FOUND, message);
            } else {
                //http status 500
                throw new RestException(HttpStatus.INTERNAL_SERVER_ERROR, message);
            }
        }
    }
}
