package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Usuario;
import com.autobots.automanager.repositorios.UsuarioRepositorio;
import com.autobots.automanager.validar.StringVerificadorNulo;

@Component
public final class UsuarioAtualizador extends Atualizar<Usuario> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final CredencialAtualizador atualizadorCredencial;
    private final DocumentoAtualizador atualizadorDocumento;
    private final EmailAtualizador atualizadorEmail;
    private final EnderecoAtualizador atualizadorEndereco;
    private final TelefoneAtualizador atualizadorTelefone;
    private final VeiculoAtualizador atualizadorVeiculo;
    private final UsuarioRepositorio repositorio;

    public UsuarioAtualizador(CredencialAtualizador atualizadorCredencial,
            DocumentoAtualizador atualizadorDocumento,
            EmailAtualizador atualizadorEmail,
            EnderecoAtualizador atualizadorEndereco,
            TelefoneAtualizador atualizadorTelefone,
            VeiculoAtualizador atualizadorVeiculo,
            UsuarioRepositorio repositorio) {
        this.atualizadorCredencial = atualizadorCredencial;
        this.atualizadorDocumento = atualizadorDocumento;
        this.atualizadorEmail = atualizadorEmail;
        this.atualizadorEndereco = atualizadorEndereco;
        this.atualizadorTelefone = atualizadorTelefone;
        this.atualizadorVeiculo = atualizadorVeiculo;
        this.repositorio = repositorio;
    }

    @Override
    protected void aplicarAtualizacao(Usuario entidade, Usuario atualizacao) {
        if (!NULO.verificar(atualizacao.getNome())) {
            entidade.setNome(atualizacao.getNome());
        }

        entidade.setNomeSocial(atualizacao.getNomeSocial());
        entidade.setPerfis(atualizacao.getPerfis());

        entidade.setEndereco(atualizadorEndereco.atualizar(entidade.getEndereco(), atualizacao.getEndereco()));

        atualizadorCredencial.atualizar(entidade.getCredenciais(), atualizacao.getCredenciais());
        atualizadorDocumento.atualizar(entidade.getDocumentos(), atualizacao.getDocumentos());
        atualizadorEmail.atualizar(entidade.getEmails(), atualizacao.getEmails());
        atualizadorTelefone.atualizar(entidade.getTelefones(), atualizacao.getTelefones());
        atualizadorVeiculo.atualizar(entidade.getVeiculos(), atualizacao.getVeiculos());
    }

    @Override
    protected Set<Long> atualizacaoExistente(Map<Long, Usuario> existentes, Set<Usuario> atualizacoes) {
        Set<Long> usados = new HashSet<>();
        atualizacoes.stream()
                .filter(atual -> atual.getId() != null)
                .forEach(atual -> {
                    Usuario existente = existentes.get(atual.getId());
                    if (existente != null) {
                        atualizar(existente, atual);
                        usados.add(existente.getId());
                    }
                });
        return usados;
    }

    @Override
    protected Usuario criarNovo() {
        return new Usuario();
    }

    @Override
    protected Usuario deletarExistente(Usuario entidade) {
        if (entidade != null) {
            repositorio.delete(entidade);
        }
        return null;
    }

    @Override
    protected Set<Usuario> extrairSemId(Set<Usuario> atualizacoes) {
        return atualizacoes.stream()
                .filter(atual -> atual.getId() == null)
                .collect(Collectors.toSet());
    }

    @Override
    protected Map<Long, Usuario> indexarPorId(Set<Usuario> entidades) {
        return entidades.stream()
                .filter(atual -> atual.getId() != null)
                .collect(Collectors.toMap(Usuario::getId, Function.identity()));
    }

    @Override
    protected Set<Usuario> reconciliar(Set<Usuario> entidades, Set<Usuario> novas, Set<Long> idsUsados) {
        Iterator<Usuario> iter = entidades.iterator();
        Iterator<Usuario> novosIter = novas.iterator();
        Set<Usuario> naoConsumidos = new HashSet<>();

        while (iter.hasNext()) {
            Usuario atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Usuario novo = novosIter.next();
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
    protected void salvarNovo(Usuario entidade) {
        try {
            repositorio.save(entidade);
        } catch (Exception erro) {
            throw new IllegalArgumentException("Já cadastrado", erro);
        }
    }
}
