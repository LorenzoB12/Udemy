package com.mballem.curso.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mballem.curso.security.service.EmailService;

@SpringBootApplication
public class DemoSecurityApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(DemoSecurityApplication.class, args);
	}

	@Autowired
	EmailService sender;
	
	@Override
	public void run(String... args) throws Exception {
		sender.enviarPedidoDeConfirmacaoDeCadastro("lorenzobusolli3@gmail.com", "123456");
		System.out.println("rodou");
	}
}
