package com.example.spring_security.library.api.v1.mapper;

import com.example.spring_security.library.api.v1.dto.request.BookRequest;
import com.example.spring_security.library.api.v1.dto.response.BookResponse;
import com.example.spring_security.library.api.v1.entity.Book;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface BookMapper {

    /**
     * Maps BookRequest to Book entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Book toEntity(BookRequest request);

    /**
     * Maps Book entity to BookResponse DTO
     */
    BookResponse toResponse(Book book);

    /**
     * Maps list of Books to list of BookResponses
     */
    List<BookResponse> toResponseList(List<Book> books);

    /**
     * Updates existing Book entity with BookRequest data
     * Only updates fields that are not null
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget Book book, BookRequest request);
}
