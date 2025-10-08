package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract sealed class Atualizar<E> permits
        DocumentoAtualizador,
        EmailAtualizador,
        EnderecoAtualizador,
        MercadoriaAtualizador,
        ServicoAtualizador,
        TelefoneAtualizador,
        UsuarioAtualizador,
        VeiculoAtualizador,
        VendaAtualizador {
    public final E atualizar(E entidade, E atualizacao) {
        if (atualizacao == null) {
            return deletarExistente(entidade);
        }

        boolean novo = (entidade == null);
        if (novo) {
            entidade = criarNovo();
        }

        aplicarAtualizacao(entidade, atualizacao);

        if (novo) {
            salvarNovo(entidade);
        }

        return entidade;
    }

    public final void atualizar(Set<E> entidades, Set<E> atualizacoes) {
        if (entidades == null) {
            entidades = new HashSet<>();
        }
        if (atualizacoes == null) {
            atualizacoes = new HashSet<>();
        }

        Set<E> semId = extrairSemId(atualizacoes);
        Map<Long, E> porId = indexarPorId(entidades);
        Set<Long> idsUsados = atualizacaoExistente(porId, atualizacoes);
        Set<E> naoConsumidos = reconciliar(entidades, semId, idsUsados);

        salvarNovo(entidades, naoConsumidos);
    }

    private void salvarNovo(Set<E> entidades, Set<E> novas) {
        for (E nova : novas) {
            salvarNovo(nova);
            entidades.add(nova);
        }
    }

    protected abstract void aplicarAtualizacao(E entidade, E atualizacao);

    protected abstract Set<Long> atualizacaoExistente(Map<Long, E> existentes, Set<E> atualizacoes);

    protected abstract E criarNovo();

    protected abstract E deletarExistente(E entidade);

    protected abstract Set<E> extrairSemId(Set<E> atualizacoes);

    protected abstract Map<Long, E> indexarPorId(Set<E> entidades);

    protected abstract Set<E> reconciliar(Set<E> entidades, Set<E> novas, Set<Long> idsUsados);

    protected abstract void salvarNovo(E entidade);
}