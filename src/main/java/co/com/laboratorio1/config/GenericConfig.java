package co.com.laboratorio1.config;

import javax.servlet.Filter;
import javax.servlet.http.HttpSessionListener;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import net.bull.javamelody.MonitoredWithSpring;

@Configuration
@MonitoredWithSpring
public class GenericConfig {
	
	@Bean
	public HttpSessionListener javaMelodyListener(){
	    return new net.bull.javamelody.SessionListener();
	}

	@Bean
	public Filter javaMelodyFilter(){
	    return new net.bull.javamelody.MonitoringFilter();
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
}
