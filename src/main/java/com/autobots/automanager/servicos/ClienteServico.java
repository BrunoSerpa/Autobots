package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autobots.automanager.converter.ClienteConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Service
public class ClienteServico {
	private static final String NAO_ENCONTRADO = "Cliente não encontrado";

	private ClienteAtualizador clienteAtualizador;
	private ClienteRepositorio repositorioCliente;
	private ClienteConverter conversorCliente;

	public ClienteServico(ClienteAtualizador clienteAtualizador,
			ClienteConverter conversorCliente,
			ClienteRepositorio repositorioCliente) {
		this.clienteAtualizador = clienteAtualizador;
		this.conversorCliente = conversorCliente;
		this.repositorioCliente = repositorioCliente;
	}

	public ClienteDTO procurar(Long id) {
		Cliente cliente = repositorioCliente.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		return conversorCliente.convertToDto(cliente);
	}

	public List<ClienteDTO> todos() {
		List<Cliente> clientes = repositorioCliente.findAll();
		return conversorCliente.convertToDto(clientes);
	}

	public ClienteDTO cadastro(ClienteDTO clienteDTO) {
		Cliente cliente = new Cliente();
		clienteAtualizador.atualizar(cliente, conversorCliente.convertToEntity(clienteDTO));
		repositorioCliente.save(cliente);
		return conversorCliente.convertToDto(cliente);
	}

	public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
		Cliente cliente = repositorioCliente.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		clienteAtualizador.atualizar(cliente, conversorCliente.convertToEntity(clienteDTO));
		repositorioCliente.save(cliente);
		return conversorCliente.convertToDto(cliente);
	}

	public void excluir(Long id) {
		Cliente cliente = repositorioCliente.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		clienteAtualizador.atualizar(new Cliente(), cliente);
		repositorioCliente.deleteById(id);
	}
}
