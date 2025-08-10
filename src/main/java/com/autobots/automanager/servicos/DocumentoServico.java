package com.autobots.automanager.servicos;

import java.util.List;

import org.springframework.stereotype.Service;

import com.autobots.automanager.converter.DocumentoConverter;
import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.modelo.DocumentoAtualizador;
import com.autobots.automanager.repositorios.ClienteRepositorio;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.validar.DocumentoValidar;

@Service
public class DocumentoServico {
	private static final String NAO_ENCONTRADO = "Documento não encontrado";
	private static final String ERRO_ENCONTRADO = "Problemas no Documento:";

	private ClienteServico servicoCliente;
	private DocumentoAtualizador atualizador;
	private DocumentoConverter conversor;
	private DocumentoRepositorio repositorio;
	private ClienteRepositorio repositorioCliente;
	private DocumentoValidar validar;

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
		Documento documento = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		return conversor.convertToDto(documento);
	}

	public List<DocumentoDTO> todos() {
		List<Documento> documentos = repositorio.findAll();
		return conversor.convertToDto(documentos);
	}

	public DocumentoDTO cadastro(Long idCliente, DocumentoDTO documentoDTO) {
		List<String> erros = validar.verificar(documentoDTO);
		if (!erros.isEmpty()) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append(ERRO_ENCONTRADO);
			erros.forEach(erro -> mensagem.append("\n").append(erro));
			throw new IllegalArgumentException(mensagem.toString());
		}

		ClienteDTO cliente = servicoCliente.procurar(idCliente);

		cliente.getDocumentos().add(documentoDTO);
		servicoCliente.atualizar(idCliente, cliente);

		return cliente.getDocumentos().get(cliente.getDocumentos().size() - 1);
	}

	public DocumentoDTO atualizar(Long id, DocumentoDTO documentoDTO) {
		Documento documento = repositorio.findById(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));

		atualizador.atualizar(documento, conversor.convertToEntity(documentoDTO));
		repositorio.save(documento);

		return conversor.convertToDto(documento);
	}

	public void excluir(Long id) {
		Cliente cliente = repositorioCliente.findOneByDocumentosId(id)
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));

		Documento documento = cliente.getDocumentos().stream()
				.filter(d -> d.getId().equals(id))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException(NAO_ENCONTRADO));
		cliente.getDocumentos().remove(documento);

		repositorioCliente.save(cliente);
	}
}
