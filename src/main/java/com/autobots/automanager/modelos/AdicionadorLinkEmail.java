package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.autobots.automanager.controles.EmailControle;
import com.autobots.automanager.entidades.Email;

public class AdicionadorLinkEmail implements AdicionadorLink<Email> {
	@Override
	public void adicionarLink(List<Email> lista) {
		for (Email email : lista) {
			long id = email.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(EmailControle.class)
							.obterEmail(id))
					.withSelfRel();
			email.add(linkProprio);
		}
	}

	@Override
	public void adicionarLink(Email objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(EmailControle.class)
						.obterEmails())
				.withRel("enderecos");
		objeto.add(linkProprio);
	}
}
