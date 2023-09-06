package com.programacionparaaprender.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentJdbc;
import com.programacionparaaprender.model.StudentJson;

@Component
public class FirstItemProcessorJson implements ItemProcessor<StudentJdbc, StudentJson>{

	@Override
	public StudentJson process(StudentJdbc item) throws Exception {
		StudentJson studentJson = new StudentJson();
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLastName(item.getLastName());
		studentJson.setId(item.getId());
		studentJson.setEmail(item.getEmail());
		System.out.println("Inside Item Processor StudentJdbc");
		return studentJson;
	}

}