package com.mballem.curso.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mballem.curso.security.domain.Paciente;
import com.mballem.curso.security.repository.PacienteRepository;

@Service
public class PacienteService {

	@Autowired
	private PacienteRepository repo;
	
	@Transactional(readOnly = true)
	public Paciente buscarPorPacienteEmail(String email) {
		return repo.findByPacienteEmail(email).orElse(new Paciente());
	}

	@Transactional(readOnly = false)
	public void salvar(Paciente paciente) {
		repo.save(paciente);
	}

	@Transactional(readOnly = false)
	public void editar(Paciente paciente) {
		Paciente p2 = repo.findById(paciente.getId()).get();
		p2.setDtNascimento(paciente.getDtNascimento());
		p2.setNome(paciente.getNome());
	}
}
