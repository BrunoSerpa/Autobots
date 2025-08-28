package com.autobots.automanager.converter;

import com.autobots.automanager.dto.UsuarioDTO;
import com.autobots.automanager.entidades.Usuario;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

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
        }

        if (propertyMapperEntity == null) {
            propertyMapperEntity = modelMapper.createTypeMap(Usuario.class, UsuarioDTO.class);
        }
    }

    @Override
    public Usuario convertToEntity(UsuarioDTO dto) {
        initializeMapper();
        return modelMapper.map(dto, Usuario.class);
    }

    @Override
    public UsuarioDTO convertToDto(Usuario entity) {
        initializeMapper();
        return modelMapper.map(entity, UsuarioDTO.class);
    }

    @Override
    public List<UsuarioDTO> convertToDto(List<Usuario> entities) {
        initializeMapper();
        return modelMapper.map(entities, new TypeToken<List<UsuarioDTO>>() {
        }.getType());
    }
}
