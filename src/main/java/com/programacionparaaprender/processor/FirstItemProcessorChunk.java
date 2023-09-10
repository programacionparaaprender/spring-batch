package com.programacionparaaprender.processor;



import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJson;

@Component
public class FirstItemProcessorChunk implements ItemProcessor<StudentCsv, StudentJson>{

	@Override
	public StudentJson process(StudentCsv item) throws Exception {
		StudentJson studentJson = new StudentJson();
		studentJson.setFirstName(item.getFirstName());
		studentJson.setLastName(item.getLastName());
		studentJson.setId(item.getId());
		studentJson.setEmail(item.getEmail());
		System.out.println("Inside Item Processor StudentJson");
		return studentJson;
	}

}
