package com.autobots.automanager.modelo;

public class StringVerificadorNulo {

	public boolean verificar(String dado) {
		boolean nulo = true;
		if (dado != null && !dado.isBlank()) {
			nulo = false;
		}
		return nulo;
	}
}