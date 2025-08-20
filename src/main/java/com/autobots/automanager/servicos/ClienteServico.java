package com.autobots.automanager.servicos;

import com.autobots.automanager.converter.ClienteConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.modelo.ClienteAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.validar.ClienteValidar;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class ClienteServico {
	private static final String NAO_ENCONTRADO = "Cliente não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String ERRO_ENCONTRADO = "Problemas no Cliente:";

	private final ClienteAtualizador atualizador;
	private final ClienteConverter conversor;
	private final ClienteRepositorio repositorio;
	private final ClienteValidar validar;

	public ClienteServico(ClienteAtualizador atualizador,
			ClienteConverter conversor,
			ClienteRepositorio repositorio,
			ClienteValidar validar) {
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.validar = validar;
	}

	public ClienteDTO procurar(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido: {}", id);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}
		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					log.warn("Cliente não encontrado: id={}", id);
					return new ResponseStatusException(
							HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<ClienteDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public ClienteDTO cadastro(@Valid ClienteDTO dto) {
		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("", erros);
			log.warn("Dados inválidos no cadastro: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + "\n" + detalhes);
		}

		Cliente entidade = conversor.convertToEntity(dto);
		Cliente salvo = repositorio.save(entidade);
		log.info("Cliente cadastrado: id={}", salvo.getId());
		return conversor.convertToDto(salvo);
	}

	public ClienteDTO atualizar(@Valid ClienteDTO dto) {
		Long id = dto.getId();
		if (id == null || id <= 0) {
			log.warn("ID inválido ao atualizar: {}", id);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}
		
		Cliente existente = repositorio.findById(id)
				.orElseThrow(() -> {
					log.warn("Cliente não encontrado ao atualizar: id={}", id);
					return new ResponseStatusException(
							HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos na atualização: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(dto));
		Cliente salvo = repositorio.save(existente);
		log.info("Cliente atualizado: id={}", salvo.getId());
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido ao excluir: {}", id);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		boolean existe = repositorio.existsById(id);
		if (!existe) {
			log.warn("Cliente não encontrado ao excluir: id={}", id);
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
		}

		repositorio.deleteById(id);
		log.info("Cliente excluído: id={}", id);
	}
}
