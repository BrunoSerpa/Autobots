package com.autobots.automanager.modelos;

import java.util.Set;

import com.autobots.automanager.entidades.Venda;

public class VendaAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Venda venda, Venda atualizacao) {
		if (atualizacao != null) {
			if (atualizacao.getCadastro() != null) {
				venda.setCadastro(atualizacao.getCadastro());
			}
			if (!verificador.verificar(atualizacao.getIdentificacao())) {
				venda.setIdentificacao(atualizacao.getIdentificacao());
			}
			if (atualizacao.getUsuario() != null) {
				venda.setUsuario(atualizacao.getUsuario());
			}
			if (atualizacao.getFuncionario() != null) {
				venda.setFuncionario(atualizacao.getFuncionario());
			}
			if (atualizacao.getMercadorias() != null) {
				venda.setMercadorias(atualizacao.getMercadorias());
			}
			if (atualizacao.getServicos() != null) {
				venda.setServicos(atualizacao.getServicos());
			}
			if (atualizacao.getVeiculo() != null) {
				venda.setVeiculo(atualizacao.getVeiculo());
			}
		}
	}

	public void atualizar(Set<Venda> vendas, Set<Venda> atualizacoes) {
		for (Venda atualizacao : atualizacoes) {
			for (Venda venda : vendas) {
				if (atualizacao.getId() != null) {
					if (atualizacao.getId() == venda.getId()) {
						atualizar(venda, atualizacao);
					}
				}
			}
		}
	}
}
