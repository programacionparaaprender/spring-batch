package com.programacionparaaprender.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentJdbc;
import com.programacionparaaprender.model.StudentJson;
import com.programacionparaaprender.model.StudentXml;

@Component
public class FirstItemProcessorXml implements ItemProcessor<StudentJdbc, StudentXml>{

	@Override
	public StudentXml process(StudentJdbc item) throws Exception {
		StudentXml studentXml = new StudentXml();
		studentXml.setFirstName(item.getFirstName());
		studentXml.setLastName(item.getLastName());
		studentXml.setId(item.getId());
		studentXml.setEmail(item.getEmail());
		System.out.println("Inside Item Processor StudentXml");
		return studentXml;
	}

}
