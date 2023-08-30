package com.programacionparaaprender.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@XmlRootElement(name = "student")
public class StudentXml {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
}
