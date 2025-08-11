package com.autobots.automanager.validar;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Component
public class EnderecoValidar implements Validar<EnderecoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private List<String> erros;
    private EnderecoRepositorio repositorio;

    public EnderecoValidar(List<String> erros,
            EnderecoRepositorio repositorio) {
        this.erros = erros;
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(EnderecoDTO entity) {
        erros.clear();

        if (entity.getId() != null) {
            if (repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Endereço não cadastrado;");
            }
            return erros;
        }

        Map<String, Supplier<String>> campos = Map.of(
                "Cidade", entity::getCidade,
                "Rua", entity::getRua,
                "Numero", entity::getNumero,
                "Código Postal", entity::getCodigoPostal);

        campos.forEach((nome, fornecedor) -> {
            String valor = fornecedor.get();
            if (NULO.verificar(valor)) {
                erros.add("- Sem " + nome + ";");
            }
        });
        return erros;
    }
}
