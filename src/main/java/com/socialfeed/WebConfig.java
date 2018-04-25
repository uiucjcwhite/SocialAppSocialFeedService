/**
 * 
 */
package com.socialfeed;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author Cameron
 * This class allows Cross origins for the whole application.
 * This let's localhost front-end back-end testing happen on
 * the same host.
 */
@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurationSupport  {
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedMethods("*")
                .allowedOrigins("*")
                .allowedHeaders("*")
				.allowCredentials(true);
	}
}
