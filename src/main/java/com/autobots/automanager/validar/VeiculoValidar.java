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
    private final UsuarioValidar validarUsuario;
    private final ObjectProvider<VendaValidar> provedorValidarVenda;

    public VeiculoValidar(
            VeiculoRepositorio repositorio,
            UsuarioValidar validarUsuario,
            ObjectProvider<VendaValidar> provedorValidarVenda) {
        this.repositorio = repositorio;
        this.validarUsuario = validarUsuario;
        this.provedorValidarVenda = provedorValidarVenda;
    }

    @Override
    public List<String> verificar(VeiculoDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Veículo não cadastrado");
            }
            return erros;
        }

        if (entity.getTipo() == null) {
            erros.add("- Tipo de veículo não informado");
        }

        if (NULO.verificar(entity.getModelo())) {
            erros.add("- Modelo do veículo não informado");
        }

        if (NULO.verificar(entity.getPlaca())) {
            erros.add("- Placa do veículo não informada");
        }

        if (entity.getProprietario() != null) {
            mesclaErros(erros, validarUsuario.verificar(entity.getProprietario()), "Proprietário");
        }

        VendaValidar validarVenda = provedorValidarVenda.getIfAvailable();
        if (validarVenda == null) {
            return erros;
        }

        if (entity.getVendas() != null && !entity.getVendas().isEmpty()) {
            validarVenda.verificar(entity.getVendas())
                    .forEach(erro -> erros.add("  " + erro));
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