package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.autobots.automanager.converter.EnderecoConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.modelo.EnderecoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.validar.EnderecoValidar;

@Service
public class EnderecoServico {
	private static final String NAO_ENCONTRADO = "Endereço não encontrado.";
	private static final String SEM_ID = "ID não informado ou inválido.";
	private static final String ENDERECO_EXISTENTE = "Cliente já possui endereço.";
	private static final String ERRO_ENCONTRADO = "Problemas no endereço:";

	private final ClienteServico servicoCliente;
	private final EnderecoAtualizador atualizador;
	private final EnderecoConverter conversor;
	private final EnderecoRepositorio repositorio;
	private final ClienteRepositorio repositorioCliente;
	private final EnderecoValidar validar;

	public EnderecoServico(ClienteServico servicoCliente,
			EnderecoAtualizador atualizador,
			EnderecoConverter conversor,
			EnderecoRepositorio repositorio,
			ClienteRepositorio repositorioCliente,
			EnderecoValidar validar) {
		this.servicoCliente = servicoCliente;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.repositorioCliente = repositorioCliente;
		this.validar = validar;
	}

	public EnderecoDTO procurar(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}
		Endereco e = repositorio.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO));
		return conversor.convertToDto(e);
	}

	public List<EnderecoDTO> todos() {
		List<Endereco> lista = repositorio.findAll();
		return conversor.convertToDto(lista);
	}

	public EnderecoDTO cadastro(Long idCliente, EnderecoDTO dto) {
		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String msg = ERRO_ENCONTRADO + "\n" + String.join("\n", erros);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}

		ClienteDTO cliente = servicoCliente.procurar(idCliente);

		if (cliente.getEndereco() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ENDERECO_EXISTENTE);
		}

		cliente.setEndereco(dto);
		servicoCliente.atualizar(cliente);

		return cliente.getEndereco();
	}

	public EnderecoDTO atualizar(EnderecoDTO dto) {
		if (dto.getId() == null || dto.getId() <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}

		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String msg = ERRO_ENCONTRADO + "\n" + String.join("\n", erros);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
		}

		Endereco existente = repositorio.findById(dto.getId())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO));

		atualizador.atualizar(existente, conversor.convertToEntity(dto));
		repositorio.save(existente);

		return conversor.convertToDto(existente);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}

		Cliente cliente = repositorioCliente.findOneByEnderecoId(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO));

		cliente.setEndereco(null);
		repositorioCliente.save(cliente);
	}
}
