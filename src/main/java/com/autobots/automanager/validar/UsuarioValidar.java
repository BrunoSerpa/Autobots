package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.repositorios.UsuarioRepositorio;

@Component
public class UsuarioValidar implements Validar<UsuarioDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final UsuarioRepositorio repositorio;
    private final DocumentoValidar validarDocumento;
    private final EnderecoValidar validarEndereco;
    private final TelefoneValidar validarTelefone;
    private final EmailValidar validarEmail;

    public UsuarioValidar(
            UsuarioRepositorio repositorio,
            DocumentoValidar validarDocumento,
            EnderecoValidar validarEndereco,
            TelefoneValidar validarTelefone,
            EmailValidar validarEmail) {
        this.repositorio = repositorio;
        this.validarDocumento = validarDocumento;
        this.validarEndereco = validarEndereco;
        this.validarTelefone = validarTelefone;
        this.validarEmail = validarEmail;
    }

    @Override
    public List<String> verificar(UsuarioDTO entity) {
        List<String> erros = new ArrayList<>();

        validarDocumentos(entity, erros);
        validarEnderecos(entity, erros);
        validarIdENome(entity, erros);
        validarPerfis(entity, erros);

        if (entity.getEmails() != null && !entity.getEmails().isEmpty()) {
            validarEmail.verificar(entity.getEmails())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getTelefones() != null && !entity.getTelefones().isEmpty()) {
            validarTelefone.verificar(entity.getTelefones())
                    .forEach(erro -> erros.add(" " + erro));
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<UsuarioDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (UsuarioDTO usuario : entities) {
            List<String> erroUsuario = verificar(usuario);
            if (!erroUsuario.isEmpty()) {
                erros.add("- " + index + "º Usuário:");
                erros.addAll(erroUsuario);
            }
            index++;
        }

        return erros;
    }
  
    private void validarDocumentos(UsuarioDTO entity, List<String> erros) {
        if (entity.getDocumentos() == null || entity.getDocumentos().isEmpty()) {
            erros.add("- Pelo menos um documento deve ser informado;");
            return;
        }
        validarDocumento.verificar(entity.getDocumentos())
                .forEach(erro -> erros.add(" " + erro));
    }

    private void validarEnderecos(UsuarioDTO entity, List<String> erros) {
        if (entity.getEndereco() == null) {
            return;
        }
        erros.add("- Endereço:");
        validarEndereco.verificar(entity.getEndereco())
                .forEach(erro -> erros.add(" " + erro));
    }

    private void validarIdENome(UsuarioDTO entity, List<String> erros) {
        if (entity.getId() != null && !repositorio.findById(entity.getId()).isPresent()) {
            erros.add("- Usuário não cadastrado;");
        } else if (NULO.verificar(entity.getNome())) {
            erros.add("- Nome não informado;");
        }
    }

    private void validarPerfis(UsuarioDTO entity, List<String> erros) {
        if (entity.getPerfis() == null || entity.getPerfis().isEmpty()) {
            erros.add("- Pelo menos um perfil deve ser informado;");
        }
    }
}
