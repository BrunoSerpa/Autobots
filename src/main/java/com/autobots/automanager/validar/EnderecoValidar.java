package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.repositorios.EnderecoRepositorio;

@Component
public class EnderecoValidar implements Validar<EnderecoDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private EnderecoRepositorio repositorio;

    public EnderecoValidar(EnderecoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public List<String> verificar(EnderecoDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Endereço não cadastrado;");
            }
            return erros;
        }

        Map<String, Supplier<String>> campos = Map.of(
                "Cidade", entity::getCidade,
                "Rua", entity::getRua,
                "Numero", entity::getNumero);

        campos.forEach((nome, fornecedor) -> {
            String valor = fornecedor.get();
            if (NULO.verificar(valor)) {
                erros.add("- Sem " + nome + ";");
            }
        });
        return erros;
    }

    @Override
    public List<String> verificar(List<EnderecoDTO> entities) {
        List<String> erros = new ArrayList<>();

        for (int index = 0; entities.size() > index; index++) {
            List<String> erroEndereco = verificar(entities.get(index));
            if (!erroEndereco.isEmpty()) {
                erros.add("- " + (index + 1) + "º Endereço:");
                erros.addAll(erroEndereco);
            }

        }

        return erros;
    }
}
