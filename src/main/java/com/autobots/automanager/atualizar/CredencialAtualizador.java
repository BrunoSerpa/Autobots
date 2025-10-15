package com.autobots.automanager.atualizar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.repositorios.CredencialCodigoBarraRepositorio;
import com.autobots.automanager.repositorios.CredencialUsuarioSenhaRepositorio;

@Component
public class CredencialAtualizador {
    private final CredencialCodigoBarraRepositorio repositorioCodigoBarra;
    private final CredencialUsuarioSenhaRepositorio repositorioUsuarioSenha;

    public CredencialAtualizador(
            CredencialCodigoBarraRepositorio repositorioCodigoBarra,
            CredencialUsuarioSenhaRepositorio repositorioUsuarioSenha) {
        this.repositorioCodigoBarra = repositorioCodigoBarra;
        this.repositorioUsuarioSenha = repositorioUsuarioSenha;
    }

    public void atualizar(Credencial existente, Credencial atualizacao) {
        if (existente == null || atualizacao == null)
            return;

        existente.setInativo(atualizacao.isInativo());
        existente.setUltimoAcesso(atualizacao.getUltimoAcesso());

        if (existente instanceof CredencialUsuarioSenha e && atualizacao instanceof CredencialUsuarioSenha a) {
            if (a.getNomeUsuario() != null && !a.getNomeUsuario().isBlank()) {
                e.setNomeUsuario(a.getNomeUsuario());
            }
            if (a.getSenha() != null && !a.getSenha().isBlank()) {
                e.setSenha(a.getSenha());
            }
        }

        if (existente instanceof CredencialCodigoBarra e && atualizacao instanceof CredencialCodigoBarra a) {
            if (a.getCodigo() > 0) {
                e.setCodigo(a.getCodigo());
            }
        }
    }

    public void atualizar(Set<Credencial> existentes, Set<Credencial> atualizacoes) {
        if (existentes == null) {
            existentes = new HashSet<>();
        }
        if (atualizacoes == null) {
            atualizacoes = new HashSet<>();
        }

        Map<Long, Credencial> porId = existentes.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(Credencial::getId, Function.identity()));

        Set<Long> idsUsados = new HashSet<>();
        for (Credencial atual : atualizacoes) {
            if (atual.getId() != null && porId.containsKey(atual.getId())) {
                atualizar(porId.get(atual.getId()), atual);
                idsUsados.add(atual.getId());
            }
        }

        Iterator<Credencial> iter = existentes.iterator();
        Iterator<Credencial> novosIter = atualizacoes.stream()
                .filter(c -> c.getId() == null)
                .iterator();

        while (iter.hasNext()) {
            Credencial atual = iter.next();
            if (idsUsados.contains(atual.getId())) {
                continue;
            }
            if (novosIter.hasNext()) {
                Credencial novo = novosIter.next();
                salvarNovo(novo);
                atualizar(atual, novo);
            } else {
                iter.remove();
                deletar(atual);
            }
        }

        while (novosIter.hasNext()) {
            Credencial novo = novosIter.next();
            salvarNovo(novo);
            existentes.add(novo);
        }
    }

    public Credencial salvarNovo(Credencial nova) {
        if (nova instanceof CredencialUsuarioSenha e) {
            return repositorioUsuarioSenha.save(e);
        } else if (nova instanceof CredencialCodigoBarra e) {
            return repositorioCodigoBarra.save(e);
        }
        throw new IllegalArgumentException("Tipo de credencial não suportado");
    }

    public void deletar(Credencial existente) {
        if (existente == null)
            return;

        if (existente instanceof CredencialUsuarioSenha e) {
            repositorioUsuarioSenha.delete(e);
        } else if (existente instanceof CredencialCodigoBarra e) {
            repositorioCodigoBarra.delete(e);
        }
    }
}
