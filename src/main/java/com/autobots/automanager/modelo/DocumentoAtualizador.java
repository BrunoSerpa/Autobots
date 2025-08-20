package com.autobots.automanager.modelo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@Component
public class DocumentoAtualizador {
	private static final String NUMERO_EXISTENTE = "Número de documento já cadastrado";
	private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

	private final DocumentoRepositorio repositorioDocumento;

	public DocumentoAtualizador(DocumentoRepositorio repositorioDocumento) {
		this.repositorioDocumento = repositorioDocumento;
	}

	public Documento atualizar(Documento documento, Documento atualizacao) {
		if (atualizacao == null) {
			return deletarSeExistir(documento);
		}

		boolean novo = (documento == null);
		if (novo) {
			documento = new Documento();
		}

		aplicarAtualizacao(documento, atualizacao);

		if (novo) {
			salvarNovo(documento);
		}
		return documento;
	}

	public void atualizar(List<Documento> documentos, List<Documento> atualizacoes) {
		listaPadrao(documentos);
		listaPadrao(atualizacoes);
		List<Documento> semIdAtualizacoes = extrairSemId(atualizacoes);
		Map<Long, Documento> porId = indexarPorId(documentos);
		Set<Long> idsUsados = atualizarExistentes(porId, atualizacoes);
		int consumidos = reconciliar(documentos, semIdAtualizacoes, idsUsados);

		persistirNovos(
				documentos,
				semIdAtualizacoes.subList(consumidos, semIdAtualizacoes.size()));
	}

	private Documento deletarSeExistir(Documento documento) {
		if (documento != null) {
			repositorioDocumento.delete(documento);
		}
		return null;
	}

	private void listaPadrao(List<Documento> possivel) {
		if (possivel == null)
			possivel = new ArrayList<>();
	}

	private List<Documento> extrairSemId(List<Documento> atualizacoes) {
		return atualizacoes.stream()
				.filter(d -> d.getId() == null)
				.toList();
	}

	private Map<Long, Documento> indexarPorId(List<Documento> documentos) {
		return documentos.stream()
				.filter(d -> d.getId() != null)
				.collect(Collectors.toMap(Documento::getId, Function.identity()));
	}

	private Set<Long> atualizarExistentes(
			Map<Long, Documento> existentes,
			List<Documento> atualizacoes) {
		Set<Long> idsUsados = new HashSet<>();
		atualizacoes.stream()
				.filter(d -> d.getId() != null)
				.forEach(atual -> {
					Documento original = existentes.get(atual.getId());
					if (original != null) {
						atualizar(original, atual);
						idsUsados.add(original.getId());
					}
				});
		return idsUsados;
	}

	private int reconciliar(
			List<Documento> documentos,
			List<Documento> semId,
			Set<Long> idsUsados) {
		Iterator<Documento> iter = documentos.iterator();
		int indice = 0;

		while (iter.hasNext()) {
			Documento doc = iter.next();
			if (idsUsados.contains(doc.getId())) {
				continue;
			}
			if (indice < semId.size()) {
				atualizar(doc, semId.get(indice));
				indice++;
			} else {
				iter.remove();
				repositorioDocumento.delete(doc);
			}
		}
		return indice;
	}

	private void aplicarAtualizacao(Documento documento, Documento atualizacao) {
		documento.setTipo(atualizacao.getTipo());
		if (!NULO.verificar(atualizacao.getNumero())) {
			documento.setNumero(atualizacao.getNumero());
		}
	}

	private void persistirNovos(
			List<Documento> documentos,
			List<Documento> novos) {
		for (Documento novo : novos) {
			try {
				Documento salvo = repositorioDocumento.save(novo);
				documentos.add(salvo);
			} catch (Exception ex) {
				throw new IllegalArgumentException(NUMERO_EXISTENTE);
			}
		}
	}

	private void salvarNovo(Documento documento) {
		try {
			repositorioDocumento.save(documento);
		} catch (Exception ex) {
			throw new IllegalArgumentException(NUMERO_EXISTENTE);
		}
	}

}