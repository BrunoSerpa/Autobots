package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Mercadoria;

import java.util.List;

import org.springframework.stereotype.Component;

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
