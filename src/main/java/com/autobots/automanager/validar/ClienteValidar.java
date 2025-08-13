package com.autobots.automanager.validar;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Component
public class ClienteValidar implements Validar<ClienteDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private ClienteRepositorio repositorio;
    private DocumentoValidar validarDocumento;
    private EnderecoValidar validarEndereco;
    private TelefoneValidar validarTelefone;

    public ClienteValidar(ClienteRepositorio repositorio,
            DocumentoValidar validarDocumento,
            EnderecoValidar validarEndereco,
            TelefoneValidar validarTelefone) {
        this.repositorio = repositorio;
        this.validarDocumento = validarDocumento;
        this.validarEndereco = validarEndereco;
        this.validarTelefone = validarTelefone;
    }

    @Override
    public List<String> verificar(ClienteDTO entity) {
        List<String> erros = new ArrayList<>();

        if (entity.getId() != null && !repositorio.findById(entity.getId()).isPresent())
            erros.add("- Cliente não cadastrado;");
        else if (NULO.verificar(entity.getNome()))
            erros.add("- Sem Nome;");

        if (entity.getEndereco() != null) {
            List<String> errosEndereco = validarEndereco.verificar(entity.getEndereco());
            if (!errosEndereco.isEmpty()) {
                erros.add("- Endereço:");
                errosEndereco.forEach(erro -> erros.add(" " + erro));
            }
        }

        if (entity.getDocumentos() != null)
            validarDocumento.verificar(entity.getDocumentos()).forEach(erro -> erros.add(" " + erro));

        if (entity.getTelefones() != null)
            validarTelefone.verificar(entity.getTelefones()).forEach(erro -> erros.add(" " + erro));

        return erros;
    }

    @Override
    public List<String> verificar(List<ClienteDTO> entities) {
        List<String> erros = new ArrayList<>();

        for (int index = 0; entities.size() > index; index++) {
            List<String> erroCliente = verificar(entities.get(index));
            if (!erroCliente.isEmpty()) {
                erros.add("- " + (index + 1) + "º Cliente:");
                erros.addAll(erroCliente);
            }
        }

        return erros;

    }
}
