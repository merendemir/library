package com.application.library.converter;

import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.data.dto.SaveShelfRequestDto;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.model.Book;
import com.application.library.model.Shelf;
import com.application.library.repository.ShelfRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {}
)
public abstract class ShelfConverter {


    public abstract Shelf toEntity(SaveShelfRequestDto dto);

    public abstract Shelf updateEntity(SaveShelfRequestDto dto, @MappingTarget Shelf shelf);

}
