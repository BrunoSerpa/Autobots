package com.autobots.automanager.modelo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.DocumentoRepositorio;

@Component
public class DocumentoAtualizador {
	private static final String NUMERO_EXISTENTE = "Número de documento já cadastrado";
	private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

	private DocumentoRepositorio repositorioDocumento;

	public DocumentoAtualizador(DocumentoRepositorio repositorioDocumento) {
		this.repositorioDocumento = repositorioDocumento;
	}

	public Documento atualizar(Documento documento, Documento atualizacao) {
		if (atualizacao == null) {
			if (documento != null) {
				repositorioDocumento.delete(documento);
			}
			return null;
		}

		boolean novo = (documento == null);
		if (novo) {
			documento = new Documento();
		}

		Map<Supplier<String>, Consumer<String>> campos = Map.of(
				atualizacao::getTipo, documento::setTipo,
				atualizacao::getNumero, documento::setNumero);

		campos.forEach((getter, setter) -> {
			String valor = getter.get();
			if (!NULO.verificar(valor)) {
				setter.accept(valor);
			}
		});

		if (novo) {
			try {
				repositorioDocumento.save(documento);
			} catch (Exception e) {
				throw new IllegalArgumentException(NUMERO_EXISTENTE);
			}
		}

		return documento;
	}

	public void atualizar(List<Documento> documentos, List<Documento> atualizacoes) {
		List<Documento> semId = atualizacoes.stream()
				.filter(documento -> documento.getId() == null)
				.toList();

		List<Long> usados = new ArrayList<>();
		for (Documento atualizacao : atualizacoes.stream()
				.filter(documento -> documento.getId() != null)
				.toList()) {
			for (Documento documento : documentos) {
				if (Objects.equals(documento.getId(), atualizacao.getId())) {
					atualizar(documento, atualizacao);
					usados.add(documento.getId());
					break;
				}
			}
		}

		Iterator<Documento> iterador = documentos.iterator();
		int posicao = 0;
		while (iterador.hasNext()) {
			Documento documento = iterador.next();
			if (usados.contains(documento.getId())) {
				continue;
			}
			if (posicao >= semId.size()) {
				iterador.remove();
				repositorioDocumento.delete(documento);
			} else {
				atualizar(documento, semId.get(posicao));
				posicao++;
			}
		}

		for (; posicao < semId.size(); posicao++) {
			try {
				Documento novo = repositorioDocumento.save(semId.get(posicao));
				documentos.add(novo);
				usados.add(novo.getId());
			} catch (Exception e) {
				throw new IllegalArgumentException(NUMERO_EXISTENTE);
			}
		}
	}
}
