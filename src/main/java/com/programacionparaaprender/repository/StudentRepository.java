package com.programacionparaaprender.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.programacionparaaprender.model.StudentOrm;

@Repository
public interface StudentRepository extends JpaRepository<StudentOrm, Long> {
    Optional<StudentOrm> findByFirstName(String firstName);
    Optional<StudentOrm> findById(Long id);
    Optional<StudentOrm> findByEmail(String email);
    boolean existsByFirstName(String firstName);
    boolean existsByEmail(String email);
}
