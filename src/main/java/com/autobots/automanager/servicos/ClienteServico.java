package com.autobots.automanager.servicos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.autobots.automanager.converter.ClienteConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.validar.ClienteValidar;
import com.autobots.automanager.validar.DocumentoValidar;
import com.autobots.automanager.validar.TelefoneValidar;

@Service
public class ClienteServico {
	private static final String NAO_ENCONTRADO = "Cliente não encontrado";

	private ClienteAtualizador atualizador;
	private ClienteConverter conversor;
	private ClienteRepositorio repositorio;
	private ClienteValidar validar;
	private DocumentoValidar validarDocumento;
	private TelefoneValidar validarTelefone;

	public ClienteServico(ClienteAtualizador atualizador,
			ClienteConverter conversor,
			ClienteRepositorio repositorio,
			ClienteValidar validar,
			DocumentoValidar validarDocumento,
			TelefoneValidar validarTelefone) {
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.validar = validar;
		this.validarDocumento = validarDocumento;
		this.validarTelefone = validarTelefone;
	}

	public ClienteDTO procurar(Long id) {
		Cliente cliente = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		return conversor.convertToDto(cliente);
	}

	public List<ClienteDTO> todos() {
		List<Cliente> clientes = repositorio.findAll();
		return conversor.convertToDto(clientes);
	}

	public ClienteDTO cadastro(ClienteDTO clienteDTO) {
		List<String> erros = validar.verificar(clienteDTO);
		if (!erros.isEmpty()) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falta os dados:");
			erros.forEach(erro -> mensagem.append("\n").append(erro));
			throw new IllegalArgumentException(mensagem.toString());
		}

		Cliente cliente = new Cliente();
		atualizador.atualizar(cliente, conversor.convertToEntity(clienteDTO));
		repositorio.save(cliente);

		return conversor.convertToDto(cliente);
	}

	public ClienteDTO atualizar(Long id, ClienteDTO clienteDTO) {
		Cliente cliente = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));

		List<String> erros = new ArrayList<>();
		if (cliente.getTelefones().size() < clienteDTO.getTelefones().size()) {
			clienteDTO.getDocumentos().stream()
					.filter(documento -> Objects.isNull(documento.getId()))
					.map(validarDocumento::verificar)
					.forEach(erros::addAll);
		}
		if (cliente.getTelefones().size() < clienteDTO.getTelefones().size()) {
			clienteDTO.getTelefones().stream()
					.filter(telefone -> Objects.isNull(telefone.getId()))
					.map(validarTelefone::verificar)
					.forEach(erros::addAll);
		}
		if (!erros.isEmpty()) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falta os dados:");
			erros.forEach(erro -> mensagem.append("\n").append(erro));
			throw new IllegalArgumentException(mensagem.toString());
		}

		atualizador.atualizar(cliente, conversor.convertToEntity(clienteDTO));
		repositorio.save(cliente);
		return conversor.convertToDto(cliente);
	}

	public void excluir(Long id) {
		Cliente cliente = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));

		atualizador.atualizar(cliente, new Cliente());

		repositorio.deleteById(id);
	}
}
