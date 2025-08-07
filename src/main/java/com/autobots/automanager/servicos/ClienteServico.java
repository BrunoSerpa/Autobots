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
	private static final String naoEncontrado = "Cliente não encontrado";

	private ClienteRepositorio repositorioCliente;
	private ClienteConverter conversorCliente;

	public ClienteServico(ClienteRepositorio repositorioCliente, ClienteConverter conversorCliente) {
		this.repositorioCliente = repositorioCliente;
		this.conversorCliente = conversorCliente;
	}

	public ClienteDTO procurar(Long id) {
		Cliente cliente = repositorioCliente.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(naoEncontrado));
		ClienteDTO clienteDTO = conversorCliente.convertToDto(cliente);
		return clienteDTO;
	}

	public List<ClienteDTO> todos() {
		List<Cliente> clientes = repositorioCliente.findAll();
		List<ClienteDTO> clientesDTO = conversorCliente.convertToDto(clientes);
		return clientesDTO;
	}

	public ClienteDTO cadastro(ClienteDTO clienteDTO) {
		Cliente cliente = conversorCliente.convertToEntity(clienteDTO);
		repositorioCliente.save(cliente);
		ClienteDTO clienteRetornado = conversorCliente.convertToDto(cliente);
		return clienteRetornado;
	}

	public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
		Cliente cliente = repositorioCliente.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(naoEncontrado));
		Cliente clienteNovo = conversorCliente.convertToEntity(clienteDTO);
		ClienteAtualizador atualizador = new ClienteAtualizador();
		atualizador.atualizar(cliente, clienteNovo);
		repositorioCliente.save(cliente);
		ClienteDTO clienteRetornado = conversorCliente.convertToDto(cliente);
		return clienteRetornado;
	}

	public void excluir(Long id) {
		repositorioCliente.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(naoEncontrado));
		repositorioCliente.deleteById(id);
	}
}
