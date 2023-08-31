package com.programacionparaaprender.model;


import java.io.Serializable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name="student")
public class StudentOrm  implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
    @Column(name="first_name")
	private String firstName;
	
	@NotNull
    @Column(name="last_name")
	private String lastName;
	
	@NotNull
    @Column(name="email")
	private String email;
}





