package com.autobots.automanager.converter;

import com.autobots.automanager.dto.EnderecoDTO;
import com.autobots.automanager.entidades.Endereco;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

@Component
public class EnderecoConverter implements Converter<Endereco, EnderecoDTO> {
    private final ModelMapper modelMapper;
    private TypeMap<EnderecoDTO, Endereco> propertyMapperDto;
    private TypeMap<Endereco, EnderecoDTO> propertyMapperEntity;

    public EnderecoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        initializeMapper();
    }

    private void initializeMapper() {
        if (propertyMapperDto == null) {
            propertyMapperDto = modelMapper.createTypeMap(EnderecoDTO.class, Endereco.class);
        }

        if (propertyMapperEntity == null) {
            propertyMapperEntity = modelMapper.createTypeMap(Endereco.class, EnderecoDTO.class);
        }
    }

    @Override
    public Endereco convertToEntity(EnderecoDTO dto) {
        initializeMapper();
        return modelMapper.map(dto, Endereco.class);
    }

    @Override
    public EnderecoDTO convertToDto(Endereco entity) {
        initializeMapper();
        return modelMapper.map(entity, EnderecoDTO.class);
    }

    @Override
    public List<EnderecoDTO> convertToDto(List<Endereco> entities) {
        initializeMapper();
        return modelMapper.map(entities, new TypeToken<List<EnderecoDTO>>() {
        }.getType());
    }
}
