package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.VendaDTO;
import com.autobots.automanager.repositorios.VendaRepositorio;

@Component
public class VendaValidar implements Validar<VendaDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final VendaRepositorio repositorio;
    private final UsuarioValidar validarUsuario;
    private final MercadoriaValidar validarMercadoria;
    private final ServicoValidar validarServico;
    private final VeiculoValidar validarVeiculo;

    public VendaValidar(
            VendaRepositorio repositorio,
            UsuarioValidar validarUsuario,
            MercadoriaValidar validarMercadoria,
            ServicoValidar validarServico,
            VeiculoValidar validarVeiculo) {
        this.repositorio = repositorio;
        this.validarUsuario = validarUsuario;
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

        if (entity.getCliente() == null) {
            erros.add("- Cliente não informado;");
        } else {
            mesclaErros(erros, validarUsuario.verificar(entity.getCliente()), "Cliente");
        }

        if (entity.getFuncionario() == null) {
            erros.add("- Funcionário não informado;");
        } else {
            mesclaErros(erros, validarUsuario.verificar(entity.getFuncionario()), "Funcionário");
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
            mesclaErros(erros, validarVeiculo.verificar(entity.getVeiculo()), "Veículo");
        }

        return erros;
    }

    private void mesclaErros(List<String> erros, List<String> novosErros, String tituloErro) {
        if (novosErros == null || novosErros.isEmpty()) {
            return;
        }
        erros.add("- " + tituloErro + ":");
        novosErros.forEach(e -> erros.add("  " + e));
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
