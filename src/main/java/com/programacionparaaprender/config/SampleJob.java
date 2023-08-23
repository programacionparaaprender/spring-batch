package com.programacionparaaprender.config;

import org.springframework.context.annotation.Configuration;

import com.programacionparaaprender.service.SecondTasklet;

import org.springframework.context.annotation.Bean;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SampleJob {

	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private SecondTasklet secondTasklet;
	
	@Bean
	public Job firstJob() {
		return jobBuilderFactory.get("First Job")
		.start(firstStep())
		.next(secondStep())
		.build();
	}
	
	private Step firstStep() {
		return stepBuilderFactory.get("First step")
		.tasklet(firstTasklet())
		.build();
	}
	
	private Step secondStep() {
		return stepBuilderFactory.get("Second step")
		.tasklet(secondTaskletMetodo(1))
		.tasklet(secondTasklet)
		.build();
	}
	
	private Tasklet firstTasklet() {
		return new Tasklet() {
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				System.out.println("This is first tasklet");
				return RepeatStatus.FINISHED;
			}
		};
	}
	
	private Tasklet secondTaskletMetodo(int id) {
		return new SecondTasklet(id);
	}

}
