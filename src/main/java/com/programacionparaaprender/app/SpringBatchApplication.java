package com.programacionparaaprender.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableBatchProcessing
@Slf4j
@ComponentScan({"com.programacionparaaprender.config", "com.programacionparaaprender.service"})
public class SpringBatchApplication {

	public static void main(String[] args) {
		log.info("Funciona log4j");
		System.out.println("Funciona log4j");
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}