package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Venda;
import com.autobots.automanager.repositorios.VendaRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class VendaAtualizador extends Atualizar<Venda> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final MercadoriaAtualizador atualizadorMercadoria;
    private final ServicoAtualizador atualizadorServico;
    private final UsuarioAtualizador atualizadorUsuario;
    private final VeiculoAtualizador atualizadorVeiculo;
    private final VendaRepositorio repositorio;

    public VendaAtualizador(MercadoriaAtualizador atualizadorMercadoria,
            ServicoAtualizador atualizadorServico,
            UsuarioAtualizador atualizadorUsuario,
            VeiculoAtualizador atualizadorVeiculo,
            VendaRepositorio repositorio) {
        this.atualizadorMercadoria = atualizadorMercadoria;
        this.atualizadorServico = atualizadorServico;
        this.atualizadorUsuario = atualizadorUsuario;
        this.atualizadorVeiculo = atualizadorVeiculo;
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Venda entidade, Venda atualizacao) {
        if (!NULO.verificar(atualizacao.getIdentificacao())) {
            entidade.setIdentificacao(atualizacao.getIdentificacao());
        }

        entidade.setCliente(atualizadorUsuario.atualizar(entidade.getCliente(), atualizacao.getCliente()));
        entidade.setFuncionario(atualizadorUsuario.atualizar(entidade.getFuncionario(), atualizacao.getFuncionario()));

        atualizadorMercadoria.atualizar(entidade.getMercadorias(), atualizacao.getMercadorias());
        atualizadorServico.atualizar(entidade.getServicos(), atualizacao.getServicos());

        entidade.setVeiculo(atualizadorVeiculo.atualizar(entidade.getVeiculo(), atualizacao.getVeiculo()));

    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Venda> existentes, Set<Venda> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Venda existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Venda criarNovo() {
        return new Venda();
    }

    @Override
    protected Venda deletarExistente(Venda entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Venda> extrairSemId(Set<Venda> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Venda> indexarPorId(Set<Venda> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Venda::getId, Function.identity()));
    }

    @Override
    protected Set<Venda> reconciliar(Set<Venda> entidades, Set<Venda> novas, Set<Long> idsUsados) {
        Iterator<Venda> iter = entidades.iterator();
        Iterator<Venda> novosIter = novas.iterator();
        Set<Venda> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Venda atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Venda novo = novosIter.next();
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
    protected void salvarNovo(Venda entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
