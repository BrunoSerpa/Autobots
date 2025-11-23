package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Venda;

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
