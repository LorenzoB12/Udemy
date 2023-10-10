package com.mballem.curso.security.web.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mballem.curso.security.domain.Medico;
import com.mballem.curso.security.domain.Perfil;
import com.mballem.curso.security.domain.PerfilTipo;
import com.mballem.curso.security.domain.Usuario;
import com.mballem.curso.security.service.MedicoService;
import com.mballem.curso.security.service.UsuarioService;

@Controller
@RequestMapping("u")
public class UsuarioController {
	
	@Autowired
	private UsuarioService service;
	@Autowired
	private MedicoService medicoService;
	
	@GetMapping("/novo/cadastro/usuario")
	public String cadastroPorAdminParaAdminMedicoPaciente(Usuario usuario) {
		return "usuario/cadastro";
	}
	
	@GetMapping("/lista")
	public String listarUsuarios() {
		return "usuario/lista";
	}
	
	@GetMapping("/datatables/server/usuarios")
	public ResponseEntity<?> listarUsuariosDatatables(HttpServletRequest request){
		return ResponseEntity.ok(service.buscarTodos(request));
	}
	@PostMapping("/cadastro/salvar")
	public String salvarUsuarios(Usuario usuario, RedirectAttributes attr) {
		List<Perfil> perfis = usuario.getPerfis();
		if(perfis.size() > 2 ||
				perfis.containsAll(Arrays.asList(new Perfil(1L), new Perfil(3L))) ||
				perfis.containsAll(Arrays.asList(new Perfil(2L), new Perfil(3L)))) {
			attr.addFlashAttribute("falha", "Paciente não pode ser Admin e/ou Médico.");
			attr.addFlashAttribute("usuario", usuario);
			return "redirect:/u/novo/cadastro/usuario";
		}
		try {
			service.salvarUsuario(usuario);
			attr.addFlashAttribute("sucesso", "Operação realizada com sucesso!");
		} catch(DataIntegrityViolationException ex) {
			attr.addFlashAttribute("falha", "Operação cancelada, e-mail já está cadastrado.");
		}
		
		return "redirect:/u/novo/cadastro/usuario";
	}
	
	@GetMapping("/editar/credenciais/usuario/{id}")
	public ModelAndView preEditarCredenciais(@PathVariable("id") Long id) {
		return new ModelAndView("usuario/cadastro", "usuario", service.buscarPorId(id));
	}
	
	@GetMapping("/editar/dados/usuario/{id}/perfis/{perfis}")
	public ModelAndView preEditarCadastroDadosPessoais(@PathVariable("id") Long usuarioId,
													   @PathVariable("perfis") Long[] perfisId) {
		Usuario us = service.buscarPorIdEPerfis(usuarioId, perfisId);
		
		if(us.getPerfis().contains(new Perfil(PerfilTipo.ADMIN.getCod())) &&
				!us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))) {
			return new ModelAndView("usuario/cadastro", "usuario", us);
			
		} else if(us.getPerfis().contains(new Perfil(PerfilTipo.MEDICO.getCod()))){
			Medico medico = medicoService.buscarPorUsuarioId(usuarioId);
			return medico.hasNotId() 
					? new ModelAndView("medico/cadastro", "medico", new Medico(new Usuario(usuarioId)))
					: new ModelAndView("medico/cadastro", "medico", medico);
			
		} else if(us.getPerfis().contains(new Perfil(PerfilTipo.PACIENTE.getCod()))){
			ModelAndView model = new ModelAndView("error");
			model.addObject("status", 403);
			model.addObject("error", "Área Restrita");
			model.addObject("message", "Os dados dos pacientes são restritos a ele.");
			return model;
			
		}
		
		return new ModelAndView("redirect:/u/lista");
	}
	
	@GetMapping("/editar/senha")
	public String abrirEditarSenha() {
		return "usuario/editar-senha";
	}
	
	@PostMapping("/confirmar/senha")
	public String editarSenha(@RequestParam("senha1") String senha1, @RequestParam("senha2") String senha2,
							  @RequestParam("senha3") String senha3, Principal principal,
							  RedirectAttributes attr) {
		if(!senha1.equals(senha2)) {
			attr.addFlashAttribute("falha", "Senhas não conferem. Tente novamente.");
			return "redirect:/u/editar/senha";
		}
		
		Usuario u = service.buscarPorEmail(principal.getName());
		if(!UsuarioService.isSenhaCorreta(senha3, u.getSenha())) {
			attr.addFlashAttribute("falha", "Senha atual não confere. Tente novamente.");
			return "redirect:/u/editar/senha";
		}
		service.alterarSenha(senha1, u);
		attr.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
		return "redirect:/u/editar/senha";
	}
	
	@GetMapping("/novo/cadastro")
	public String novoCadastro(Usuario usuario) {
		return "cadastrar-se";
	}
	
	@GetMapping("/cadastro/realizado")
	public String cadastroRealizado() {
		return "fragments/mensagem";
	}
	
	@PostMapping("/cadastro/paciente/salvar")
	public String salvarCadastroPaciente(Usuario usuario, BindingResult result) {
		try {
			service.salvarCadastroPaciente(usuario);
		} catch(DataIntegrityViolationException ex) {
			result.reject("email", "Ops... Este e-mail já está cadastrado!");
			return "cadastrar-se";
		}
		return "redirect:/u/cadastro/realizado";
	}
}
