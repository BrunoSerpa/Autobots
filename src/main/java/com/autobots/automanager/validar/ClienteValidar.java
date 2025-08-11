package com.autobots.automanager.validar;

import java.util.List;
import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.modelo.StringVerificadorNulo;
import com.autobots.automanager.repositorios.ClienteRepositorio;

@Component
public class ClienteValidar implements Validar<ClienteDTO> {
    private static final StringVerificadorNulo NULO = new StringVerificadorNulo();

    private List<String> erros;
    private ClienteRepositorio repositorio;
    private DocumentoValidar validarDocumento;
    private EnderecoValidar validarEndereco;
    private TelefoneValidar validarTelefone;

    public ClienteValidar(List<String> erros,
            ClienteRepositorio repositorio,
            DocumentoValidar validarDocumento,
            EnderecoValidar validarEndereco,
            TelefoneValidar validarTelefone) {
        this.erros = erros;
        this.repositorio = repositorio;
        this.validarDocumento = validarDocumento;
        this.validarEndereco = validarEndereco;
        this.validarTelefone = validarTelefone;
    }

    @Override
    public List<String> verificar(ClienteDTO entity) {
        erros.clear();

        if (entity.getId() != null) {
            if (!repositorio.findById(entity.getId()).isPresent()) {
                erros.add("- Cliente não cadastrado;");
            }
            return erros;
        }

        if (NULO.verificar(entity.getNome()))
            erros.add("- Sem Nome;");
        
        if (entity.getDataNascimento() == null || NULO.verificar(entity.getDataNascimento().toString()))
            erros.add("- Sem Data Cadastro;");


        List<String> errosEndereco = validarEndereco.verificar(entity.getEndereco());
        if (!errosEndereco.isEmpty()) {
            erros.add("- Endereco:");
            errosEndereco.forEach(erro -> erros.add(" " + erro));
        }

        for (int index = 0; index < entity.getTelefones().size(); index++) {
            List<String> errosTelefone = validarTelefone.verificar(entity.getTelefones().get(index));
            if (!errosTelefone.isEmpty()) {
                erros.add("- " + (index + 1) + "º Telefone:");
                errosTelefone.forEach(erro -> erros.add(" " + erro));
            }
        }

        for (int index = 0; index < entity.getDocumentos().size(); index++) {
            List<String> errosTelefone = validarDocumento.verificar(entity.getDocumentos().get(index));
            if (!errosTelefone.isEmpty()) {
                erros.add("- " + (index + 1) + "º Documento:");
                errosTelefone.forEach(erro -> erros.add(" " + erro));
            }
        }

        return erros;
    }
}
