package com.programacionparaaprender.listener;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;

@Component
public class SkipListener {

	@OnSkipInRead
	public void skipInRead(Throwable th) {
		if(th instanceof FlatFileParseException) {
			String filePath 
				= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\reader\\SkipInRead.txt";
			createFile(filePath, ((FlatFileParseException) th).getInput());
		}
	}
	
	@OnSkipInProcess
	public void skipInProcess(StudentCsv studentCsv, Throwable th) {
		String filePath 
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\processor\\SkipInProcess.txt";
		createFile(filePath, studentCsv.toString());
	}
	
	public void createFile(String filePath, String data) {
		try(FileWriter fileWriter = new FileWriter(new File(filePath), true)) {
			fileWriter.write(data + "," + new Date() + "\n");
		}catch(Exception e) {
			
		}
	}
}