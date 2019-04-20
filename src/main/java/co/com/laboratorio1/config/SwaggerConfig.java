package co.com.laboratorio1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import springfox.documentation.service.Contact;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import co.com.laboratorio1.utils.Constantes;

@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {
		
    @Bean
    public Docket api() { 
        return new Docket(DocumentationType.SWAGGER_2)  
          .select()                                  
          .apis(RequestHandlerSelectors.basePackage(Constantes.BASE_PACKAGE_CONTROLLER))              
          .paths(PathSelectors.any())                          
          .build()
          .apiInfo(metaData());                                           
    }
    
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title(Constantes.API_TITLE)
                .description(Constantes.API_SUB_TITLE)
                .version(Constantes.API_VERSION)
                .license(Constantes.API_LICENCE)
                .licenseUrl(Constantes.API_LICENCE_URL)
                .contact(new Contact(Constantes.CONTACT_NAME, 
			                		 Constantes.CONTACT_URL, 
			                		 Constantes.CONTACT_MAIL))
                .build();
    }
    
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(Constantes.RESOURCE_HANDLER_1)
                .addResourceLocations(Constantes.RESOURCE_LOCATION_1);
 
        registry.addResourceHandler(Constantes.RESOURCE_HANDLER_2)
                .addResourceLocations(Constantes.RESOURCE_LOCATION_2);
    }
    
}