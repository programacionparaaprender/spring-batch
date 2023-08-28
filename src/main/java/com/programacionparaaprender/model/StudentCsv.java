package com.programacionparaaprender.model;

import lombok.Data;

@Data
public class StudentCsv {
	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	@Override
	public String toString() {
		return "StudentCsv [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName +", email=" + email + "]";
	}
}
