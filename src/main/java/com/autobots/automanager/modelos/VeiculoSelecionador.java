package com.autobots.automanager.modelos;

import java.util.List;

import com.autobots.automanager.entidades.Veiculo;

public class VeiculoSelecionador {
	public Veiculo selecionar(List<Veiculo> veiculos, long id) {
		Veiculo selecionado = null;
		for (Veiculo veiculo : veiculos) {
			if (veiculo.getId() == id) {
				selecionado = veiculo;
				break;
			}
		}
		return selecionado;
	}
}
