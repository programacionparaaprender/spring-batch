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

<<<<<<< HEAD
	@Override
	public void onSkipInRead(Throwable th) {
		String filePath 
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\reader\\SkipInRead.txt";
=======
	private String base = "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch-1\\Chunk Job1\\First Chunk Step1\\";
	
	@Override
	public void onSkipInRead(Throwable th) {
		String filePath 
		= base + "reader\\SkipInRead.txt";
>>>>>>> 24f394d014ec9ee5437dd9cbeb615bd2821d6524
		if(th instanceof FlatFileParseException) {
			createFile(filePath, ((FlatFileParseException) th).getInput());
		}
	}

	@Override
	public void onSkipInWrite(StudentJson item, Throwable t) {
		String filePath 
<<<<<<< HEAD
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\writer\\SkipInWriter.txt";
=======
		= base + "writer\\SkipInWriter.txt";
>>>>>>> 24f394d014ec9ee5437dd9cbeb615bd2821d6524
		createFile(filePath, item.toString());
	}

	@Override
	public void onSkipInProcess(StudentCsv item, Throwable t) {
		String filePath 
<<<<<<< HEAD
		= "C:\\Users\\luis1\\Documents\\htdocs\\telefonica\\spring-batch\\Chunk Job1\\First Chunk Step1\\processor\\SkipInProcess.txt";
=======
		= base + "processor\\SkipInProcess.txt";
>>>>>>> 24f394d014ec9ee5437dd9cbeb615bd2821d6524
		createFile(filePath, item.toString());
	}
	
	public void createFile(String filePath, String data) {
		try(FileWriter fileWriter = new FileWriter(new File(filePath), true)) {
			fileWriter.write(data + "," + new Date() + "\n");
		}catch(Exception e) {
			
		}
	}

}
