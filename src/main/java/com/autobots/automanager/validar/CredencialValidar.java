package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.CredencialDTO;
import com.autobots.automanager.repositorios.CredencialCodigoBarraRepositorio;
import com.autobots.automanager.repositorios.CredencialUsuarioSenhaRepositorio;

@Component
public class CredencialValidar implements Validar<CredencialDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final CredencialCodigoBarraRepositorio repositorioCodigoBarra;
    private final CredencialUsuarioSenhaRepositorio repositorioUsuarioSenha;

    public CredencialValidar(
            CredencialCodigoBarraRepositorio repositorioCodigoBarra,
            CredencialUsuarioSenhaRepositorio repositorioUsuarioSenha) {
        this.repositorioCodigoBarra = repositorioCodigoBarra;
        this.repositorioUsuarioSenha = repositorioUsuarioSenha;
    }

    @Override
    public List<String> verificar(CredencialDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            boolean existe = repositorioCodigoBarra.findById(entity.getId()).isPresent()
                    || repositorioUsuarioSenha.findById(entity.getId()).isPresent();
            if (!existe) {
                erros.add("- Credencial não cadastrada;");
            }
            return erros;
        }

        long codigo = entity.getCodigo();
        String nomeUsuario = entity.getNomeUsuario();
        String senha = entity.getSenha();

        if (!NULO.verificar(nomeUsuario) || !NULO.verificar(senha)) {
            validarUsuarioSenha(nomeUsuario, senha, erros);
        } else if (codigo != 0) {
            validarCodigoBarra(codigo, erros);
        } else {
            erros.add("- Tipo de credencial não reconhecido;");
        }

        return erros;
    }

    private void validarUsuarioSenha(String nomeUsuario, String senha, List<String> erros) {
        if (NULO.verificar(nomeUsuario)) {
            erros.add("- Nome de usuário não informado;");
        } else if (repositorioUsuarioSenha.findByNomeUsuario(nomeUsuario).isPresent()) {
            erros.add("- Nome de usuário já cadastrado;");
        }

        if (NULO.verificar(senha)) {
            erros.add("- Senha não informada;");
        } else if (senha.length() < 6) {
            erros.add("- Senha deve ter pelo menos 6 caracteres;");
        }
    }

    private void validarCodigoBarra(long codigo, List<String> erros) {
        if (codigo <= 0) {
            erros.add("- Código de barras inválido;");
        } else if (repositorioCodigoBarra.findByCodigo(codigo).isPresent()) {
            erros.add("- Código de barras já cadastrado;");
        }
    }

    @Override
    public List<String> verificar(Set<CredencialDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (CredencialDTO credencial : entities) {
            List<String> erroCredencial = verificar(credencial);
            if (!erroCredencial.isEmpty()) {
                erros.add("- " + index + "ª Credencial:");
                erros.addAll(erroCredencial);
            }
            index++;
        }

        return erros;
    }
}
