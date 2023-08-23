package com.programacionparaaprender.service;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Service;

@Service
public class SecondTasklet implements Tasklet{

	private int id;
	
	public SecondTasklet(int id) {
		this.id = id;
	}
	
	public SecondTasklet() {
		id = 0;
	}
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		System.out.printf("This is second tasklet %d\n", id);
		return RepeatStatus.FINISHED;
	}

}
