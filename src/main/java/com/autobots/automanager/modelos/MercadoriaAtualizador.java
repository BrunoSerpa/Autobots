package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Mercadoria;

public class MercadoriaAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	private void atualizarDados(Mercadoria mercadoria, Mercadoria atualizacao) {
		if (atualizacao.getValidade() != null) {
			mercadoria.setValidade(atualizacao.getValidade());
		}
		if (atualizacao.getFabricao() != null) {
			mercadoria.setFabricao(atualizacao.getFabricao());
		}
		if (atualizacao.getCadastro() != null) {
			mercadoria.setCadastro(atualizacao.getCadastro());
		}
		if (!verificador.verificar(atualizacao.getNome())){
			mercadoria.setNome(atualizacao.getNome());
		}
		mercadoria.setQuantidade(atualizacao.getQuantidade());
		mercadoria.setValor(atualizacao.getValor());
		if (!verificador.verificar(atualizacao.getDescricao())) {
			mercadoria.setDescricao(atualizacao.getDescricao());
		}
	}

	public void atualizar(Mercadoria mercadoria, Mercadoria atualizacao) {
		atualizarDados(mercadoria, atualizacao);
	}
}
