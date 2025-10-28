package com.autobots.automanager.servicos;

import com.autobots.automanager.atualizar.DocumentoAtualizador;
import com.autobots.automanager.converter.DocumentoConverter;
import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.validar.DocumentoValidar;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.TreeSet;

@Service
public class DocumentoServico {
	private static final String NAO_ENCONTRADO = "Documento não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String SEM_ID = "Documento não possui ID.";
	private static final String ERRO_ENCONTRADO = "Problemas no Documento:";

	private final UsuarioServico servicoUsuario;
	private final DocumentoAtualizador atualizador;
	private final DocumentoConverter conversor;
	private final DocumentoRepositorio repositorio;
	private final UsuarioRepositorio repositorioUsuario;
	private final DocumentoValidar validar;

	public DocumentoServico(UsuarioServico servicoUsuario,
			DocumentoAtualizador atualizador,
			DocumentoConverter conversor,
			DocumentoRepositorio repositorio,
			UsuarioRepositorio repositorioUsuario,
			DocumentoValidar validar) {
		this.servicoUsuario = servicoUsuario;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.repositorioUsuario = repositorioUsuario;
		this.validar = validar;
	}

	public DocumentoDTO procurar(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}
		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<DocumentoDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public DocumentoDTO cadastro(Long idUsuario, @Valid DocumentoDTO documentoDTO) {
		if (idUsuario == null || idUsuario <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		List<String> erros = validar.verificar(documentoDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		UsuarioDTO cliente = servicoUsuario.procurar(idUsuario);

		cliente.getDocumentos().add(documentoDTO);
		servicoUsuario.atualizar(cliente);
		cliente = servicoUsuario.procurar(idUsuario);

		DocumentoDTO criado = ((TreeSet<DocumentoDTO>) cliente.getDocumentos()).last();
		return criado;
	}

	public DocumentoDTO atualizar(@Valid DocumentoDTO documentoDTO) {
		Long id = documentoDTO.getId();
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}

		Documento existente = repositorio.findById(id)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(documentoDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(documentoDTO));
		Documento salvo = repositorio.save(existente);
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Usuario cliente = repositorioUsuario.findOneByDocumentosId(id)
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		Documento documento = cliente.getDocumentos().stream()
				.filter(d -> d.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> {
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		cliente.getDocumentos().remove(documento);
		repositorioUsuario.save(cliente);
	}
}
