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
    private final CredencialValidar validarCredencial;
    private final DocumentoValidar validarDocumento;
    private final EmailValidar validarEmail;
    private final EnderecoValidar validarEndereco;
    private final TelefoneValidar validarTelefone;

    public UsuarioValidar(
            UsuarioRepositorio repositorio,
            CredencialValidar validarCredencial,
            DocumentoValidar validarDocumento,
            EnderecoValidar validarEndereco,
            TelefoneValidar validarTelefone,
            EmailValidar validarEmail) {
        this.repositorio = repositorio;
        this.validarCredencial = validarCredencial;
        this.validarDocumento = validarDocumento;
        this.validarEndereco = validarEndereco;
        this.validarTelefone = validarTelefone;
        this.validarEmail = validarEmail;
    }

    @Override
    public List<String> verificar(UsuarioDTO entity) {
        List<String> erros = new ArrayList<>();

        validarAssociacoesProibidas(entity, erros);
        validarCredenciais(entity, erros);
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

    private void validarAssociacoesProibidas(UsuarioDTO entity, List<String> erros) {
        List<String> associacoes = new ArrayList<>();

        if (entity.getMercadorias() != null && !entity.getMercadorias().isEmpty()) {
            associacoes.add("mercadorias");
        }
        if (entity.getVendas() != null && !entity.getVendas().isEmpty()) {
            associacoes.add("vendas");
        }
        if (entity.getVeiculos() != null && !entity.getVeiculos().isEmpty()) {
            associacoes.add("veículos");
        }

        String associados;
        if (associacoes.isEmpty()) {
            return;
        } else if (associacoes.size() == 1) {
            associados = associacoes.get(0);
        } else if (associacoes.size() == 2) {
            associados = associacoes.get(0) + " e " + associacoes.get(1);
        } else {
            associados = String.join(", ", associacoes.subList(0, associacoes.size() - 1))
                    + " e " + associacoes.get(associacoes.size() - 1);
        }

        erros.add("- Não é permitido cadastrar "+associados+" junto com o usuário.");
    }

    private void validarCredenciais(UsuarioDTO entity, List<String> erros) {
        if (entity.getCredenciais() == null || entity.getCredenciais().isEmpty()) {
            erros.add("- Pelo menos uma credencial deve ser informada");
            return;
        }
        validarCredencial.verificar(entity.getCredenciais())
                .forEach(erro -> erros.add(" " + erro));
    }

    private void validarDocumentos(UsuarioDTO entity, List<String> erros) {
        if (entity.getDocumentos() == null || entity.getDocumentos().isEmpty()) {
            erros.add("- Pelo menos um documento deve ser informado");
            return;
        }
        validarDocumento.verificar(entity.getDocumentos())
            .forEach(erro -> erros.add(" " + erro));
    }

    private void validarEnderecos(UsuarioDTO entity, List<String> erros) {
        if (entity.getEndereco() == null) {
            return;
        }
        List<String> errosEndereco = validarEndereco.verificar(entity.getEndereco());
        if (errosEndereco.isEmpty()){
            return;
        }
        erros.add("- Endereço:");
        errosEndereco.forEach(erro -> erros.add(" " + erro));
    }

    private void validarIdENome(UsuarioDTO entity, List<String> erros) {
        if (entity.getId() != null && !repositorio.findById(entity.getId()).isPresent()) {
            erros.add("- Usuário não cadastrado");
        } else if (NULO.verificar(entity.getNome())) {
            erros.add("- Nome não informado");
        }
    }

    private void validarPerfis(UsuarioDTO entity, List<String> erros) {
        if (entity.getPerfis() == null || entity.getPerfis().isEmpty()) {
            erros.add("- Pelo menos um perfil deve ser informado");
        }
    }
}
