package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.VeiculoDTO;
import com.autobots.automanager.repositorios.VeiculoRepositorio;

@Component
public class VeiculoValidar implements Validar<VeiculoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final VeiculoRepositorio repositorio;
    private final ObjectProvider<UsuarioValidar> provedorValidarUsuario;
    private final ObjectProvider<VendaValidar> provedorValidarVenda;

    public VeiculoValidar(
            VeiculoRepositorio repositorio,
            ObjectProvider<UsuarioValidar> provedorValidarUsuario,
            ObjectProvider<VendaValidar> provedorValidarVenda) {
        this.repositorio = repositorio;
        this.provedorValidarUsuario = provedorValidarUsuario;
        this.provedorValidarVenda = provedorValidarVenda;
    }

    @Override
    public List<String> verificar(VeiculoDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Veículo não cadastrado;");
            }
            return erros;
        }

        if (entity.getTipo() == null) {
            erros.add("- Tipo de veículo não informado;");
        }

        if (NULO.verificar(entity.getModelo())) {
            erros.add("- Modelo do veículo não informado;");
        }

        if (NULO.verificar(entity.getPlaca())) {
            erros.add("- Placa do veículo não informada;");
        }

        if (entity.getProprietario() != null) {
            UsuarioValidar validarUsuario = provedorValidarUsuario.getIfAvailable();
            if (validarUsuario != null) {
                List<String> errosProprietario = validarUsuario.verificar(entity.getProprietario());
                if (!errosProprietario.isEmpty()) {
                    erros.add("- Proprietário:");
                    errosProprietario.forEach(e -> erros.add("  " + e));
                }
            }
        }

        if (entity.getVendas() != null && !entity.getVendas().isEmpty()) {
            VendaValidar validarVenda = provedorValidarVenda.getIfAvailable();
            if (validarVenda != null) {
                validarVenda.verificar(entity.getVendas())
                        .forEach(erro -> erros.add("  " + erro));
            }
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<VeiculoDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (VeiculoDTO veiculo : entities) {
            List<String> erroVeiculo = verificar(veiculo);
            if (!erroVeiculo.isEmpty()) {
                erros.add("- " + index + "º Veículo:");
                erros.addAll(erroVeiculo);
            }
            index++;
        }

        return erros;
    }
}