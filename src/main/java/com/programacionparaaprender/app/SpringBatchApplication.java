package com.programacionparaaprender.app;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import lombok.extern.slf4j.Slf4j;


@SpringBootApplication
@EnableBatchProcessing
@ComponentScan({"com.programacionparaaprender.config"
	,"com.programacionparaaprender.repository"
	, "com.programacionparaaprender.service"
	 ,"com.programacionparaaprender.reader"
	 ,"com.programacionparaaprender.processor"
	 ,"com.programacionparaaprender.writer"
	 ,"com.programacionparaaprender.controllers"
	 ,"com.programacionparaaprender.model"
	 ,"com.programacionparaaprender.listener"
	 
	 })
@EnableAsync
@EnableScheduling
public class SpringBatchApplication {

	public static void main(String[] args) {
		System.out.println("Funciona log4j");
		SpringApplication.run(SpringBatchApplication.class, args);
	}

}