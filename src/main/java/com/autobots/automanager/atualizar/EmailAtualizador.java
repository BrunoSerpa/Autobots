package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Email;
import com.autobots.automanager.repositorios.EmailRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class EmailAtualizador extends Atualizar<Email> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final EmailRepositorio repositorio;

    public EmailAtualizador(EmailRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Email entidade, Email atualizacao) {
        if (!NULO.verificar(atualizacao.getEndereco())) {
            entidade.setEndereco(atualizacao.getEndereco());
        }
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Email> existentes, Set<Email> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Email existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Email criarNovo() {
        return new Email();
    }

    @Override
    protected Email deletarExistente(Email entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Email> extrairSemId(Set<Email> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Email> indexarPorId(Set<Email> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Email::getId, Function.identity()));
    }

    @Override
    protected Set<Email> reconciliar(Set<Email> entidades, Set<Email> novas, Set<Long> idsUsados) {
        Iterator<Email> iter = entidades.iterator();
        Iterator<Email> novosIter = novas.iterator();
        Set<Email> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Email atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Email novo = novosIter.next();
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
    protected void salvarNovo(Email entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
