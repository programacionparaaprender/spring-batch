package com.programacionparaaprender.service;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class FirstStepListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		System.out.println("before step " + stepExecution.getStepName());
		System.out.println("before exec cont " + stepExecution.getJobExecution().getExecutionContext());
		System.out.println("before step exec cont " + stepExecution.getExecutionContext());
		stepExecution.getExecutionContext().put("sec", "sec value");
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		System.out.println("after step " + stepExecution.getStepName());
		System.out.println("after exec cont " + stepExecution.getJobExecution().getExecutionContext());
		System.out.println("after step exec cont " + stepExecution.getExecutionContext());
		return null;
	}

}
