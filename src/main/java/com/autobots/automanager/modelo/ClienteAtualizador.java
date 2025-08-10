package com.autobots.automanager.modelo;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Component
public class ClienteAtualizador {
	private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

	private DocumentoAtualizador atualizadorDocumento;
	private EnderecoAtualizador atualizadorEndereco;
	private TelefoneAtualizador atualizadorTelefone;
	private ClienteRepositorio repositorioCliente;

	public ClienteAtualizador(DocumentoAtualizador atualizadorDocumento,
			EnderecoAtualizador atualizadorEndereco,
			TelefoneAtualizador atualizadorTelefone,
			ClienteRepositorio repositorioCliente) {
		this.atualizadorDocumento = atualizadorDocumento;
		this.atualizadorEndereco = atualizadorEndereco;
		this.atualizadorTelefone = atualizadorTelefone;
		this.repositorioCliente = repositorioCliente;
	}

	public Cliente atualizar(Cliente cliente, Cliente atualizacao) {
		if (atualizacao == null) {
			if (cliente != null) {
				repositorioCliente.delete(cliente);
			}
			return null;
		}

		boolean novo = (cliente == null);
		if (novo) {
			cliente = new Cliente();
		}

		Map<Supplier<String>, Consumer<String>> campos = Map.of(
				atualizacao::getNome, cliente::setNome,
				atualizacao::getNomeSocial, cliente::setNomeSocial);

		campos.forEach((getter, setter) -> {
			String valor = getter.get();
			if (!NULO.verificar(valor)) {
				setter.accept(valor);
			}
		});

		if (cliente.getDataCadastro() == null) {
			cliente.setDataCadastro(atualizacao.getDataCadastro());
		}

		if (atualizacao.getDataNascimento() != null) {
			cliente.setDataNascimento(atualizacao.getDataNascimento());
		}

		cliente.setEndereco(atualizadorEndereco.atualizar(cliente.getEndereco(), atualizacao.getEndereco()));

		atualizadorDocumento.atualizar(cliente.getDocumentos(), atualizacao.getDocumentos());
		atualizadorTelefone.atualizar(cliente.getTelefones(), atualizacao.getTelefones());

		if (novo) {
			repositorioCliente.save(cliente);
		}

		return cliente;
	}
}
