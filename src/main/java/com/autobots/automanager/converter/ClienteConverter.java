package com.autobots.automanager.converter;

import com.autobots.automanager.dto.ClienteDTO;
import com.autobots.automanager.entidades.Cliente;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

@Component
public class ClienteConverter implements Converter<Cliente, ClienteDTO> {
    private final ModelMapper modelMapper;
    private TypeMap<ClienteDTO, Cliente> propertyMapperDto;
    private TypeMap<Cliente, ClienteDTO> propertyMapperEntity;

    public ClienteConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        initializeMapper();
    }

    private void initializeMapper() {
        if (propertyMapperDto == null) {
            propertyMapperDto = modelMapper.createTypeMap(ClienteDTO.class, Cliente.class);
        }

        if (propertyMapperEntity == null) {
            propertyMapperEntity = modelMapper.createTypeMap(Cliente.class, ClienteDTO.class);
        }
    }

    @Override
    public Cliente convertToEntity(ClienteDTO dto) {
        initializeMapper();
        return modelMapper.map(dto, Cliente.class);
    }

    @Override
    public ClienteDTO convertToDto(Cliente entity) {
        initializeMapper();
        return modelMapper.map(entity, ClienteDTO.class);
    }

    @Override
    public List<ClienteDTO> convertToDto(List<Cliente> entities) {
        initializeMapper();
        return modelMapper.map(entities, new TypeToken<List<ClienteDTO>>() {
        }.getType());
    }
}
