package com.autobots.automanager.modelos;

import java.util.List;

import com.autobots.automanager.entidades.Empresa;


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
