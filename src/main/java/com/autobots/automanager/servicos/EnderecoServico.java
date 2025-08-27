package com.autobots.automanager.servicos;

import com.autobots.automanager.atualizar.EnderecoAtualizador;
import com.autobots.automanager.converter.EnderecoConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.validar.EnderecoValidar;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class EnderecoServico {
	private static final String NAO_ENCONTRADO = "Endereço não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
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
			log.warn("ID inválido na procura de endereço: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					log.warn("Endereço não encontrado ao procurar: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<EnderecoDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public EnderecoDTO cadastro(Long idCliente, @Valid EnderecoDTO dto) {
		if (idCliente == null || idCliente <= 0) {
			log.warn("ID de cliente inválido no cadastro de endereço: {}", idCliente);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos no cadastro de endereço: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		ClienteDTO cliente = servicoCliente.procurar(idCliente);
		if (cliente.getEndereco() != null) {
			log.warn("Tentativa de cadastrar endereço duplicado para cliente: {}", idCliente);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ENDERECO_EXISTENTE);
		}

		cliente.setEndereco(dto);
		servicoCliente.atualizar(cliente);
		cliente = servicoCliente.procurar(idCliente);

		EnderecoDTO criado = cliente.getEndereco();
		log.info("Endereço cadastrado: idCliente={}, idEndereco={}", idCliente, criado.getId());
		return criado;
	}

	public EnderecoDTO atualizar(@Valid EnderecoDTO dto) {
		Long id = dto.getId();
		if (id == null || id <= 0) {
			log.warn("ID inválido ao atualizar endereço: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Endereco existente = repositorio.findById(id)
				.orElseThrow(() -> {
					log.warn("Endereço não encontrado ao atualizar: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos na atualização de endereço: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(dto));
		Endereco salvo = repositorio.save(existente);

		log.info("Endereço atualizado: id={}", salvo.getId());
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido ao excluir endereço: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Cliente cliente = repositorioCliente.findOneByEnderecoId(id)
				.orElseThrow(() -> {
					log.warn("Endereço não encontrado em cliente ao excluir: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		cliente.setEndereco(null);
		repositorioCliente.save(cliente);

		log.info("Endereço excluído: id={}", id);
	}
}
