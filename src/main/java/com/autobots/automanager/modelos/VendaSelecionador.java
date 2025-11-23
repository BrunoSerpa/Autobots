package com.autobots.automanager.modelos;

import java.util.List;

import com.autobots.automanager.entidades.Venda;

public class VendaSelecionador {
	public Venda selecionar(List<Venda> vendas, long id) {
		Venda selecionado = null;
		for (Venda venda : vendas) {
			if (venda.getId() == id) {
				selecionado = venda;
				break;
			}
		}
		return selecionado;
	}
}
