package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Mercadoria;
import com.autobots.automanager.repositorios.MercadoriaRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class MercadoriaAtualizador extends Atualizar<Mercadoria> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final MercadoriaRepositorio repositorio;

    public MercadoriaAtualizador(MercadoriaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Mercadoria entidade, Mercadoria atualizacao) {
        if (atualizacao.getValidade() != null) {
            entidade.setValidade(atualizacao.getValidade());
        }
        if (atualizacao.getFabricao() != null) {
            entidade.setFabricao(atualizacao.getFabricao());
        }
        if (!NULO.verificar(atualizacao.getNome())) {
            entidade.setNome(atualizacao.getNome());
        }

        Optional.ofNullable(atualizacao.getQuantidade())
                .ifPresent(entidade::setQuantidade);
        Optional.ofNullable(atualizacao.getValor())
                .ifPresent(entidade::setValor);

        entidade.setDescricao(atualizacao.getDescricao());
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Mercadoria> existentes, Set<Mercadoria> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Mercadoria existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Mercadoria criarNovo() {
        return new Mercadoria();
    }

    @Override
    protected Mercadoria deletarExistente(Mercadoria entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Mercadoria> extrairSemId(Set<Mercadoria> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Mercadoria> indexarPorId(Set<Mercadoria> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Mercadoria::getId, Function.identity()));
    }

    @Override
    protected Set<Mercadoria> reconciliar(Set<Mercadoria> entidades, Set<Mercadoria> novas, Set<Long> idsUsados) {
        Iterator<Mercadoria> iter = entidades.iterator();
        Iterator<Mercadoria> novosIter = novas.iterator();
        Set<Mercadoria> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Mercadoria atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Mercadoria novo = novosIter.next();
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
    protected void salvarNovo(Mercadoria entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
