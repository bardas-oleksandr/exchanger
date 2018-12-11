package ua.pb.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
        rootContext.register(ApplicationConfig.class);
        rootContext.register(SecurityConfig.class);

        servletContext.addListener(new ContextLoaderListener(rootContext));

        AnnotationConfigWebApplicationContext dispatcherServletContext = new AnnotationConfigWebApplicationContext();
        dispatcherServletContext.register(WebMvcConfig.class);

        ServletRegistration.Dynamic servletRegistration = servletContext.addServlet("dispatcher"
                , new DispatcherServlet(dispatcherServletContext));
        servletRegistration.addMapping("/", "/index");
        servletRegistration.setLoadOnStartup(1);

        FilterRegistration.Dynamic securityFilterRegistration = servletContext
                .addFilter("springSecurityFilterChain", DelegatingFilterProxy.class);
        securityFilterRegistration.addMappingForUrlPatterns(null
                , false, "/*");

        FilterRegistration.Dynamic encodingFilterRegistration = servletContext
                .addFilter("encodingFilter", CharacterEncodingFilter.class);
        encodingFilterRegistration.setInitParameter("encoding","UTF-8");
        encodingFilterRegistration.setInitParameter("forceEncoding","true");
        encodingFilterRegistration.addMappingForUrlPatterns(null
                , false, "/*");
    }
}
