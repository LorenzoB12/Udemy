package com.mballem.curso.security.exceptions;

@SuppressWarnings("serial")
public class AcessoNegadoException extends RuntimeException{

	public AcessoNegadoException(String message) {
		super(message);
	}
	
}
