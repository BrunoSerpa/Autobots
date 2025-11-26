package com.autobots.automanager.modelos;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Servico;

import java.util.List;

@Component
public class ServicoSelecionador {
	public Servico selecionar(List<Servico> servicos, long id) {
		Servico selecionado = null;
		for (Servico servico : servicos) {
			if (servico.getId() == id) {
				selecionado = servico;
				break;
			}
		}

		return selecionado;
	}
}
