package com.autobots.automanager.modelo;

import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Cliente;

@Component
public class ClienteAtualizador {
	private StringVerificadorNulo verificador = new StringVerificadorNulo();
	private EnderecoAtualizador enderecoAtualizador;
	private DocumentoAtualizador documentoAtualizador;
	private TelefoneAtualizador telefoneAtualizador;

	public ClienteAtualizador(DocumentoAtualizador documentoAtualizador,
			EnderecoAtualizador enderecoAtualizador,
			TelefoneAtualizador telefoneAtualizador) {
		this.documentoAtualizador = documentoAtualizador;
		this.enderecoAtualizador = enderecoAtualizador;
		this.telefoneAtualizador = telefoneAtualizador;
	}

	private void atualizarDados(Cliente cliente, Cliente atualizacao) {
		if (!verificador.verificar(atualizacao.getNome())) {
			cliente.setNome(atualizacao.getNome());
		}

		cliente.setNomeSocial(atualizacao.getNomeSocial());

		if (atualizacao.getDataCadastro() != null) {
			cliente.setDataCadastro(atualizacao.getDataCadastro());
		}

		cliente.setDataNascimento(atualizacao.getDataNascimento());

		cliente.setEndereco(enderecoAtualizador.atualizar(cliente.getEndereco(), atualizacao.getEndereco()));
	}

	public void atualizar(Cliente cliente, Cliente atualizacao) {
		atualizarDados(cliente, atualizacao);
		documentoAtualizador.atualizar(cliente.getDocumentos(), atualizacao.getDocumentos());
		telefoneAtualizador.atualizar(cliente.getTelefones(), atualizacao.getTelefones());
	}
}
