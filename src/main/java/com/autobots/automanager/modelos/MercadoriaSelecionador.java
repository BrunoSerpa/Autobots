package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Mercadoria;

@Component
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
