package com.autobots.automanager.converter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

import com.autobots.automanager.dto.CredencialDTO;
import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.entidades.Credencial;
import com.autobots.automanager.entidades.CredencialCodigoBarra;
import com.autobots.automanager.entidades.CredencialUsuarioSenha;
import com.autobots.automanager.entidades.Usuario;

@Component
public class UsuarioConverter implements Converter<Usuario, UsuarioDTO> {

    private final ModelMapper modelMapper;
    private TypeMap<UsuarioDTO, Usuario> propertyMapperDto;
    private TypeMap<Usuario, UsuarioDTO> propertyMapperEntity;

    public UsuarioConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        initializeMapper();
    }

    private void initializeMapper() {
        if (propertyMapperDto == null) {
            propertyMapperDto = modelMapper.createTypeMap(UsuarioDTO.class, Usuario.class);
            propertyMapperDto.addMappings(mapper -> mapper.skip(Usuario::setCredenciais));
        }

        if (propertyMapperEntity == null) {
            propertyMapperEntity = modelMapper.createTypeMap(Usuario.class, UsuarioDTO.class);
        }
    }

    private Credencial mapearCredencial(CredencialDTO dto) {
        Credencial credencial = null;

        if (dto.getNomeUsuario() != null) {
            CredencialUsuarioSenha c;
            c = new CredencialUsuarioSenha();
            c.setNomeUsuario(dto.getNomeUsuario());
            c.setSenha(dto.getSenha());
            credencial = c;
        } else if (dto.getCodigo() > 0) {
            CredencialCodigoBarra c;
            c = new CredencialCodigoBarra();
            c.setCodigo(dto.getCodigo());
            credencial = c;
        }

        if (credencial != null) {
            credencial.setId(dto.getId());
            credencial.setInativo(dto.isInativo());
            credencial.setUltimoAcesso(dto.getUltimoAcesso());
        }

        return credencial;
    }

    @Override
    public Usuario convertToEntity(UsuarioDTO dto) {
        initializeMapper();
        Usuario usuario = modelMapper.map(dto, Usuario.class);

        if (dto.getCredenciais() != null) {
            Set<Credencial> credenciais = dto.getCredenciais().stream()
                    .map(this::mapearCredencial)
                    .collect(Collectors.toSet());
            usuario.setCredenciais(credenciais);
        }

        return usuario;
    }

    @Override
    public UsuarioDTO convertToDto(Usuario entity) {
        initializeMapper();
        UsuarioDTO dto = modelMapper.map(entity, UsuarioDTO.class);

        if (entity.getCredenciais() != null) {
            Set<CredencialDTO> credenciais = entity.getCredenciais().stream()
                    .map(c -> {
                        CredencialDTO cdto = new CredencialDTO();
                        cdto.setId(c.getId());
                        cdto.setInativo(c.isInativo());
                        cdto.setUltimoAcesso(c.getUltimoAcesso());
                        if (c instanceof CredencialUsuarioSenha e) {
                            cdto.setNomeUsuario(e.getNomeUsuario());
                            cdto.setSenha(e.getSenha());
                        } else if (c instanceof CredencialCodigoBarra e) {
                            cdto.setCodigo(e.getCodigo());
                        }
                        return cdto;
                    })
                    .collect(Collectors.toSet());
            dto.setCredenciais(credenciais);
        }

        return dto;
    }

    @Override
    public List<UsuarioDTO> convertToDto(List<Usuario> entities) {
        initializeMapper();
        return modelMapper.map(entities, new TypeToken<List<UsuarioDTO>>() {
        }.getType());
    }
}
