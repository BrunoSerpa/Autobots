package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autobots.automanager.converter.EnderecoConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Service
public class EnderecoServico {
	private static final String NAO_ENCONTRADO = "Endereço não encontrado";
	private static final String ENDERECO_EXISTENTE = "Cliente possui endereço";

	private ClienteServico clienteServico;
	private EnderecoAtualizador enderecoAtualizador;
	private EnderecoRepositorio repositorioEndereco;
	private EnderecoConverter conversorEndereco;

	public EnderecoServico(ClienteServico clienteServico,
			EnderecoAtualizador enderecoAtualizador,
			EnderecoConverter conversorEndereco,
			EnderecoRepositorio repositorioEndereco) {
		this.clienteServico = clienteServico;
		this.enderecoAtualizador = enderecoAtualizador;
		this.conversorEndereco = conversorEndereco;
		this.repositorioEndereco = repositorioEndereco;
	}

	public EnderecoDTO procurar(Long id) {
		Endereco endereco = repositorioEndereco.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		return conversorEndereco.convertToDto(endereco);
	}

	public List<EnderecoDTO> todos() {
		List<Endereco> enderecos = repositorioEndereco.findAll();
		return conversorEndereco.convertToDto(enderecos);
	}

	public EnderecoDTO cadastro(Long idCliente, EnderecoDTO enderecoDTO) {
		ClienteDTO cliente = clienteServico.procurar(idCliente);
		if (cliente.getEndereco() != null) {
			throw new IllegalArgumentException(ENDERECO_EXISTENTE);
		}
		cliente.setEndereco(enderecoDTO);
		clienteServico.atualizar(idCliente, cliente);
		return cliente.getEndereco();
	}

	public EnderecoDTO atualizar(Long id, EnderecoDTO enderecoDTO) {
		Endereco endereco = repositorioEndereco.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		enderecoAtualizador.atualizar(endereco, conversorEndereco.convertToEntity(enderecoDTO));
		return conversorEndereco.convertToDto(endereco);
	}

	public void excluir(Long idCliente) {
		ClienteDTO cliente = clienteServico.procurar(idCliente);
		if (cliente.getEndereco() == null) {
			throw new IllegalArgumentException(NAO_ENCONTRADO);
		}
		cliente.setEndereco(null);
		clienteServico.atualizar(idCliente, cliente);
	}
}
