package com.autobots.automanager.modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Component
public class TelefoneAtualizador {
	private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

	private final TelefoneRepositorio repositorioTelefone;

	public TelefoneAtualizador(TelefoneRepositorio repositorioTelefone) {
		this.repositorioTelefone = repositorioTelefone;
	}

	public Telefone atualizar(Telefone telefone, Telefone atualizacao) {
		if (atualizacao == null) {
			return deletarSeExistir(telefone);
		}

		boolean novo = (telefone == null);
		if (novo) {
			telefone = new Telefone();
		}

		aplicarAtualizacao(telefone, atualizacao);

		if (novo) {
			salvarNovo(telefone);
		}
		return telefone;
	}

	public void atualizar(List<Telefone> telefones, List<Telefone> atualizacoes) {
		listaPadrao(telefones);
		listaPadrao(atualizacoes);

		List<Telefone> semId = extrairSemId(atualizacoes);
		Map<Long, Telefone> porId = indexarPorId(telefones);
		Set<Long> idsUsados = atualizarExistentes(porId, atualizacoes);
		int consumidos = reconciliar(telefones, semId, idsUsados);

		persistirNovos(
				telefones,
				semId.subList(consumidos, semId.size()));
	}

	private Telefone deletarSeExistir(Telefone telefone) {
		if (telefone != null) {
			repositorioTelefone.delete(telefone);
		}
		return null;
	}

	private void listaPadrao(List<Telefone> possivel) {
		if (possivel == null)
			possivel = new ArrayList<>();
	}

	private List<Telefone> extrairSemId(List<Telefone> atualizacoes) {
		return atualizacoes.stream()
				.filter(t -> t.getId() == null)
				.toList();
	}

	private Map<Long, Telefone> indexarPorId(List<Telefone> telefones) {
		return telefones.stream()
				.filter(t -> t.getId() != null)
				.collect(Collectors.toMap(Telefone::getId, Function.identity()));
	}

	private Set<Long> atualizarExistentes(
			Map<Long, Telefone> existentes,
			List<Telefone> atualizacoes) {
		Set<Long> usados = new HashSet<>();
		atualizacoes.stream()
				.filter(t -> t.getId() != null)
				.forEach(atual -> {
					Telefone orig = existentes.get(atual.getId());
					if (orig != null) {
						atualizar(orig, atual);
						usados.add(orig.getId());
					}
				});
		return usados;
	}

	private int reconciliar(
			List<Telefone> telefones,
			List<Telefone> semId,
			Set<Long> idsUsados) {
		Iterator<Telefone> iter = telefones.iterator();
		int indice = 0;

		while (iter.hasNext()) {
			Telefone atual = iter.next();
			if (idsUsados.contains(atual.getId())) {
				continue;
			}
			if (indice < semId.size()) {
				atualizar(atual, semId.get(indice));
				indice++;
			} else {
				iter.remove();
				repositorioTelefone.delete(atual);
			}
		}
		return indice;
	}

	private void aplicarAtualizacao(Telefone telefone, Telefone atualizacao) {
		String ddd = atualizacao.getDdd();
		if (!NULO.verificar(ddd)) {
			telefone.setDdd(ddd);
		}

		String numero = atualizacao.getNumero();
		if (!NULO.verificar(numero)) {
			telefone.setNumero(numero);
		}
	}

	private void persistirNovos(
			List<Telefone> telefones,
			List<Telefone> novos) {
		for (Telefone novo : novos) {
			try {
				Telefone salvo = repositorioTelefone.save(novo);
				telefones.add(salvo);
			} catch (Exception ex) {
				throw new IllegalArgumentException("DDD ou número inválido ou já cadastrado");
			}
		}
	}

	private void salvarNovo(Telefone telefone) {
		try {
			repositorioTelefone.save(telefone);
		} catch (Exception ex) {
			throw new IllegalArgumentException("DDD ou número inválido ou já cadastrado");
		}
	}
}