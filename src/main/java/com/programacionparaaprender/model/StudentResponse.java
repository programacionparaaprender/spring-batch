package com.programacionparaaprender.model;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class StudentResponse {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
}
