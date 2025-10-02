package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Veiculo;
import com.autobots.automanager.repositorios.VeiculoRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class VeiculoAtualizador extends Atualizar<Veiculo> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final ObjectProvider<UsuarioAtualizador> provedorAtualizadorUsuario;
    private final ObjectProvider<VendaAtualizador> provedorAtualizadorVenda;
    private final VeiculoRepositorio repositorio;

    public VeiculoAtualizador(ObjectProvider<UsuarioAtualizador> provedorAtualizadorUsuario,
            ObjectProvider<VendaAtualizador> provedorAtualizadorVenda,
            VeiculoRepositorio repositorio) {
        this.provedorAtualizadorUsuario = provedorAtualizadorUsuario;
        this.provedorAtualizadorVenda = provedorAtualizadorVenda;
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Veiculo entidade, Veiculo atualizacao) {
        if (atualizacao.getTipo() != null) {
            entidade.setTipo(atualizacao.getTipo());
        }

        if (!NULO.verificar(atualizacao.getModelo())) {
            entidade.setModelo(atualizacao.getModelo());
        }
        if (!NULO.verificar(atualizacao.getPlaca())) {
            entidade.setPlaca(atualizacao.getPlaca());
        }

        UsuarioAtualizador atualizadorUsuario = provedorAtualizadorUsuario.getIfAvailable();
        if (atualizadorUsuario != null) {
            entidade.setProprietario(
                    atualizadorUsuario.atualizar(entidade.getProprietario(), atualizacao.getProprietario()));
        }

        VendaAtualizador atualizadorVenda = provedorAtualizadorVenda.getIfAvailable();
        if (atualizadorVenda != null) {
            atualizadorVenda.atualizar(entidade.getVendas(), atualizacao.getVendas());
        }
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Veiculo> existentes, Set<Veiculo> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Veiculo existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Veiculo criarNovo() {
        return new Veiculo();
    }

    @Override
    protected Veiculo deletarExistente(Veiculo entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Veiculo> extrairSemId(Set<Veiculo> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Veiculo> indexarPorId(Set<Veiculo> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Veiculo::getId, Function.identity()));
    }

    @Override
    protected Set<Veiculo> reconciliar(Set<Veiculo> entidades, Set<Veiculo> novas, Set<Long> idsUsados) {
        Iterator<Veiculo> iter = entidades.iterator();
        Iterator<Veiculo> novosIter = novas.iterator();
        Set<Veiculo> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Veiculo atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Veiculo novo = novosIter.next();
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
    protected void salvarNovo(Veiculo entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
