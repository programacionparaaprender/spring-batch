package com.programacionparaaprender.model;


import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlRootElement(name="student")
public class StudentJdbc {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
}
