package com.autobots.automanager.servicos;

import com.autobots.automanager.atualizar.UsuarioAtualizador;
import com.autobots.automanager.converter.UsuarioConverter;
import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.validar.UsuarioValidar;

import lombok.extern.slf4j.Slf4j;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class UsuarioServico {
	private static final String NAO_ENCONTRADO = "Usuário não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String ERRO_ENCONTRADO = "Problemas no Usuário:";

	private final UsuarioAtualizador atualizador;
	private final UsuarioConverter conversor;
	private final UsuarioRepositorio repositorio;
	private final UsuarioValidar validar;

	public UsuarioServico(UsuarioAtualizador atualizador,
			UsuarioConverter conversor,
			UsuarioRepositorio repositorio,
			UsuarioValidar validar) {
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.validar = validar;
	}

	public UsuarioDTO procurar(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido: {}", id);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}
		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					log.warn("Usuário não encontrado: id={}", id);
					return new ResponseStatusException(
							HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<UsuarioDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public UsuarioDTO cadastro(@Valid UsuarioDTO dto) {
		List<String> erros = validar.verificar(dto);
		if (!erros.isEmpty()) {
			String detalhes = String.join("", erros);
			log.warn("Dados inválidos no cadastro: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + "\n" + detalhes);
		}

		Usuario entidade = atualizador.atualizar(null, conversor.convertToEntity(dto));
		Usuario salvo = repositorio.save(entidade);
		log.info("Usuário cadastrado: id={}", salvo.getId());
		return conversor.convertToDto(salvo);
	}

	public UsuarioDTO atualizar(@Valid UsuarioDTO dto) {
		Long id = dto.getId();
		if (id == null || id <= 0) {
			log.warn("ID inválido ao atualizar: {}", id);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}
		
		Usuario existente = repositorio.findById(id)
				.orElseThrow(() -> {
					log.warn("Usuário não encontrado ao atualizar: id={}", id);
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
		Usuario salvo = repositorio.save(existente);
		log.info("Usuário atualizado: id={}", salvo.getId());
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
			log.warn("Usuário não encontrado ao excluir: id={}", id);
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
		}

		repositorio.deleteById(id);
		log.info("Usuário excluído: id={}", id);
	}
}
