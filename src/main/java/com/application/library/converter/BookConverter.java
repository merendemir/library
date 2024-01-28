package com.application.library.converter;

import com.application.library.data.dto.CreateBookRequestDto;
import com.application.library.data.dto.SaveBookRequestDto;
import com.application.library.exception.EntityNotFoundException;
import com.application.library.model.Book;
import com.application.library.model.Shelf;
import com.application.library.repository.ShelfRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.Query;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {}
)
public abstract class BookConverter {

    @Autowired
    private ShelfRepository shelfRepository;


    @Mapping(target = "shelf", source = "dto.shelfId", qualifiedByName = "idToShelf")
    public abstract Book toEntity(CreateBookRequestDto dto);

    public abstract Book updateEntity(SaveBookRequestDto dto, @MappingTarget Book book);

    @Named("idToShelf")
    Shelf idToShelf(Long id) {
        return id == null ? null : shelfRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
