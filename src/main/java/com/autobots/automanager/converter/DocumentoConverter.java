package com.autobots.automanager.converter;

import com.autobots.automanager.dto.DocumentoDTO;
import com.autobots.automanager.entidades.Documento;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Component;

@Component
public class DocumentoConverter implements Converter<Documento, DocumentoDTO> {
    private final ModelMapper modelMapper;
    private TypeMap<DocumentoDTO, Documento> propertyMapperDto;
    private TypeMap<Documento, DocumentoDTO> propertyMapperEntity;

    public DocumentoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
        initializeMapper();
    }

    private void initializeMapper() {
        if (propertyMapperDto == null) {
            propertyMapperDto = modelMapper.createTypeMap(DocumentoDTO.class, Documento.class);
        }

        if (propertyMapperEntity == null) {
            propertyMapperEntity = modelMapper.createTypeMap(Documento.class, DocumentoDTO.class);
        }
    }

    @Override
    public Documento convertToEntity(DocumentoDTO dto) {
        initializeMapper();
        return modelMapper.map(dto, Documento.class);
    }

    @Override
    public DocumentoDTO convertToDto(Documento entity) {
        initializeMapper();
        return modelMapper.map(entity, DocumentoDTO.class);
    }

    @Override
    public List<DocumentoDTO> convertToDto(List<Documento> entities) {
        initializeMapper();
        return modelMapper.map(entities, new TypeToken<List<DocumentoDTO>>() {
        }.getType());
    }
}
