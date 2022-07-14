package com.kalsym.locationservice;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.HttpMessageConverter;
import java.awt.image.BufferedImage;

import com.kalsym.locationservice.repository.CustomRepositoryImpl;

import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.CommandLineRunner;


@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomRepositoryImpl.class)

public class LocationServiceApplication {

	public static String VERSION;
    public static String ASSETURL ;
    public static String MARKETPLACEURL ;


    static {
        System.setProperty("spring.jpa.hibernate.naming.physical-strategy", "org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl");
    }

	public static void main(String[] args) {
		SpringApplication.run(LocationServiceApplication.class, args);
		System.out.println("LOCATION SERVICE IS RUNNING :::::");
	}

    @Value("${asset.service.url}")
    private String assetServiceUrl;

    @Value("${marketplace.url}")
    private String marketPlaceUrl;

    @Bean
    CommandLineRunner lookup(ApplicationContext context) {
        return args -> {
            ASSETURL = assetServiceUrl;
            MARKETPLACEURL = marketPlaceUrl;
        };
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
