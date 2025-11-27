package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Veiculo;

import java.util.Set;

public class VeiculoAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	public void atualizar(Veiculo veiculo, Veiculo atualizacao) {
		if (atualizacao != null) {
			if (atualizacao.getTipo() != null) {
				veiculo.setTipo(atualizacao.getTipo());
			}
			if (!verificador.verificar(atualizacao.getModelo())) {
				veiculo.setModelo(atualizacao.getModelo());
			}
			if (!verificador.verificar(atualizacao.getPlaca())) {
				veiculo.setPlaca(atualizacao.getPlaca());
			}
			if (atualizacao.getProprietario() != null) {
				veiculo.setProprietario(atualizacao.getProprietario());
			}
			if (atualizacao.getVendas() != null) {
				veiculo.setVendas(atualizacao.getVendas());
			}
		}
	}

	public void atualizar(Set<Veiculo> veiculos, Set<Veiculo> atualizacoes) {
		for (Veiculo atualizacao : atualizacoes) {
			for (Veiculo veiculo : veiculos) {
				if (atualizacao.getId() != null && atualizacao.getId() == veiculo.getId()) {
					atualizar(veiculo, atualizacao);
				}
			}
		}
	}
}
