package com.autobots.automanager.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Component
public class TelefoneAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();

	private TelefoneRepositorio repositorioTelefone;

	public TelefoneAtualizador(TelefoneRepositorio repositorioTelefone) {
		this.repositorioTelefone = repositorioTelefone;
	}

	public void atualizar(Telefone telefone, Telefone atualizacao) {
		if (atualizacao != null) {
			if (telefone == null) {
				telefone = new Telefone();
			}
			if (!verificador.verificar(atualizacao.getDdd())) {
				telefone.setDdd(atualizacao.getDdd());
			}
			if (!verificador.verificar(atualizacao.getNumero())) {
				telefone.setNumero(atualizacao.getNumero());
			}
		} else if (telefone != null) {
			repositorioTelefone.delete(telefone);
		}
	}

	public void atualizar(List<Telefone> telefones, List<Telefone> atualizacoes) {
		List<Telefone> semId = atualizacoes.stream()
				.filter(telefone -> telefone.getId() == null)
				.toList();

		List<Long> usados = new ArrayList<>();
		for (Telefone atualizacao : atualizacoes.stream()
				.filter(telefone -> telefone.getId() != null)
				.toList()) {
			for (Telefone telefone : telefones) {
				System.out.println(telefone);
				if (Objects.equals(telefone.getId(), atualizacao.getId())) {
					atualizar(telefone, atualizacao);
					usados.add(telefone.getId());
					break;
				}
			}
		}

		Iterator<Telefone> iterador = telefones.iterator();
		int posicao = 0;
		while (iterador.hasNext()) {
			Telefone telefone = iterador.next();
			if (usados.contains(telefone.getId())) {
				continue;
			}
			if (posicao >= semId.size()) {
				iterador.remove();
				repositorioTelefone.delete(telefone);
			} else {
				atualizar(telefone, semId.get(posicao));
				posicao++;
			}
		}

		for (; posicao < semId.size(); posicao++) {
			Telefone novo = repositorioTelefone.save(semId.get(posicao));
			telefones.add(novo);
			usados.add(novo.getId());
		}
		repositorioTelefone.saveAll(telefones);
	}
}
