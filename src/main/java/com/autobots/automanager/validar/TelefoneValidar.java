package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Component
public class TelefoneValidar implements Validar<TelefoneDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private TelefoneRepositorio repositorio;

    public TelefoneValidar(TelefoneRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(TelefoneDTO entity) {
        List<String> erros = new ArrayList<>();
        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent())
                erros.add("- Telefone não cadastrado;");
            return erros;
        }
        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Sem Numero;");
        }
        return erros;
    }

    @Override
    public List<String> verificar(List<TelefoneDTO> entities) {
        List<String> erros = new ArrayList<>();

        for (int index = 0; entities.size() > index; index++) {
            List<String> erroTelefone = verificar(entities.get(index));
            if (!erroTelefone.isEmpty()) {
                erros.add("- " + (index + 1) + "º Telefone:");
                erros.addAll(erroTelefone);
            }
        }

        return erros;
    }
}
