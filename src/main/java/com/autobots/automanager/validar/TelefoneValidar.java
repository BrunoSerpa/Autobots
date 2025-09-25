package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Component
public class TelefoneValidar implements Validar<TelefoneDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private final TelefoneRepositorio repositorio;

    public TelefoneValidar(TelefoneRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(TelefoneDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Telefone não cadastrado;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getDdd())) {
            erros.add("- DDD não informado;");
        }

        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Número de telefone não informado;");
        }

        return erros;
    }

    @Override
    public List<String> verificar(Set<TelefoneDTO> entities) {
        List<String> erros = new ArrayList<>();
        int index = 1;

        for (TelefoneDTO telefone : entities) {
            List<String> erroTelefone = verificar(telefone);
            if (!erroTelefone.isEmpty()) {
                erros.add("- " + index + "º Telefone:");
                erros.addAll(erroTelefone);
            }
            index++;
        }

        return erros;
    }
}
