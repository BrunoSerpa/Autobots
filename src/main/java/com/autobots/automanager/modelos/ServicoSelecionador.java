package com.autobots.automanager.modelos;

import java.util.List;

import com.autobots.automanager.entidades.Servico;

import org.springframework.stereotype.Component;
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
