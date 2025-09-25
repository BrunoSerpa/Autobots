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
    private final CredencialValidar validarCredencial;
    private final MercadoriaValidar validarMercadoria;
    private final VendaValidar validarVenda;
    private final VeiculoValidar validarVeiculo;

    public UsuarioValidar(
            UsuarioRepositorio repositorio,
            DocumentoValidar validarDocumento,
            EnderecoValidar validarEndereco,
            TelefoneValidar validarTelefone,
            EmailValidar validarEmail,
            CredencialValidar validarCredencial,
            MercadoriaValidar validarMercadoria,
            VendaValidar validarVenda,
            VeiculoValidar validarVeiculo) {
        this.repositorio = repositorio;
        this.validarDocumento = validarDocumento;
        this.validarEndereco = validarEndereco;
        this.validarTelefone = validarTelefone;
        this.validarEmail = validarEmail;
        this.validarCredencial = validarCredencial;
        this.validarMercadoria = validarMercadoria;
        this.validarVenda = validarVenda;
        this.validarVeiculo = validarVeiculo;
    }

    @Override
    public List<String> verificar(UsuarioDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null && !repositorio.findById(entity.getId()).isPresent()) {
            erros.add("- Usuário não cadastrado;");
        } else if (NULO.verificar(entity.getNome())) {
            erros.add("- Nome não informado;");
        }

        if (entity.getDocumentos() == null || entity.getDocumentos().isEmpty()) {
            erros.add("- Pelo menos um documento deve ser informado;");
        } else {
            validarDocumento.verificar(entity.getDocumentos())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getEndereco() != null) {
            List<String> errosEndereco = validarEndereco.verificar(entity.getEndereco());
            if (!errosEndereco.isEmpty()) {
                erros.add("- Endereço:");
                errosEndereco.forEach(erro -> erros.add(" " + erro));
            }
        }

        if (entity.getTelefones() != null && !entity.getTelefones().isEmpty()) {
            validarTelefone.verificar(entity.getTelefones())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getEmails() != null && !entity.getEmails().isEmpty()) {
            validarEmail.verificar(entity.getEmails())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getCredenciais() != null && !entity.getCredenciais().isEmpty()) {
            validarCredencial.verificar(entity.getCredenciais())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getMercadorias() != null && !entity.getMercadorias().isEmpty()) {
            validarMercadoria.verificar(entity.getMercadorias())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getVendas() != null && !entity.getVendas().isEmpty()) {
            validarVenda.verificar(entity.getVendas())
                    .forEach(erro -> erros.add(" " + erro));
        }

        if (entity.getVeiculos() != null && !entity.getVeiculos().isEmpty()) {
            validarVeiculo.verificar(entity.getVeiculos())
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
}
