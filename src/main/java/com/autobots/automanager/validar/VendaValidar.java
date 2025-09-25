package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.VendaDTO;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Component
public class VendaValidar implements Validar<VendaDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final VendaRepositorio repositorio;
    private final ObjectProvider<UsuarioValidar> provedorValidarUsuario;
    private final MercadoriaValidar validarMercadoria;
    private final ServicoValidar validarServico;
    private final VeiculoValidar validarVeiculo;

    public VendaValidar(
            VendaRepositorio repositorio,
            ObjectProvider<UsuarioValidar> provedorValidarUsuario,
            MercadoriaValidar validarMercadoria,
            ServicoValidar validarServico,
            VeiculoValidar validarVeiculo) {
        this.repositorio = repositorio;
        this.provedorValidarUsuario = provedorValidarUsuario;
        this.validarMercadoria = validarMercadoria;
        this.validarServico = validarServico;
        this.validarVeiculo = validarVeiculo;
    }

    @Override
    public List<String> verificar(VendaDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Venda não cadastrada;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getIdentificacao())) {
            erros.add("- Identificação da venda não informada;");
        }

        UsuarioValidar usuarioValidador = provedorValidarUsuario.getIfAvailable();
        if (entity.getCliente() == null) {
            erros.add("- Cliente não informado;");
        } else if (usuarioValidador != null) {
            List<String> errosCli = usuarioValidador.verificar(entity.getCliente());
            if (!errosCli.isEmpty()) {
                erros.add("- Cliente:");
                errosCli.forEach(e -> erros.add("  " + e));
            }
        }

        if (entity.getFuncionario() == null) {
            erros.add("- Funcionário não informado;");
        } else if (usuarioValidador != null) {
            List<String> errosFun = usuarioValidador.verificar(entity.getFuncionario());
            if (!errosFun.isEmpty()) {
                erros.add("- Funcionário:");
                errosFun.forEach(e -> erros.add("  " + e));
            }
        }

        if (entity.getMercadorias() != null && !entity.getMercadorias().isEmpty()) {
            validarMercadoria.verificar(entity.getMercadorias())
                    .forEach(e -> erros.add("  " + e));
        }

        if (entity.getServicos() != null && !entity.getServicos().isEmpty()) {
            validarServico.verificar(entity.getServicos())
                    .forEach(e -> erros.add("  " + e));
        }

        if (entity.getVeiculo() != null) {
            List<String> errosVeic = validarVeiculo.verificar(entity.getVeiculo());
            if (!errosVeic.isEmpty()) {
                erros.add("- Veículo:");
                errosVeic.forEach(e -> erros.add("  " + e));
            }
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<VendaDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (VendaDTO venda : entities) {
            List<String> erroVenda = verificar(venda);
            if (!erroVenda.isEmpty()) {
                erros.add("- " + index + "ª Venda:");
                erros.addAll(erroVenda);
            }
            index++;
        }

        return erros;
    }
}
