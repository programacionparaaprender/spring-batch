package com.programacionparaaprender.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.programacionparaaprender.request.JobParamsRequest;

@Service
public class JobService {
	@Autowired
	JobLauncher jobLauncher;
	
	@Qualifier("firstJob")
	@Autowired
	Job firstJob;
	
	@Qualifier("secondJob")
	@Autowired
	Job secondJob;
	
	@Async
	public void startJob(String jobName
			, List<JobParamsRequest> jobParamsRequestList) {
		try {
			
			JobExecution jobExecution = null;
			Map<String, JobParameter> params = new HashMap<>();
			params.put("currentTime", new JobParameter(System.currentTimeMillis()));
			
			jobParamsRequestList.stream().forEach(jobParamReq -> {
				params.put(jobParamReq.getParamKey(), 
						new JobParameter(jobParamReq.getParamValue()));
			});
			
			JobParameters jobParameters = new JobParameters(params);
			if(jobName.equals("First Job")) {
				jobExecution = jobLauncher.run(firstJob, jobParameters);
			}else if(jobName.equals("Second Job")) {
				jobExecution = jobLauncher.run(secondJob, jobParameters);
			}
			System.out.println("Job Execution ID = " + jobExecution.getId());
		}catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
}
