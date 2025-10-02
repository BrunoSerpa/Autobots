package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.repositorios.DocumentoRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class DocumentoAtualizador extends Atualizar<Documento> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final DocumentoRepositorio repositorio;

    public DocumentoAtualizador(DocumentoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Documento entidade, Documento atualizacao) {
        if (atualizacao.getDataEmissao() != null) {
            entidade.setDataEmissao(atualizacao.getDataEmissao());
        }
        if (atualizacao.getTipo() != null) {
            entidade.setTipo(atualizacao.getTipo());
        }
        if (!NULO.verificar(atualizacao.getNumero())) {
            entidade.setNumero(atualizacao.getNumero());
        }
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Documento> existentes, Set<Documento> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Documento existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Documento criarNovo() {
        return new Documento();
    }

    @Override
    protected Documento deletarExistente(Documento entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Documento> extrairSemId(Set<Documento> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Documento> indexarPorId(Set<Documento> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Documento::getId, Function.identity()));
    }

    @Override
    protected Set<Documento> reconciliar(Set<Documento> entidades, Set<Documento> novas, Set<Long> idsUsados) {
        Iterator<Documento> iter = entidades.iterator();
        Iterator<Documento> novosIter = novas.iterator();
        Set<Documento> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Documento atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Documento novo = novosIter.next();
                atualizar(atual, novo);
            } else {
                iter.remove();
                repositorio.delete(atual);
            }
        }

        while (novosIter.hasNext()) {
            naoConsumidos.add(novosIter.next());
        }

        return naoConsumidos;
    }

    @Override
    protected void salvarNovo(Documento entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
