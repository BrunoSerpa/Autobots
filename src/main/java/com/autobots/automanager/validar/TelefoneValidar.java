package com.autobots.automanager.validar;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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

        Map<String, Supplier<String>> campos = Map.of(
                "DDD", entity::getDdd,
                "Numero", entity::getNumero);

        campos.forEach((nome, fornecedor) -> {
            String valor = fornecedor.get();
            if (NULO.verificar(valor)) {
                erros.add("- Sem " + nome + ";");
            }
        });

        return erros;
    }
}
