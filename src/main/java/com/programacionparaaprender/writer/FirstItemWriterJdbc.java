package com.programacionparaaprender.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import com.programacionparaaprender.model.StudentJdbc;


@Component
public class FirstItemWriterJdbc implements ItemWriter<StudentJdbc>{

	@Override
	public void write(List<? extends StudentJdbc> items) throws Exception {
		System.out.println("Inside Item Writer");
		items.stream().forEach(System.out::println);
	}

}
