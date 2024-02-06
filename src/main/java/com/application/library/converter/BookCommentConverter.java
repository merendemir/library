package com.application.library.converter;

import com.application.library.data.dto.BookCommentRequestDto;
import com.application.library.helper.AuthHelper;
import com.application.library.model.Book;
import com.application.library.model.BookComment;
import com.application.library.model.User;
import com.application.library.service.BookService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL,
        collectionMappingStrategy = CollectionMappingStrategy.SETTER_PREFERRED,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {}
)
public abstract class BookCommentConverter {

    @Autowired
    private BookService bookService;


    @Mapping(target = "book", source = "bookId", qualifiedByName = "idToBook")
    @Mapping(target = "user", source = "bookId", qualifiedByName = "getAuthUser")
    public abstract BookComment toEntity(Long bookId, BookCommentRequestDto dto);

    public abstract BookComment updateEntity(BookCommentRequestDto dto, @MappingTarget BookComment entity);


    @Named("idToBook")
    Book idToBook(Long id) {
        return id == null ? null : bookService.findById(id);
    }

    @Named("getAuthUser")
    User getAuthUser(Long id) {
        return AuthHelper.getActiveUser();
    }

}
