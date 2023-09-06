package com.programacionparaaprender.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
//import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentJson {
	private Long id;
	//@JsonProperty("first_name")
	private String firstName;
	private String lastName;
	private String email;
	@Override
	public String toString() {
		return "StudentJson [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ "]";
	}
	
	
}
