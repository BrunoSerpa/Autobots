package com.autobots.automanager.modelos;

import com.autobots.automanager.entidades.Email;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EmailSelecionador {
	public Email selecionar(List<Email> emails, long id) {
		Email selecionado = null;
		for (Email email : emails) {
			if (email.getId() == id) {
				selecionado = email;
				break;
			}
		}
		return selecionado;
	}
}
