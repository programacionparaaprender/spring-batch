package com.programacionparaaprender.controllers;

import java.util.List;

import org.springframework.batch.core.launch.JobExecutionNotRunningException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.programacionparaaprender.model.Mensaje;
import com.programacionparaaprender.model.StudentOrm;
import com.programacionparaaprender.request.JobParamsRequest;
import com.programacionparaaprender.service.JobService;
import com.programacionparaaprender.service.StudentService;


@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = { "http://localhost:4200" }, maxAge = 3600)
public class StudentController {
/*
	@Autowired
	StudentService studentService;
	
	@GetMapping("/detalle/{id}")
    public ResponseEntity<?> getId(@PathVariable("id") Long id){
        if(!studentService.existsById(id))
            return getMensaje("no existe", HttpStatus.NOT_FOUND);
        StudentOrm tio = studentService.findById(id).get();
        ResponseEntity<StudentOrm> resultado = ResponseEntity.ok(tio);
        return resultado;
    }
	
	public ResponseEntity<Mensaje> getMensaje(String mensaje, HttpStatus status){
        Mensaje msj = new Mensaje(mensaje);
        ResponseEntity<Mensaje> entity = new ResponseEntity<Mensaje>(msj, status);
        return entity;
    }
    */
}