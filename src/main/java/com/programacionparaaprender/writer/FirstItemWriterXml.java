package com.programacionparaaprender.writer;


import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJson;
import com.programacionparaaprender.model.StudentXml;


@Component
public class FirstItemWriterXml implements ItemWriter<StudentXml>{

	@Override
	public void write(List<? extends StudentXml> items) throws Exception {
		System.out.println("Inside Item Writer");
		items.stream().forEach(System.out::println);
	}

}
