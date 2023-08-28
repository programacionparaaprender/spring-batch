package com.programacionparaaprender.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;


@Component
public class FirstItemWriterCsv implements ItemWriter<StudentCsv>{

	@Override
	public void write(List<? extends StudentCsv> items) throws Exception {
		System.out.println("Inside Item Writer");
		items.stream().forEach(System.out::println);
	}

}
