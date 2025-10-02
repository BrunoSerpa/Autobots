package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.repositorios.EnderecoRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class EnderecoAtualizador extends Atualizar<Endereco> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final EnderecoRepositorio repositorio;

    public EnderecoAtualizador(EnderecoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Endereco entidade, Endereco atualizacao) {
        Map<Supplier<String>, Consumer<String>> campos = Map.of(
                atualizacao::getEstado, entidade::setEstado,
                atualizacao::getCidade, entidade::setCidade,
                atualizacao::getBairro, entidade::setBairro,
                atualizacao::getRua, entidade::setRua,
                atualizacao::getNumero, entidade::setNumero,
                atualizacao::getCodigoPostal, entidade::setCodigoPostal);

        campos.forEach((getter, setter) -> {
            String valor = getter.get();
            if (!NULO.verificar(valor)) {
                setter.accept(valor);
            }
        });
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Endereco> existentes, Set<Endereco> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Endereco existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Endereco criarNovo() {
        return new Endereco();
    }

    @Override
    protected Endereco deletarExistente(Endereco entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Endereco> extrairSemId(Set<Endereco> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Endereco> indexarPorId(Set<Endereco> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Endereco::getId, Function.identity()));
    }

    @Override
    protected Set<Endereco> reconciliar(Set<Endereco> entidades, Set<Endereco> novas, Set<Long> idsUsados) {
        Iterator<Endereco> iter = entidades.iterator();
        Iterator<Endereco> novosIter = novas.iterator();
        Set<Endereco> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Endereco atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Endereco novo = novosIter.next();
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
    protected void salvarNovo(Endereco entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
