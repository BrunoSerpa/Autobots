package com.autobots.automanager.modelos;

import java.util.List;

import com.autobots.automanager.entidades.Mercadoria;

public class MercadoriaSelecionador {
	public Mercadoria selecionar(List<Mercadoria> mercadorias, long id) {
		Mercadoria selecionado = null;
		for (Mercadoria mercadoria : mercadorias) {
			if (mercadoria.getId() == id) {
				selecionado = mercadoria;
				break;
			}
		}
		return selecionado;
	}
}
