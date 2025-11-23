package com.autobots.automanager.modelos;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import com.autobots.automanager.controles.VendaControle;
import com.autobots.automanager.entidades.Venda;

public class AdicionadorLinkVenda implements AdicionadorLink<Venda> {
	@Override
	public void adicionarLink(List<Venda> lista) {
		for (Venda venda : lista) {
			long id = venda.getId();
			Link linkProprio = WebMvcLinkBuilder
					.linkTo(WebMvcLinkBuilder
							.methodOn(VendaControle.class)
							.obterVenda(id))
					.withSelfRel();
			venda.add(linkProprio);
		}
	}

	@Override
	public void adicionarLink(Venda objeto) {
		Link linkProprio = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder
						.methodOn(VendaControle.class)
						.obterVendas())
				.withRel("enderecos");
		objeto.add(linkProprio);
	}
}