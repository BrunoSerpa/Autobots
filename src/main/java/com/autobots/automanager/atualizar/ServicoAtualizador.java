package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Servico;
import com.autobots.automanager.repositorios.ServicoRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class ServicoAtualizador extends Atualizar<Servico> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final ServicoRepositorio repositorio;

    public ServicoAtualizador(ServicoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Servico entidade, Servico atualizacao) {
        if (!NULO.verificar(atualizacao.getNome())) {
            entidade.setNome(atualizacao.getNome());
        }

        Optional.ofNullable(atualizacao.getValor())
            .ifPresent(entidade::setValor);

        entidade.setDescricao(atualizacao.getDescricao());
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Servico> existentes, Set<Servico> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Servico existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Servico criarNovo() {
        return new Servico();
    }

    @Override
    protected Servico deletarExistente(Servico entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Servico> extrairSemId(Set<Servico> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Servico> indexarPorId(Set<Servico> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Servico::getId, Function.identity()));
    }

    @Override
    protected Set<Servico> reconciliar(Set<Servico> entidades, Set<Servico> novas, Set<Long> idsUsados) {
        Iterator<Servico> iter = entidades.iterator();
        Iterator<Servico> novosIter = novas.iterator();
        Set<Servico> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Servico atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Servico novo = novosIter.next();
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
    protected void salvarNovo(Servico entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
