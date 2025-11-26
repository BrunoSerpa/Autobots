package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Email;

public class EmailAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	private void atualizarDados(Email email, Email atualizacao) {
		if (!verificador.verificar(atualizacao.getEndereco())) {
			email.setEndereco(atualizacao.getEndereco());
		}
	}

	public void atualizar(Email email, Email atualizacao) {
		atualizarDados(email, atualizacao);
	}
}
