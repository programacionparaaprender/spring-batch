package com.programacionparaaprender.listener;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.springframework.batch.core.annotation.OnSkipInProcess;
import org.springframework.batch.core.annotation.OnSkipInRead;
import org.springframework.batch.core.annotation.OnSkipInWrite;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJson;

@Component
public class SkipListener {
	
<<<<<<< HEAD
=======
	private String base = "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch-1\\Chunk Job1\\First Chunk Step1\\";
	
>>>>>>> 24f394d014ec9ee5437dd9cbeb615bd2821d6524
	@OnSkipInRead
	public void skipInRead(Throwable th) {
		if(th instanceof FlatFileParseException) {
			String filePath 
				= base + "reader\\SkipInRead.txt";
			createFile(filePath, ((FlatFileParseException) th).getInput());
		}
	}
	
	@OnSkipInProcess
	public void skipInProcess(StudentCsv studentCsv, Throwable th) {
		String filePath 
		= base + "processor\\SkipInProcess.txt";
		createFile(filePath, studentCsv.toString());
	}
	
	@OnSkipInWrite
	public void skipInWriter(StudentJson studentJson, Throwable th) {
		String filePath 
<<<<<<< HEAD
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\writer\\SkipInWriter.txt";
=======
		= base + "writer\\SkipInWriter.txt";
>>>>>>> 24f394d014ec9ee5437dd9cbeb615bd2821d6524
		createFile(filePath, studentJson.toString());
	}
	
	
	public void createFile(String filePath, String data) {
		try(FileWriter fileWriter = new FileWriter(new File(filePath), true)) {
			fileWriter.write(data + "," + new Date() + "\n");
		}catch(Exception e) {
			
		}
	}
}