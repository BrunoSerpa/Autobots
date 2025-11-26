package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Empresa;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EmpresaSelecionador {
	public Empresa selecionar(List<Empresa> empresas, long id) {
		Empresa selecionado = null;
		for (Empresa empresa : empresas) {
			if (empresa.getId() == id) {
				selecionado = empresa;
				break;
			}
		}
		return selecionado;
	}
}
