package com.programacionparaaprender.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.programacionparaaprender.model.StudentCsv;
import com.programacionparaaprender.model.StudentOrm;
import com.programacionparaaprender.model.StudentResponse;
import com.programacionparaaprender.repository.StudentRepository;

@Service
@Transactional
public class StudentService {
	//@Autowired(required = false) 
	//private StudentRepository studentRepository;

	List<StudentResponse> list;
    
	/*
    @Transactional(readOnly = true)
    public Optional<StudentOrm> findById(Long id){
    	return studentRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<StudentOrm> findByFirstName(String firstName){
        return studentRepository.findByFirstName(firstName);
    }

    @Transactional(readOnly = true)
    public Optional<StudentOrm> findByEmail(String email){
        return studentRepository.findByEmail(email);
    }


    public boolean save(StudentOrm studentOrm){
        if(studentRepository.existsByFirstName(studentOrm.getFirstName()) || studentRepository.existsByEmail(studentOrm.getEmail())) {
            return false;
        }
        studentRepository.save(studentOrm);
        return true;
    }


    public void delete(Long id){
    	studentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id){
        return studentRepository.existsById(id);
    }

    @Transactional(readOnly = true)
    public boolean existsByFirstName(String firstName){
        return studentRepository.existsByFirstName(firstName);
    }

    @Transactional(readOnly = true)
    public boolean exixtsByEmail(String email){
        return studentRepository.existsByEmail(email);
    }
    
    */
    
    public StudentResponse getStudent(long id, String name){
		System.out.println("id = " + id + " and name = " + name);
    	if(list == null) {
    		list = new LinkedList<StudentResponse>();
    		restCallToStudents();
    	}
    	if(!list.isEmpty()) {
    		return list.remove(0);
    	}
		return null;
    }
    
	public void restCallToStudents(){
		RestTemplate restTemplate = new RestTemplate();
		StudentResponse[] studentResponseArray;
		studentResponseArray = restTemplate.getForObject("http://localhost:8081/api/v1/students"
				, StudentResponse[].class);
		for(StudentResponse sr: studentResponseArray) {
			list.add(sr);
		}
	}
	public StudentResponse restCallToCreateStudent(StudentCsv studentCsv) {
		RestTemplate restTemplate = new RestTemplate();
		String url = "http://localhost:8081/api/v1/createStudent";
		StudentResponse response;
		response = restTemplate.postForObject(url, studentCsv, StudentResponse.class);
		return response;
	}
}
