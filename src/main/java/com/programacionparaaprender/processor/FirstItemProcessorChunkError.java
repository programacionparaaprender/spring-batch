package com.programacionparaaprender.processor;


import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJson;

@Component
public class FirstItemProcessorChunkError implements ItemProcessor<StudentCsv, StudentJson>{

	@Override
	public StudentJson process(StudentCsv item) throws Exception {
		System.out.println("Inside Item Processor StudentJson");
		if(item.getId() == 1) {
			throw new NullPointerException();
		}
		StudentJson studentJson = new StudentJson();
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLastName(item.getLastName());
		studentJson.setId(item.getId());
		studentJson.setEmail(item.getEmail());
		return studentJson;
	}

}