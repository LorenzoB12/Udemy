package com.mballem.curso.security.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mballem.curso.security.domain.Especialidade;

public interface EspecialidadeRepository extends JpaRepository<Especialidade, Long>{

	@Query("select e from Especialidade e where e.titulo like :titulo%")
	Page<?> findAllByTitulo(String titulo, Pageable pageable);

	@Query("select e.titulo from Especialidade e where e.titulo like :termo%") 
	List<String> findEspecialidadesByTermo(String termo);

	@Query("select e from Especialidade e where e.titulo IN :titulos")
	Set<Especialidade> findEspecialidadesByTitulos(String[] titulos);

	@Query("select e from Especialidade e "
			+ "join e.medicos m "
			+ "where m.id = :id")
	Page<Especialidade> findAllByMedicoId(Long id, Pageable pageable);

}
