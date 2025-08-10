package com.autobots.automanager.converter;

import com.autobots.automanager.dto.TelefoneDTO;
import com.autobots.automanager.entidades.Telefone;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

@Component
public class TelefoneConverter implements Converter<Telefone, TelefoneDTO> {
    private final ModelMapper modelMapper;
    private TypeMap<TelefoneDTO, Telefone> propertyMapperDto;
    private TypeMap<Telefone, TelefoneDTO> propertyMapperEntity;

    public TelefoneConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        initializeMapper();
    }

    private void initializeMapper() {
        if (propertyMapperDto == null) {
            propertyMapperDto = modelMapper.createTypeMap(TelefoneDTO.class, Telefone.class);
        }

        if (propertyMapperEntity == null) {
            propertyMapperEntity = modelMapper.createTypeMap(Telefone.class, TelefoneDTO.class);
        }
    }

    @Override
    public Telefone convertToEntity(TelefoneDTO dto) {
        initializeMapper();
        return modelMapper.map(dto, Telefone.class);
    }

    @Override
    public TelefoneDTO convertToDto(Telefone entity) {
        initializeMapper();
        return modelMapper.map(entity, TelefoneDTO.class);
    }

    @Override
    public List<TelefoneDTO> convertToDto(List<Telefone> entities) {
        initializeMapper();
        return modelMapper.map(entities, new TypeToken<List<TelefoneDTO>>() {
        }.getType());
    }
}
