package com.programacionparaaprender.listener;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.springframework.batch.core.SkipListener;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.stereotype.Component;

import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentJson;

@Component
public class SkipListenerImpl implements SkipListener<StudentCsv, StudentJson> {

	@Override
	public void onSkipInRead(Throwable th) {
		String filePath 
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\reader\\SkipInRead.txt";
		if(th instanceof FlatFileParseException) {
			createFile(filePath, ((FlatFileParseException) th).getInput());
		}
	}

	@Override
	public void onSkipInWrite(StudentJson item, Throwable t) {
		String filePath 
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\writer\\SkipInWriter.txt";
		createFile(filePath, item.toString());
	}

	@Override
	public void onSkipInProcess(StudentCsv item, Throwable t) {
		String filePath 
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\processor\\SkipInProcess.txt";
		createFile(filePath, item.toString());
	}
	
	public void createFile(String filePath, String data) {
		try(FileWriter fileWriter = new FileWriter(new File(filePath), true)) {
			fileWriter.write(data + "," + new Date() + "\n");
		}catch(Exception e) {
			
		}
	}

}
