package com.autobots.automanager.modelo;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Component
public class EnderecoAtualizador {
	private static final StringVerificadorNulo NULO = new StringVerificadorNulo();
	private EnderecoRepositorio repositorioEndereco;

	public EnderecoAtualizador(EnderecoRepositorio repositorioEndereco) {
		this.repositorioEndereco = repositorioEndereco;
	}

	public Endereco atualizar(Endereco endereco, Endereco atualizacao) {
		if (atualizacao == null) {
			if (endereco != null) {
				repositorioEndereco.delete(endereco);
			}
			return null;
		}

		boolean novo = (endereco == null);
		if (novo) {
			endereco = new Endereco();
		}

		Map<Supplier<String>, Consumer<String>> campos = Map.of(
				atualizacao::getCidade, endereco::setCidade,
				atualizacao::getBairro, endereco::setBairro,
				atualizacao::getRua, endereco::setRua,
				atualizacao::getNumero, endereco::setNumero);

		campos.forEach((getter, setter) -> {
			String valor = getter.get();
			if (!NULO.verificar(valor)) {
				setter.accept(valor);
			}
		});

		endereco.setEstado(atualizacao.getEstado());
		endereco.setBairro(atualizacao.getBairro());
		endereco.setInformacoesAdicionais(atualizacao.getInformacoesAdicionais());

		if (novo) {
			return repositorioEndereco.save(endereco);
		}

		return endereco;
	}
}
