package com.programacionparaaprender.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentJson;
import com.programacionparaaprender.model.StudentResponse;

@Component
public class FirstItemWriterResponse implements ItemWriter<StudentResponse>{

	@Override
	public void write(List<? extends StudentResponse> items) throws Exception {
		System.out.println("Inside Item Writer");
		items.stream().forEach(System.out::println);
	}

}

