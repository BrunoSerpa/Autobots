package com.autobots.automanager.validar;

import java.util.List;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.TelefoneRepositorio;

@Component
public class TelefoneValidar implements Validar<TelefoneDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private List<String> erros;
    private TelefoneRepositorio repositorio;

    public TelefoneValidar(List<String> erros,
            TelefoneRepositorio repositorio) {
        this.erros = erros;
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(TelefoneDTO entity) {
        erros.clear();

        if (entity.getId() != null) {
            if (repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Telefone não cadastrado;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getNumero())) {
            erros.add("- Sem Numero;");
        }

        return erros;
    }
}
