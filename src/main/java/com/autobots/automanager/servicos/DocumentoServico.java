package com.autobots.automanager.servicos;

import com.autobots.automanager.converter.DocumentoConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.validar.DocumentoValidar;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class DocumentoServico {
	private static final String NAO_ENCONTRADO = "Documento não encontrado.";
	private static final String ID_INVALIDO = "ID não informado ou inválido.";
	private static final String SEM_ID = "Documento não possui ID.";
	private static final String ERRO_ENCONTRADO = "Problemas no Documento:";

	private final ClienteServico servicoCliente;
	private final DocumentoAtualizador atualizador;
	private final DocumentoConverter conversor;
	private final DocumentoRepositorio repositorio;
	private final ClienteRepositorio repositorioCliente;
	private final DocumentoValidar validar;

	public DocumentoServico(ClienteServico servicoCliente,
			DocumentoAtualizador atualizador,
			DocumentoConverter conversor,
			DocumentoRepositorio repositorio,
			ClienteRepositorio repositorioCliente,
			DocumentoValidar validar) {
		this.servicoCliente = servicoCliente;
		this.atualizador = atualizador;
		this.conversor = conversor;
		this.repositorio = repositorio;
		this.repositorioCliente = repositorioCliente;
		this.validar = validar;
	}

	public DocumentoDTO procurar(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido na procura de documento: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}
		return repositorio.findById(id)
				.map(conversor::convertToDto)
				.orElseThrow(() -> {
					log.warn("Documento não encontrado ao procurar: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});
	}

	public List<DocumentoDTO> todos() {
		return repositorio.findAll()
				.stream()
				.map(conversor::convertToDto)
				.toList();
	}

	public DocumentoDTO cadastro(Long idCliente, @Valid DocumentoDTO documentoDTO) {
		if (idCliente == null || idCliente <= 0) {
			log.warn("ID de cliente inválido no cadastro de documento: {}", idCliente);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		List<String> erros = validar.verificar(documentoDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos no cadastro de documento: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		ClienteDTO cliente = servicoCliente.procurar(idCliente);

		cliente.getDocumentos().add(documentoDTO);
		servicoCliente.atualizar(cliente);

		DocumentoDTO criado = cliente.getDocumentos()
				.get(cliente.getDocumentos().size() - 1);
		log.info("Documento cadastrado: idCliente={}, idDocumento={}", idCliente, criado.getId());
		return criado;
	}

	public DocumentoDTO atualizar(@Valid DocumentoDTO documentoDTO) {
		Long id = documentoDTO.getId();
		if (id == null || id <= 0) {
			log.warn("ID inválido ao atualizar documento: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SEM_ID);
		}

		Documento existente = repositorio.findById(id)
				.orElseThrow(() -> {
					log.warn("Documento não encontrado ao atualizar: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		List<String> erros = validar.verificar(documentoDTO);
		if (!erros.isEmpty()) {
			String detalhes = String.join("; ", erros);
			log.warn("Dados inválidos na atualização de documento: {}", detalhes);
			throw new ResponseStatusException(
					HttpStatus.BAD_REQUEST,
					ERRO_ENCONTRADO + " " + detalhes);
		}

		atualizador.atualizar(existente, conversor.convertToEntity(documentoDTO));
		Documento salvo = repositorio.save(existente);
		log.info("Documento atualizado: id={}", salvo.getId());
		return conversor.convertToDto(salvo);
	}

	public void excluir(Long id) {
		if (id == null || id <= 0) {
			log.warn("ID inválido ao excluir documento: {}", id);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ID_INVALIDO);
		}

		Cliente cliente = repositorioCliente.findOneByDocumentosId(id)
				.orElseThrow(() -> {
					log.warn("Documento não encontrado em cliente ao excluir: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		Documento documento = cliente.getDocumentos().stream()
				.filter(d -> d.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> {
					log.warn("Documento não encontrado na lista do cliente ao excluir: id={}", id);
					return new ResponseStatusException(HttpStatus.NOT_FOUND, NAO_ENCONTRADO);
				});

		cliente.getDocumentos().remove(documento);
		repositorioCliente.save(cliente);
		log.info("Documento excluído: id={}", id);
	}
}
