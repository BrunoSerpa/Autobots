package com.autobots.automanager.validar;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;

@Component
public class DocumentoValidar implements Validar<DocumentoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private List<String> erros;

    public DocumentoValidar(List<String> erros) {
        this.erros = erros;
    }

    @Override
    public List<String> verificar(DocumentoDTO entity) {
        erros.clear();

        Map<String, Supplier<String>> campos = Map.of(
                "Tipo", entity::getTipo,
                "Numero", entity::getNumero);

        campos.forEach((nome, fornecedor) -> {
            String valor = fornecedor.get();
            if (NULO.verificar(valor)) {
                erros.add("- " + nome + ";");
            }
        });

        return erros;
    }
}
