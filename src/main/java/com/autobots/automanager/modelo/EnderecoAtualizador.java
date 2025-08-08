package com.autobots.automanager.modelo;

import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Component
public class EnderecoAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private EnderecoRepositorio repositorioEndereco;

	public EnderecoAtualizador(EnderecoRepositorio repositorioEndereco) {
		this.repositorioEndereco = repositorioEndereco;
	}

	public Endereco atualizar(Endereco endereco, Endereco atualizacao) {
		if (atualizacao != null) {
			if (endereco == null) {
				endereco = new Endereco();
			}
			if (!verificador.verificar(atualizacao.getEstado())) {
				endereco.setEstado(atualizacao.getEstado());
			}
			if (!verificador.verificar(atualizacao.getCidade())) {
				endereco.setCidade(atualizacao.getCidade());
			}
			if (!verificador.verificar(atualizacao.getBairro())) {
				endereco.setBairro(atualizacao.getBairro());
			}
			if (!verificador.verificar(atualizacao.getRua())) {
				endereco.setRua(atualizacao.getRua());
			}
			if (!verificador.verificar(atualizacao.getNumero())) {
				endereco.setNumero(atualizacao.getNumero());
			}

			endereco.setInformacoesAdicionais(atualizacao.getInformacoesAdicionais());

			return repositorioEndereco.save(endereco);
		} else if (endereco != null) {
			repositorioEndereco.delete(endereco);
		}
		return null;
	}
}