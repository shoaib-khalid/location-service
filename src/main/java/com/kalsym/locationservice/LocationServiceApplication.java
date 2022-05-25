package com.kalsym.locationservice;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import java.awt.image.BufferedImage;

import com.kalsym.locationservice.repository.CustomRepositoryImpl;

import org.springframework.http.converter.BufferedImageHttpMessageConverter;


@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)

public class LocationServiceApplication {

	public static String VERSION;

    static {
        System.setProperty("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
    }

	public static void main(String[] args) {
		SpringApplication.run(LocationServiceApplication.class, args);
		System.out.println("LOCATION SERVICE IS RUNNING :::::");
	}

	@Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

}
